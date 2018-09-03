package net.intigral.geolocation.model;

import java.util.Objects;

public final class Country {
	private final int geonameId;
	private final String countryISOCode;
	private final String countryName;
	private long lowIP;
	private long highIP;
	private String cidr;

	public Country(final int geonameId, final String countryISOCode, final String countryName) {
		this.geonameId = geonameId;
		this.countryISOCode = countryISOCode;
		this.countryName = countryName;
	}

	public String getCidr() {
		return cidr;
	}

	public void setCidr(final String cidr) {
		this.cidr = cidr;
	}

	public int getGeonameId() {
		return geonameId;
	}

	public String getCountryISOCode() {
		return countryISOCode;
	}

	public String getCountryName() {
		return countryName;
	}

	public long getLowIP() {
		return lowIP;
	}

	public void setLowIP(final long lowIP) {
		this.lowIP = lowIP;
	}

	public long getHighIP() {
		return highIP;
	}

	public void setHighIP(final long highIP) {
		this.highIP = highIP;
	}

	@Override public boolean equals(final Object o) {
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }
		final Country country = (Country) o;
		return geonameId == country.geonameId;
	}

	@Override public int hashCode() {
		return Objects.hash(geonameId);
	}

	/*@Override public int compareTo(final Country that) {
		if (this.lowIP > that.highIP) { return 1; }
		else if (this.highIP < that.lowIP) { return -1; }
		else { return 0; }
	}*/
}
