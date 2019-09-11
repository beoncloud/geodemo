package net.intigral.geolocation.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.intigral.geolocation.model.Country;
import org.apache.commons.net.util.SubnetUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;

import java.io.*;
import java.nio.file.Path;

public final class CSVIngester {
	private static final Logger log = LogManager.getLogger(CSVIngester.class);

	private final Int2ObjectOpenHashMap<Country> countryMap = new Int2ObjectOpenHashMap<>(255);
	private final Country[]                      allIPBlocks;

	public CSVIngester(final Path countryListFile, final Path ipListFile) {
		readGeonames(countryListFile);
		int lineNum = 1;
		try (final var is = new FileInputStream(ipListFile.toFile());
			 final var isr = new InputStreamReader(is);
			 final var br = new BufferedReader(isr);)
		{
			final var ipCountries = new ObjectArrayList<Country>();
			var       line        = br.readLine(); //ignoring header line
			while ((line = br.readLine()) != null) {
				final var tokens         = line.split(",");
				final var ipBlock        = tokens[0];
				var       geonameCountry = tokens[1];
				if (geonameCountry == null || geonameCountry.length() == 0) {
					geonameCountry = tokens[2];
					if (geonameCountry == null || geonameCountry.length() == 0) {
						log.warn("skipping cidr block: {} near line: {} because no country info was found",
							ipBlock, Unbox.box(lineNum));
						continue;
					}
				}
				final var geonameId = Integer.parseInt(geonameCountry);
				final var cidr      = new SubnetUtils(ipBlock);
				cidr.setInclusiveHostCount(true);
				final var lowLongIP     = ipToLong(cidr.getInfo().getLowAddress());
				final var highLongIP    = ipToLong(cidr.getInfo().getHighAddress());
				final var mappedCountry = countryMap.get(geonameId);
				if (mappedCountry == null) {
					throw new RuntimeException(String.format("country with geonameId: %d not found", geonameId));
				}
				final var country = new Country(
					mappedCountry.getGeonameId(),
					mappedCountry.getCountryISOCode(),
					mappedCountry.getCountryName());
				country.setCidr(ipBlock);
				country.setLowIP(lowLongIP);
				country.setHighIP(highLongIP);
				ipCountries.add(country);
				lineNum += 1;
			}
			log.info("found {} ip blocks with geonamed countries", Unbox.box(ipCountries.size()));
			allIPBlocks = ipCountries.toArray(new Country[0]);
			log.info("successfully prepared an array of all ip blocks with their countries...");
		}
		catch (Exception e) {
			log.error("caught exception while reading ip blocks list: ", e);
			throw new RuntimeException(e);
		}
	}

	private void readGeonames(final Path countryListFile) {
		try (final var is = new FileInputStream(countryListFile.toFile());
			 final var isr = new InputStreamReader(is);
			 final var br = new BufferedReader(isr);)
		{
			var line = br.readLine(); //ignoring header line
			while ((line = br.readLine()) != null) {
				final var tokens         = line.split(",");
				final var geonameId      = Integer.parseInt(tokens[0]);
				final var countryISOCode = tokens[4];
				final var countryName    = tokens[5].replaceAll("\"", "");
				final var country        = new Country(geonameId, countryISOCode, countryName);
				countryMap.put(geonameId, country);
			}
		}
		catch (Exception e) {
			log.error("caught exception while reading country list: ", e);
			throw new RuntimeException(e);
		}
		log.info("loaded {} countries in country mapping", Unbox.box(countryMap.size()));
	}

	public Country[] getAllIPBlocks() {
		return this.allIPBlocks;
	}

	public static void main1(String[] args) {
		final var cidr        = "1.0.16.0/20";
		final var subnetUtils = new SubnetUtils(cidr);
		subnetUtils.setInclusiveHostCount(true);
		final var lowAddr = subnetUtils.getInfo().getLowAddress();
		System.out.println(lowAddr);
		System.out.println(ipToLong(lowAddr));
		final var highAddr = subnetUtils.getInfo().getHighAddress();
		System.out.println(highAddr);
		System.out.println(ipToLong(highAddr));
	}

	public static long ipToLong(String ipAddress) {
		long      result           = 0;
		final var ipAddressInArray = ipAddress.split("\\.");
		for (int i = 3; i >= 0; i--) {
			long ip = Long.parseLong(ipAddressInArray[3 - i]);
			result |= ip << (i * 8);
		}
		return result;
	}
}
