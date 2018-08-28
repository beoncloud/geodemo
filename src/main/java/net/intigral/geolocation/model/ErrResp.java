package net.intigral.geolocation.model;

public final class ErrResp {
	private String code;
	private String description;

	public ErrResp() { }

	public ErrResp(final String code, final String description) {
		this.code = code;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(final String code) {
		this.code = code;
	}
}
