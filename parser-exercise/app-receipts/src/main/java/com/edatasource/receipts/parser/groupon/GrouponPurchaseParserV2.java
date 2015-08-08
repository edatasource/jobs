package com.edatasource.receipts.parser.groupon;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.edatasource.receipts.model.Address;
import com.edatasource.receipts.model.ecommerce.EcommerceItem;
import com.edatasource.receipts.model.ecommerce.EcommerceReceipt;
import com.edatasource.receipts.model.ecommerce.EcommerceShipment;
import com.edatasource.receipts.parser.AddressParser;

import dto.DocumentDto;
import dto.ElementDto;

public class GrouponPurchaseParserV2{

	public static EcommerceReceipt parse(Document doc)  {
		EcommerceReceipt ecommerceReceipt=new EcommerceReceipt();
		EcommerceShipment ecommerceShipment=new EcommerceShipment();
		for(Element element:doc.getElementsContainingOwnText("Shipping address:")){
			Address address=new Address();
			address.setStreetOrBoxInfo(element.parent().nextElementSibling().child(0).child(0).child(0).child(1).child(0).html());
			String[] addressParts=element.parent().nextElementSibling().child(0).child(0).child(0).child(3).child(0).html().split(",");
			address.setCity(addressParts[0]);
			address.setState(addressParts[1].trim().split(" ")[0]);
			address.setPostalCode(addressParts[1].trim().split(" ")[1]);
			ecommerceShipment.setShipping(address);
		}
		
		for(Element element:doc.getElementsContainingOwnText("Order summary:")){
			EcommerceItem item=new EcommerceItem();
			item.setDescription(element.parent().parent().child(2).child(0).ownText());
			item.setQuantity(Integer.parseInt(element.parent().parent().child(2).child(1).ownText().replace("(x", "").replace(")", "")));
			ecommerceShipment.addItem(item);
		}

		ecommerceReceipt.addShipment(ecommerceShipment);
		//Subtotal
		for(Element element:doc.getElementsContainingOwnText("Subtotal")){
			ecommerceReceipt.setOrderSubTotal(Double.parseDouble(element.nextElementSibling().ownText().trim().substring(1)));
		}
		//Total
		for(Element element:doc.getElementsContainingOwnText("Total")){
			ecommerceReceipt.setOrderTotal(Double.parseDouble(element.nextElementSibling().ownText().trim().substring(1)));
		}
		
		return ecommerceReceipt;
	}

	public static DocumentDto parse(String path) {
		DocumentDto documentDto=new DocumentDto();
		Document doc=null;
		try{
			File htmlFile=new File(path);
			doc = Jsoup.parse(htmlFile,Charset.defaultCharset().name());
		} catch(Exception ex){
			return null;
		}
		documentDto.setTitle(doc.title());
		
		if(!doc.head().children().isEmpty()){
			List<ElementDto> elementList=new ArrayList<ElementDto>();
			for(Element element:doc.head().children()){
				elementList.add(getElementDto(element));
			}
			documentDto.setHeadElements(elementList);
		}
		
		if(!doc.body().children().isEmpty()){
			List<ElementDto> elementList=new ArrayList<ElementDto>();
			for(Element element:doc.body().children()){
				elementList.add(getElementDto(element));
			}
			documentDto.setBodyElements(elementList);
		}
		
		return documentDto;
	}
	
	public static ElementDto getElementDto(Element element){
		ElementDto elementDto=new ElementDto();
		elementDto.setName(element.tagName());
		elementDto.setId(element.id());
		elementDto.setBody(element.html());
		// Attributes
		if(element.attributes().size()!=0){
			Map<String, String> attrMap=new HashMap<String, String>();
			for(Attribute attr:element.attributes()){
				attrMap.put(attr.getKey(), attr.getValue());
			}
			elementDto.setAttributesMap(attrMap);
		}
		//inner elements
		if(!element.children().isEmpty()){
			List<ElementDto> elementDtoList=new ArrayList<ElementDto>();
			for(Element innerElement:element.children()){
				elementDtoList.add(getElementDto(innerElement));
			}
			elementDto.setElements(elementDtoList);
		}
		
		return elementDto;
	}

}
