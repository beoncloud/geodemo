package net.intigral.geolocation.model;

public final class RespBody {
	private String userIP;
	private String countryCode;
	private String countryName;
	private String txRef;

	public RespBody() { }

	public RespBody(final String userIP, final String countryCode, final String countryName, final String txRef) {
		this.userIP = userIP;
		this.countryCode = countryCode;
		this.countryName = countryName;
		this.txRef = txRef;
	}

	public String getTxRef() {
		return txRef;
	}

	public void setTxRef(final String txRef) {
		this.txRef = txRef;
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
