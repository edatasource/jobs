package com.edatasource.receipts.parser.groupon;

import com.edatasource.receipts.helper.ParserUtil;
import com.edatasource.receipts.model.Address;
import com.edatasource.receipts.model.ecommerce.EcommerceItem;
import com.edatasource.receipts.model.ecommerce.EcommerceShipment;
import com.edatasource.receipts.parser.AddressParser;
import org.jsoup.nodes.Document;

import com.edatasource.receipts.model.ecommerce.EcommerceReceipt;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.Locale;

public class GrouponPurchaseParserV2 {

	public static EcommerceReceipt parse(Document doc)  {

            //AddressParser.parse(doc.html());
            EcommerceReceipt receipt = new EcommerceReceipt();

            Elements tableElements = doc.select("table");
            // gets all the table td elements list
            Elements columnData = tableElements.select(":not(thead) tr td");

            ArrayList<String> dataList = new ArrayList<>();

            for (int i = 0; i < columnData.size(); i++) {
                    Element col = columnData.get(i);
                    // filter and check not to include in text list all the empty elements from text
                    String replaced = col.text().replace("\u00a0", "");
                    if (!replaced.equals("")) {
                            dataList.add(replaced);
                    }

            }

            int listSize = dataList.size();

            int totalIndex = dataList.indexOf(EcommerceTexts.TOTAL.getValue());

            if(totalIndex != -1 && totalIndex + 1 < listSize) {

                    Number tNumber =  ParserUtil.parseCurrency(dataList.get(totalIndex + 1), Locale.US);
                    receipt.setOrderTotal(tNumber.doubleValue());
            }

            int subTotalIndex = dataList.indexOf(EcommerceTexts.SUBTOTAL.getValue());

            if(subTotalIndex != -1 && subTotalIndex + 1 < listSize) {

                    Number subNumber =  ParserUtil.parseCurrency(dataList.get(subTotalIndex + 1), Locale.US);
                    receipt.setOrderSubTotal(subNumber.doubleValue());
            }

            int addressIndex = dataList.indexOf(EcommerceTexts.SHIPPING_ADDR.getValue());

            EcommerceShipment shipment = new EcommerceShipment();

            int descIndex = dataList.indexOf(EcommerceTexts.DESCRIPTION.getValue());

            if(totalIndex != -1 && totalIndex + 2 < listSize) {
                    EcommerceItem item = new EcommerceItem();
                    String description = dataList.get(descIndex + 2);
                    item.setDescription(description);
                    shipment.addItem(item);
            }


            if(addressIndex != -1 && addressIndex + 4 < listSize) {
                    Address address = AddressParser.parse(dataList.get(addressIndex + 2) + "\n" +
                            dataList.get(addressIndex + 3) + "\n" +
                            dataList.get(addressIndex + 4));


                    shipment.setShipping(address);
                    receipt.addShipment(shipment);
            }

        return receipt;
	}


        private enum EcommerceTexts {

                SUBTOTAL("Subtotal"),
                TOTAL("Total"),
                SHIPPING_ADDR("Shipping address:"),
                DESCRIPTION("Order summary:");

                private String value;

                public String getValue() {
                        return value;
                }

                EcommerceTexts(String val) {
                        this.value = val;
                }
        }
}
