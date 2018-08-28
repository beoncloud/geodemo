package net.intigral.geolocation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class RespMsg {
	private RespBody data;
	private ErrResp  error;

	public RespMsg() { }

	public RespMsg(final RespBody data) {
		this.data = data;
	}

	public RespMsg(final ErrResp error) {
		this.error = error;
	}

	public RespBody getData() {
		return data;
	}

	public void setData(final RespBody data) {
		this.data = data;
	}

	public ErrResp getError() {
		return error;
	}

	public void setError(final ErrResp error) {
		this.error = error;
	}
}
