package net.intigral.geolocation;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.intigral.geolocation.handler.AllowedCountriesHandler;
import net.intigral.geolocation.handler.GmtTimeHandler;
import net.intigral.geolocation.handler.MyLocalLocationHandler;
import net.intigral.geolocation.handler.MyServedLocationHandler;
import net.intigral.geolocation.util.CSVIngester;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rapidoid.config.Conf;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqRespHandler;
import org.rapidoid.http.Resp;
import org.rapidoid.setup.App;
import org.rapidoid.setup.On;
import org.yaml.snakeyaml.Yaml;

import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

public final class Main {
	static final String COUNTRY_LIST = "GeoLite2-Country-Locations-en.csv";
	static final String IP_LIST      = "GeoLite2-Country-Blocks-IPv4.csv";
	private static final Logger log = LogManager.getLogger(Main.class);
	private static final ObjectMapper mapper = new ObjectMapper();
//	private final RedissonClient redissonClient;

	private Main(Map<String, Map<String, ?>> values) {
		try {
			if (values == null || values.isEmpty()) {
				log.info("loading config.yaml to read app config");

				try (final var is = Thread.currentThread().getContextClassLoader()
										  .getResourceAsStream("application.yml"))
				{
					Yaml yaml = new Yaml();
					values = (Map<String, Map<String, ? extends Object>>) yaml.load(is);
				}
			}

			/*final var redisConfig = (Map<String, String>) values.get("redis");
			final var redisUrl    = redisConfig.get("url");
			final var config      = new Config();
			final var poolSize    = Runtime.getRuntime().availableProcessors() * 2 + 2;
			config
				.useClusterServers()
				.setScanInterval(2000)
				.addNodeAddress(redisUrl)
				.setMasterConnectionPoolSize(poolSize)
				.setMasterConnectionMinimumIdleSize(poolSize)
				.setSlaveConnectionPoolSize(poolSize)
				.setSlaveConnectionMinimumIdleSize(poolSize);

			config.setCodec(new KryoCodec());

			redissonClient = Redisson.create(config);
			log.info("initialized redisson client connection");*/
		}
		catch (Exception e) {
			log.error("caught exception parsing application.yml: ", e);
			throw new RuntimeException("config resolution");
		}
	}

	private static Object setCorsHeaders(final Req req, final Resp resp) throws Exception {
		log.info("Request Headers: {}", req.header("Access-Control-Request-Headers", ""));
		resp.header("Access-Control-Allow-Origin", "*");
		resp.header("Access-Control-Allow-Headers", req.header("Access-Control-Request-Headers", ""));
		log.info("Response Headers: {}", mapper.writeValueAsString(resp.headers()));
		resp.body(mapper.writeValueAsBytes(mapper.createObjectNode().put("status", "accepted")));
		resp.done();
		return resp;
	}

	public static void main(String[] args) {
//		final Main mainConfig = new Main(null);
		bootupServer(null);
	}

	private static void bootupServer(final Main mainConfig) {
		App.profiles("production");
		On.address("0.0.0.0").port(9393);
		Conf.HTTP.set("maxPipeline", 32);
		Conf.NET.set("bufSizeKB", 16);

		log.info("setting up geolocation finder...");
		try {
			var countryFilePath = Optional
				.ofNullable(System.getProperty("mmdb_country"))
				.map(Paths::get)
				.orElseThrow(() -> new IllegalArgumentException("mmdb_country path for country_list not provided"));
			var ipFilePath = Optional
				.ofNullable(System.getProperty("mmdb_ip"))
				.map(Paths::get)
				.orElseThrow(() -> new IllegalArgumentException("mmdb_ip path for ip_list not provided"));

			final var csvIngester = new CSVIngester(countryFilePath, ipFilePath);
			On.get("/v1/myservedlocation").json(new MyServedLocationHandler());
			On.get("/v1/mylocation").json(new MyLocalLocationHandler(csvIngester.getAllIPBlocks()));
			On.options("/v1/mylocation").json((ReqRespHandler) Main::setCorsHeaders);
			On.get("/v1/allowedCountries").json(new AllowedCountriesHandler());
			On.get("/v1/gmtTime").json(new GmtTimeHandler());
		} catch (Exception e) {
			log.error("caught exception while booting up server: ", e);
			System.exit(-1);
		}
	}
}
