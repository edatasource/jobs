package com.edatasource.receipts.parser.groupon;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.edatasource.receipts.helper.ParserUtil;
import com.edatasource.receipts.model.Address;
import com.edatasource.receipts.model.ecommerce.EcommerceItem;
import com.edatasource.receipts.model.ecommerce.EcommerceReceipt;
import com.edatasource.receipts.model.ecommerce.EcommerceShipment;
import com.edatasource.receipts.parser.AddressParser;

public class GrouponPurchaseParserV2{

	public static EcommerceReceipt parse(Document doc)  {
		
		EcommerceReceiptLoader receiptLoader = new EcommerceReceiptLoader(doc);
		Address shippingAddress = receiptLoader.getShippingAddress();
		
		EcommerceReceipt receipt = new EcommerceReceipt()
		{
			@Override
			public Address getShipping() // The getShipping method is not working properly by default, so an override is necessary.
			{
				return shippingAddress;
			}
		};
		
		receiptLoader.loadEcommerceReceiptData(receipt);
		
		return receipt;
	}

	private static class EcommerceReceiptLoader{
		private Document doc;
		
		private EcommerceReceiptLoader(Document doc)
		{
			this.doc = doc;
		}
		
		private void loadEcommerceReceiptData(EcommerceReceipt receipt)
		{
			// Set order subtotals and full total
			loadOrderTotals(receipt);
			
			// Set shipment of ecommerce items.
			getEcommerceShipments().stream().forEach(shipment -> receipt.addShipment(shipment)); // Java 8 Stream API and Lambda Expression
			
			// Set shipping address.  The EcommerceReceipt object's getShipping method is not utilizing the shipping address object set
			// on the first non null shipment object as documented in the API docs. But I am setting a shipping address anyway though 
			// useless.  The method override done on EcommerceReceipt's getShipping method seems to be the only solution to get it to return a
			// shipping address object.
			if(!CollectionUtils.isEmpty(getEcommerceShipments()))
				getEcommerceShipments().get(0).setShipping(getShippingAddress());
		}
		
		private void loadOrderTotals(EcommerceReceipt receipt)
		{
			Elements subTotalElems = doc.select("td:matches(Subtotal) + td");
			
			if(!CollectionUtils.isEmpty(subTotalElems))
			{
				receipt.setOrderSubTotal(ParserUtil.dollarsToDouble(subTotalElems.get(0).text()));
			}
			
			Elements orderTotalElems = doc.select("td:matches(Total) + td");
			
			if(!CollectionUtils.isEmpty(orderTotalElems))
			{
				receipt.setOrderTotal(ParserUtil.dollarsToDouble(orderTotalElems.get(0).text()));
			}
		}
		
		private List<EcommerceShipment> getEcommerceShipments()
		{
			List<EcommerceShipment> ecommerceShipments = new ArrayList<>();
			
			//Not sure where to source the shipments data, so we'll just simulate one shipment being available.
			EcommerceShipment ecommerceShipment = new EcommerceShipment();
			List<EcommerceItem> ecommerceItems = getEcommerceItems();
			
			ecommerceItems.stream().forEach(item -> ecommerceShipment.addItem(item)); // Java 8 Stream API and Lambda Expression
			
			ecommerceShipments.add(ecommerceShipment);
			
			return ecommerceShipments;
		}
		
		private List<EcommerceItem> getEcommerceItems()
		{
			List<EcommerceItem> ecommerceItems = new ArrayList<>();
					
			Elements orderItemsElems = doc.select("tr:matches(Order placed on) + tr"); // Regex to locate order items section
			
			if(!CollectionUtils.isEmpty(orderItemsElems))
			{
				for(Element lineItemElem : orderItemsElems)
				{
					Elements lineItemCellsElems = lineItemElem.select("td"); // Should be two cells per line item.
					
					if(CollectionUtils.size(lineItemCellsElems) == 2) // If two cells, then we have a line item.
					{
						EcommerceItem ecomItem = new EcommerceItem();
						
						ecomItem.setDescription(lineItemCellsElems.get(0).text());
						ecommerceItems.add(ecomItem);
					}
					else // We're not in the line items section anymore so we can stop checking.
					{
						break;
					}
				}
			}

			return ecommerceItems;	
		}
		
		private Address getShippingAddress()
		{
			Address address = new Address(); 
			
			Element shippingAddressSubTitleElem = doc.select("tr:matches(Shipping address) + tr").first(); // Regex to locate address section.
			
			if(shippingAddressSubTitleElem != null)
			{
				Elements shippingAddressLinesElems = shippingAddressSubTitleElem.select("table tr"); // Locate address lines
				
				  // Build multi-line address field for parsing to Address object.
				int lineCount = CollectionUtils.size(shippingAddressLinesElems);
				
				if(lineCount > 0)
				{
					int maxLines = 4;  // We need to anticipate less than six relevant address lines.
					
					StringBuilder addrLines = new StringBuilder();
					
					for(int i = 0; i < lineCount; i++)
					{
						if(i < maxLines)
							addrLines.append(shippingAddressLinesElems.get(i).text() + "\n");
					}
					
					try
					{
						address = AddressParser.parse(addrLines.toString());
					}
					catch(Throwable e) // Cannot parse address lines but no need for a runtime exception explosion.
					{
						
					}
				}
			}
			
			return address;
		}
		
	}
	
}
