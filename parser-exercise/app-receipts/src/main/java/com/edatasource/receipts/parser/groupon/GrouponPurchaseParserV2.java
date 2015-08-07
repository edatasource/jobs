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

import com.edatasource.receipts.model.ecommerce.EcommerceReceipt;

import dto.DocumentDto;
import dto.ElementDto;

public class GrouponPurchaseParserV2{

	public static EcommerceReceipt parse(Document doc)  {
		return null;
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
