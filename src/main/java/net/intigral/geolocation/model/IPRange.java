package net.intigral.geolocation.model;

public final class IPRange {
	private final long start;
	private final long end;

	public IPRange(final long start, final long end) {
		this.start = start;
		this.end = end;
	}

	public long getStart() {
		return start;
	}

	public long getEnd() {
		return end;
	}
}
