package net.intigral.geolocation.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqRespHandler;
import org.rapidoid.http.Resp;

import java.time.LocalDateTime;
import java.time.ZoneId;

public final class GmtTimeHandler implements ReqRespHandler {
	private static final Logger log = LogManager.getLogger(AllowedCountriesHandler.class);

	@Override public Object execute(final Req req, final Resp resp) throws Exception {
		String clientIP =
			req.header("CLIENT_IP",
				req.header("X-Forwarded-For",
					req.clientIpAddress()));
		log.info("serving gmt time requested from clientIP: {}", clientIP);
		resp.body(MyLocalLocationHandler.mapper.writeValueAsBytes(new GmtResp(
			LocalDateTime.now().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli())));
		resp.done();
		return resp;
	}

	static class GmtResp {
		long data;

		public GmtResp() {}

		public GmtResp(final long data) {
			this.data = data;
		}
	}
}
