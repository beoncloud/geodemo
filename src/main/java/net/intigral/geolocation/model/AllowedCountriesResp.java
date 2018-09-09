package net.intigral.geolocation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class AllowedCountriesResp {
	private AllowedCountriesBody data;
	private ErrResp error;

	public AllowedCountriesResp() {}

	public AllowedCountriesResp(final AllowedCountriesBody data) {
		this.data = data;
	}

	public AllowedCountriesResp(final ErrResp error) {
		this.error = error;
	}

	public AllowedCountriesBody getData() {
		return data;
	}

	public void setData(final AllowedCountriesBody data) {
		this.data = data;
	}

	public ErrResp getError() {
		return error;
	}

	public void setError(final ErrResp error) {
		this.error = error;
	}
}
