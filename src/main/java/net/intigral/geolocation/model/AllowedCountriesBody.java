package net.intigral.geolocation.model;

public final class AllowedCountriesBody {
	private String[] allowedCountries;

	public AllowedCountriesBody() {}

	public AllowedCountriesBody(final String[] allowedCountries) {
		this.allowedCountries = allowedCountries;
	}

	public String[] getAllowedCountries() {
		return allowedCountries;
	}

	public void setAllowedCountries(final String[] allowedCountries) {
		this.allowedCountries = allowedCountries;
	}
}
