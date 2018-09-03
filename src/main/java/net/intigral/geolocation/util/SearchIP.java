package net.intigral.geolocation.util;

import net.intigral.geolocation.model.Country;

import static net.intigral.geolocation.util.CSVIngester.ipToLong;

public final class SearchIP {
	public static Country search(final long ip, final Country[] allIPBlocks) {
		int low = 0;
		int high = allIPBlocks.length - 1;
		while (low <= high) {
			int mid = (low + high) >>> 1;
			final var country = allIPBlocks[mid];
			if (ip < country.getLowIP()) {
				high = mid - 1;
			}
			else if (ip > country.getHighIP()) {
				low = mid + 1;
			}
			else {
				return country;
			}
		}
		return null;
	}

	public static Country search(final String ip, final Country[] allIPCountries) {
		if (ip == null || ip.length() < 7) {
			return null;
		}
		return search(ipToLong(ip), allIPCountries);
	}
}
