package com.edatasource.receipts.parser.groupon;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;

import com.edatasource.receipts.model.Address;
import com.edatasource.receipts.model.ecommerce.EcommerceItem;
import com.edatasource.receipts.model.ecommerce.EcommerceReceipt;
import com.edatasource.receipts.model.ecommerce.EcommerceShipment;

public class GrouponPurchaseParserV2{
	public static Pattern STATES = Pattern
			.compile("(?i)(Alabama|Alaska|Arizona|Arkansas|California|Colorado|Connecticut|Delaware|Florida|Georgia|Hawaii|Idaho|Illinois|Indiana|Iowa|Kansas|Kentucky|Louisiana|"
					+ "Maine|Maryland|Massachusetts|Michigan|Minnesota|Mississippi|Missouri|Montana|Nebraska|Nevada|New Hampshire|New Jersey|New Mexico|New York|North Carolina|North Dakota|"
					+ "Ohio|Oklahoma|Oregon|Pennsylvania|Rhode Island|South Carolina|South Dakota|Tennessee|Texas|Utah|Vermont|Virginia|Washington|West Virginia|Wisconsin|Wyoming)");
	public static EcommerceReceipt parse(Document doc)  {
		long orderNumer = 0;
		String orderDateInString = "";
		String firstName = "";
		String lastName = "";
		String state = "";
		String post = "";
		String street = "";
		String city = "";
		double subtotal = 0;
		double tax = 0;
		double shipHan = 0;
		double total = 0;
		List<Item> itemsList = new LinkedList<>();

		//here started to matching
		String sample = doc.body().text();
		Pattern p = Pattern.compile("Your order number is (\\d+)");
		Matcher m = p.matcher(sample);

		while (m.find()) {
			orderNumer = Long.valueOf(m.group(1));
			System.out.println(m.group(1));
		}
		
		p = Pattern.compile("Order placed on (\\w+\\s\\d,\\s\\d{4})");
		m = p.matcher(sample);
		while (m.find()) {
			orderDateInString = m.group(1);
			System.out.println(m.group(1));
		}
		
		p = Pattern.compile("\\w+\\s\\d,\\s\\d{4} (.*?) Subtotal");
		m = p.matcher(sample);
		while (m.find()) {
			String itemsString = m.group(1);
			String[] items = itemsString.split("\\(x\\d\\)");
			for (String ss : items) {
				Item i = new Item();
				i.setDescription(ss.trim());
				itemsList.add(i);
				System.out.println(ss.trim());
				itemsString = itemsString.replaceAll(ss.trim(), "");
			}

			itemsString = itemsString.trim();
			p = Pattern.compile("\\(x(\\d+)\\)");
			m = p.matcher(itemsString);
			int counter = 0;
			while (m.find()) {
				itemsList.get(counter)
						.setQuantity(Integer.parseInt(m.group(1)));
				counter++;
				System.out.println(m.group(1));
			}
			
			p = Pattern.compile("Subtotal \\$([0-9]+[,.][0-9]{2}?)");
			m = p.matcher(sample);
			while (m.find()) {
				subtotal = Double.parseDouble(m.group(1).trim());
				System.out.println(m.group(1));
			}
			
			p = Pattern.compile("Tax ([0-9]+[,.][0-9]{2}?)");
			m = p.matcher(sample);
			while (m.find()) {
				tax = Double.parseDouble(m.group(1).trim());
				System.out.println(m.group(1));
			}
			
			p = Pattern.compile("Shipping & Handling ([0-9]+[,.][0-9]{2}?)");
			m = p.matcher(sample);
			while (m.find()) {
				shipHan = Double.parseDouble(m.group(1).trim());
				System.out.println(m.group(1));
			}
			
			p = Pattern.compile("Total \\$([0-9]+[,.][0-9]{2}?)");
			m = p.matcher(sample);
			while (m.find()) {
				total = Double.parseDouble(m.group(1).trim());
				System.out.println(m.group(1));
			}
			
			p = Pattern.compile("Shipping address: (.*?) Address incorrect?");
			m = p.matcher(sample);
			while (m.find()) {
				System.out.println(m.group(1).trim());
				String address = m.group(1).trim();
				p = Pattern.compile("^\\w+\\s\\w+");
				m = p.matcher(address);
				while(m.find()){
					//System.out.println(m.group().trim());
					String[] names = m.group().trim().split("\\s");
					if(names.length == 1){
						firstName = names[0];
					}
					if(names.length < 1){
						firstName = names[0];
						lastName = names[1];
					}
				}
				
				p = STATES;
				m = p.matcher(address);
				while (m.find()) {
					state =  m.group();
					System.out.println(state);
				}
				
				p = Pattern.compile("[0-9]+\\-[0-9]+");
				m = p.matcher(address);
				while (m.find()) {
					post =  m.group();
					System.out.println(post);
				}
				
				p =  Pattern.compile("[0-9]+\\s\\w+\\s\\w+");
				m = p.matcher(address);
				while (m.find()) {
					street =  m.group();
					System.out.println(street);
				}
				
				p =  Pattern.compile(street+"(.*?)"+state);
				m = p.matcher(address);
				while (m.find()) {
					city =  m.group(1).trim().replaceAll(",","");
					System.out.println(city);
				}
			}
		}
		
		EcommerceReceipt receipt = new EcommerceReceipt();
		List<EcommerceShipment> ecommerceShipments = new LinkedList<EcommerceShipment>();
		EcommerceShipment shipment = new EcommerceShipment();
		List<EcommerceItem> ecommerceItems = new LinkedList<EcommerceItem>();
		
		for(Item i:itemsList){
			EcommerceItem ecommerceItem = new EcommerceItem();
			ecommerceItem.setDescription(i.getDescription());
			ecommerceItem.setQuantity(i.getQuantity());
			ecommerceItems.add(ecommerceItem);
		}
		
		Address address = new Address();
		address.setCity(city);
		address.setFirstName(firstName);
		address.setLastName(lastName);
		address.setPostalCode(post);
		address.setState(state);
		address.setStreetOrBoxInfo(street);
		
		shipment.setShipping(address);
		shipment.setItems(ecommerceItems);
		
		ecommerceShipments.add(shipment);
		
		receipt.setShipments(ecommerceShipments);
		receipt.setOrderSubTotal(subtotal);
		receipt.setOrderTax(tax);
		receipt.setOrderTotal(total);
		
		return receipt;
	}
}
