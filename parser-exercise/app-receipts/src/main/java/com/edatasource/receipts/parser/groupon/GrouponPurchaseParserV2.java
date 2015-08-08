package com.edatasource.receipts.parser.groupon;

import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.edatasource.receipts.model.Address;
import com.edatasource.receipts.model.ecommerce.EcommerceItem;
import com.edatasource.receipts.model.ecommerce.EcommerceReceipt;
import com.edatasource.receipts.model.ecommerce.EcommerceShipment;
import com.edatasource.receipts.parser.AddressParser;

public class GrouponPurchaseParserV2{

	public static EcommerceReceipt parse(Document doc)  {
		EcommerceReceipt receipt = new EcommerceReceipt();
		String orderNumerText =doc.select("strong").get(0).text();
		if(orderNumerText.contains("Your order number is") ){
			String orderNumber = orderNumerText.replace("Your order number is","").trim();
			receipt.setOrderNumber(orderNumber);
		}
		
		String tdText= null;
		ArrayList<String> orderInfo = new ArrayList<String>();
		
		Elements elements= doc.select("td");	
		for(Element element : elements){
			tdText= element.text();
			if(tdText ==null) continue;
			tdText= tdText.trim();
			if(tdText.toLowerCase().startsWith("order placed on")){

				Element table = element.parent().parent(); // parent table
				for(Element td : table.getAllElements()){
					
					tdText = td.text().trim();
					if(td.childNodes().size()==1 && tdText.length()>0){
						orderInfo.add(tdText);
					}
				}
				break;
			}
		}
		
		for(int i = 0; i < orderInfo.size(); i++){
			String text = orderInfo.get(i).toLowerCase();
			
			if(text.startsWith("subtotal")){
				try{
					double subTotal = Double.parseDouble(orderInfo.get(i+1).replace('$', ' '));
					
					receipt.setOrderSubTotal(subTotal);
					
					for(int j = 2; j<i; j+=2){
						
						EcommerceShipment eship = new EcommerceShipment();
						EcommerceItem item = new EcommerceItem();
						item.setDescription(orderInfo.get(j).trim());
						
						try{
							int quality = Integer.parseInt(orderInfo.get(j+1).replace("(", "").replace("x", "").replace(")", ""));
							item.setQuantity(quality);
						}catch(Exception ex1){}
						
						eship.addItem(item);
						receipt.addShipment(eship);
						
					}
				}catch(Exception ex) {}
			}
			
			if(text.startsWith("tax")){
				try{
					double orderTax =Double.parseDouble(orderInfo.get(i+1));
					receipt.setOrderTax(orderTax);
				}catch(Exception ex) {}
			}
			
			if(text.contains("shipping") || text.contains("handling")){
				try{
					double amount = Double.parseDouble(orderInfo.get(i+1));
					receipt.setShippingAmount(amount);
				}catch(Exception ex) {}
			}
			
			if(text.startsWith("total")){
				try{
					double orderTotal = Double.parseDouble(orderInfo.get(i+1).replace('$', ' '));
					receipt.setOrderTotal(orderTotal);
				}catch(Exception ex) {}
			}
			
			if(text.contains("visa ending in")){
				try{
					String []sCredit = orderInfo.get(i).split("-");
					int orderCredit = Integer.parseInt(sCredit[1]);
					receipt.setOrderCredit((double)orderCredit);
				}catch(Exception ex) {}
			}
			
			if(text.contains("shipping address")){
				try{
					String []parserName = orderInfo.get(i+1).split(" ");
					String originalLocation = orderInfo.get(i+2) + ", "+ orderInfo.get(i+3);
					
					Address billing = AddressParser.parse(originalLocation);
					billing.setFirstName(parserName[0].trim());
					billing.setLastName(parserName[1].trim());
					receipt.setBilling(billing);
					
				}catch(Exception ex) {
					System.out.println(ex.getMessage());
				}
			}
			
		}
		
		return receipt;
	}
	

}
