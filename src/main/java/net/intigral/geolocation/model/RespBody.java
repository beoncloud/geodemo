package net.intigral.geolocation.model;

public final class RespBody {
	private String userIP;
	private String countryCode;
	private String countryName;

	public RespBody() { }

	public RespBody(final String userIP, final String countryCode, final String countryName) {
		this.userIP = userIP;
		this.countryCode = countryCode;
		this.countryName = countryName;
	}

	public String getUserIP() {
		return userIP;
	}

	public void setUserIP(final String userIP) {
		this.userIP = userIP;
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
