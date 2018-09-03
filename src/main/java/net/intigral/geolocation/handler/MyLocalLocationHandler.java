package net.intigral.geolocation.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.RandomBasedGenerator;
import net.intigral.geolocation.model.Country;
import net.intigral.geolocation.model.ErrResp;
import net.intigral.geolocation.model.RespBody;
import net.intigral.geolocation.model.RespMsg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqRespHandler;
import org.rapidoid.http.Resp;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static net.intigral.geolocation.util.CSVIngester.ipToLong;
import static net.intigral.geolocation.util.SearchIP.search;

public final class MyLocalLocationHandler implements ReqRespHandler {
	private static final Logger log = LogManager.getLogger(MyLocalLocationHandler.class);

	private static final RandomBasedGenerator randGen = Generators.randomBasedGenerator();
	private static final ObjectMapper         mapper  = new ObjectMapper();

	private final Country[] allIPBlocks;
	private final byte[]    ERR_SERVER_EX;
	private final byte[]    ERR_INVALID_IP;
	private final byte[]    ERR_LOOPBACK_IP;
	private final byte[]    ERR_NF;

	public MyLocalLocationHandler(final Country[] allIPBlocks) {
		this.allIPBlocks = allIPBlocks;
		try {
			ERR_SERVER_EX = mapper
				.writeValueAsBytes(new RespMsg(new ErrResp("GL_ERR_EX", "server error")));
			ERR_INVALID_IP = mapper
				.writeValueAsBytes(new RespMsg(new ErrResp("GL_CL_IP", "invalid/missing clientIP")));
			ERR_LOOPBACK_IP = mapper
				.writeValueAsBytes(new RespMsg(new ErrResp("GL_CL_LB", "loopback clientIP")));
			ERR_NF = mapper
				.writeValueAsBytes(new RespMsg(new ErrResp("GL_CL_NF", "no country found")));
		}
		catch (Exception e) {
			log.error("caught exception while constructing error msgs: ", e);
			throw new RuntimeException(e);
		}
	}

	@Override public Object execute(final Req req, final Resp resp) throws Exception {
		req.async();
		try {
			String clientIP =
				req.header("CLIENT_IP",
					req.header("X-Forwarded-For",
						req.clientIpAddress()));

			if (clientIP == null || clientIP.length() == 0) {
				log.info("invalid clientIP received: {}, not proceeding", clientIP);
				resp.body(ERR_INVALID_IP);
				resp.done();
				return resp;
			}

			final var inetAddr = InetAddress.getByName(clientIP);
			if (inetAddr.isLoopbackAddress() || inetAddr.isAnyLocalAddress()) {
				log.info("loopback clientIP received: {}, not proceeding", clientIP);
				resp.body(ERR_LOOPBACK_IP);
				resp.done();
				return resp;
			}

			final var txRef = randGen.generate().toString();
			log.info("clientIP: {} requested it's geolocation, txRef: {}", clientIP, txRef);

			final var country = search(ipToLong(clientIP), allIPBlocks);
			if (country != null) {
				log.info("found country: {} for txRef: {}, clientIP: {}",
					country.getCountryName(), txRef, clientIP);
				resp.body(mapper.writeValueAsBytes(
					new RespMsg(new RespBody(clientIP, country.getCountryISOCode(), country.getCountryName()))));
			}
			else {
				log.warn("no country found for txRef: {}, clientIP: {}", txRef, clientIP);
				resp.body(ERR_NF);
			}
		}
		catch (Exception e) {
			if (e.getClass().equals(UnknownHostException.class)) {
				log.warn("unknown host: ", e);
				resp.body(ERR_INVALID_IP);
			}
			log.error("caught exception while resolving geolocation: ", e);
			resp.body(ERR_SERVER_EX);
		}
		resp.done();
		return resp;
	}
}
