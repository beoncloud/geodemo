package net.intigral.geolocation;

import net.intigral.geolocation.handler.AllowedCountriesHandler;
import net.intigral.geolocation.handler.MyLocalLocationHandler;
import net.intigral.geolocation.handler.MyServedLocationHandler;
import net.intigral.geolocation.util.CSVIngester;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rapidoid.config.Conf;
import org.rapidoid.setup.App;
import org.rapidoid.setup.On;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.KryoCodec;
import org.redisson.config.Config;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

public final class Main {
	private static final Logger log = LogManager.getLogger(Main.class);

	static final String COUNTRY_LIST = "GeoLite2-Country-Locations-en.csv";
	static final String IP_LIST      = "GeoLite2-Country-Blocks-IPv4.csv";

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

	private static void bootupServer(final Main mainConfig) {
		App.profiles("production");
		On.address("0.0.0.0").port(9393);
		Conf.HTTP.set("maxPipeline", 32);
		Conf.NET.set("bufSizeKB", 16);

		log.info("setting up geolocation finder...");
		final var csvIngester = new CSVIngester(COUNTRY_LIST, IP_LIST);
		On.get("/v1/myservedlocation").json( new MyServedLocationHandler());
		On.get("/v1/mylocation").json(new MyLocalLocationHandler(csvIngester.getAllIPBlocks()));
		On.get("/v1/allowedCountries").json(new AllowedCountriesHandler());
	}

	public static void main(String[] args) {
//		final Main mainConfig = new Main(null);
		bootupServer(null);
	}
}
