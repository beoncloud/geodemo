package net.intigral.geolocation.model;

public final class RespBody {
	private String ip;
	private String countryCode;
	private String countryName;

	public RespBody() { }

	public RespBody(final String ip, final String countryCode, final String countryName) {
		this.ip = ip;
		this.countryCode = countryCode;
		this.countryName = countryName;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(final String ip) {
		this.ip = ip;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(final String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(final String countryName) {
		this.countryName = countryName;
	}
}
