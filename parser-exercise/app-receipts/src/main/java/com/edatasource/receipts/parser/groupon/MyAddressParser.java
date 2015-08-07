package com.edatasource.receipts.parser.groupon;

import org.jsoup.select.Elements;

import com.edatasource.receipts.model.Address;

public class MyAddressParser {
	
	public static Address parse(Elements wholeAddress) {
		if(wholeAddress == null)
			return null;
		Address address = new Address();
		String[] names = wholeAddress.select("tr > td > table > tbody > tr:eq(0) > td").text().split(" ");
		String firstName = names[0];
		String lastname = names[1];
		address.setFirstName(firstName);
		address.setLastName(lastname);
		
		address.setStreetOrBoxInfo(wholeAddress.select("tr > td > table > tbody > tr:eq(1) > td").text());
		String yard = wholeAddress.select("tr > td > table > tbody > tr:eq(3) > td").text();
		String city = yard.substring(0, yard.indexOf(","));
		
		yard = yard.substring(yard.indexOf(",") + 1).trim();
		String[] countryAndPostal = yard.split(" ");
		address.setState(countryAndPostal[0]);
		address.setPostalCode(countryAndPostal[1]);
		
		address.setCity(city);
		return address;
	}

}
