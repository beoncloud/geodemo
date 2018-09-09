package net.intigral.geolocation.handler;

import net.intigral.geolocation.model.AllowedCountriesBody;
import net.intigral.geolocation.model.AllowedCountriesResp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rapidoid.http.Req;
import org.rapidoid.http.ReqRespHandler;
import org.rapidoid.http.Resp;

public final class AllowedCountriesHandler implements ReqRespHandler {
	private static final Logger log = LogManager.getLogger(AllowedCountriesHandler.class);

	private static final String[] ALLOWED_COUNTRIES;
	private static final byte[] ALC_RESP;

	static {
		try {
			ALLOWED_COUNTRIES = new String[] {
				"AF", "DZ", "BH", "TD", "DJ", "EG", "IQ", "JO", "SA", "KW", "LB", "LY", "MR", "MA", "OM", "PS", "QA",
				"SO", "TN", "AE", "YE"
			};

			ALC_RESP = MyLocalLocationHandler.mapper.writeValueAsBytes(new AllowedCountriesResp(
				new AllowedCountriesBody(ALLOWED_COUNTRIES)
			));
		}
		catch (Exception e) {
			log.error("caught exception while trying to prepare resp: ", e);
			throw new RuntimeException(e);
		}
	}

	@Override public Object execute(final Req req, final Resp resp) throws Exception {
		req.async();
		log.info("sending list of allowed countries");
		resp.body(ALC_RESP);
		resp.done();
		return resp;
	}
}
