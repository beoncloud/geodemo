package net.intigral.geolocation.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.RandomBasedGenerator;
import net.intigral.geolocation.model.ErrResp;
import net.intigral.geolocation.model.ReqRespBody;
import net.intigral.geolocation.model.RespBody;
import net.intigral.geolocation.model.RespMsg;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqRespHandler;
import org.rapidoid.http.Resp;

public final class MyLocationHandler implements ReqRespHandler {
	private static final Logger log = LogManager.getLogger(MyLocationHandler.class);

	private static final RandomBasedGenerator randGen = Generators.randomBasedGenerator();
	private static final ObjectMapper mapper = new ObjectMapper();

//	private final RedissonClient redisson;

//	public MyLocationHandler(final RedissonClient redisson) {
//		this.redisson = redisson;
//	}

	final static String URI = "http://api.ipstack.com/{}?access_key=5fdd984eb0bf1f97d457cdbff41c58ab&format=1";

	@Override public Object execute(final Req req, final Resp resp) throws Exception {
		req.async();
		String clientIP =
			req.header("CLIENT_IP",
				req.header("X-Forwarded-For",
					req.clientIpAddress()));
		if (clientIP == null || clientIP.length() == 0) {
			log.info("invalid clientIP received: {}, not proceeding", clientIP);
			final var ERR_SERVER_EX = mapper.writeValueAsBytes(new RespMsg(new ErrResp("LOC_CLIENT_IP", "invalid/missing clientIP")));
			resp.body(ERR_SERVER_EX);
			resp.done();
			return resp;
		}
		final var txRef = randGen.generate().toString();
		log.info("clientIP: {} requested it's geolocation, txRef: {}", clientIP, txRef);
		//http://api.ipstack.com/91.74.153.30?access_key=5fdd984eb0bf1f97d457cdbff41c58ab&format=1
		try (final var httpClient = HttpClients.createDefault()) {
			final var uri = URI.replace("{}", clientIP);
			final var get = new HttpGet(uri);
			log.info("@ {}", uri);
			try (final var response = httpClient.execute(get)) {
				final var statusCode = response.getStatusLine().getStatusCode();
				log.info(":: {}", Unbox.box(statusCode));
				final var respBodyStr = new BasicResponseHandler().handleEntity(response.getEntity());
				final var respBody = mapper.readValue(respBodyStr, ReqRespBody.class);
				resp.body(mapper.writeValueAsBytes(new RespMsg(new RespBody(respBody.getIp(), respBody.getCountryCode(), respBody.getCountryName()))));
				resp.done();
				return resp;
			}
		}
		catch (Exception e) {
			final var ERR_SERVER_EX = mapper.writeValueAsBytes(new RespMsg(new ErrResp("LOC_SERVER_ERR", "server error")));
			log.error("caught exception while trying to request geolocation: ", e);
			resp.body(ERR_SERVER_EX);
			resp.done();
			return resp;
		}
	}
}