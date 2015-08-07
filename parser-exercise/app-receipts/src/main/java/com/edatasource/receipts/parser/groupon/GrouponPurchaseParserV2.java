package com.edatasource.receipts.parser.groupon;

import com.edatasource.receipts.helper.ParserUtil;
import com.edatasource.receipts.model.Address;
import com.edatasource.receipts.model.ecommerce.EcommerceItem;
import com.edatasource.receipts.model.ecommerce.EcommerceReceipt;
import com.edatasource.receipts.model.ecommerce.EcommerceShipment;
import com.edatasource.receipts.parser.AddressParser;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GrouponPurchaseParserV2 {

    private static Pattern itemPattern = Pattern.compile("<tr>\\s*<td.*>(.*)</td>\\s*<td.*>\\(x(\\d+)\\)</td>\\s*</tr>");

    public static EcommerceReceipt parse(Document doc) throws IOException {
        EcommerceReceipt ecommerceReceipt = new EcommerceReceipt();
        ecommerceReceipt.setOrderNumber(parseOrderNumber(doc));
        ecommerceReceipt.setOrderSubTotal(parseBillingAmount(doc, "Subtotal"));
        ecommerceReceipt.setOrderTax(parseBillingAmount(doc, "Tax"));
        ecommerceReceipt.setShippingAmount(parseBillingAmount(doc, "Shipping & Handling"));
        ecommerceReceipt.setOrderTotal(parseBillingAmount(doc, "Total"));
        ecommerceReceipt.setShipments(parseShipments(doc));
        return ecommerceReceipt;
    }

    private static String parseOrderNumber(Document doc) {
        return doc.getElementsContainingOwnText("Your order number is").get(0).child(0).text();
    }

    private static Double parseBillingAmount(Document doc, String category) {
        String dollars = doc.getElementsMatchingOwnText("^" + category + "$").get(0).nextElementSibling().text();
        return ParserUtil.dollarsToDouble(dollars);
    }

    private static List<EcommerceShipment> parseShipments(Document doc) {
        List<EcommerceShipment> shipments = new ArrayList<>();
        EcommerceShipment shipment = new EcommerceShipment();
        shipment.setItems(parseItems(doc));
        shipment.setShipping(parseShippingAddress(doc));
        shipments.add(shipment);
        return shipments;
    }

    private static List<EcommerceItem> parseItems(Document doc) {
        List<EcommerceItem> items = new ArrayList<>();
        Matcher matcher = itemPattern.matcher(doc.html());
        while(matcher.find()) {
            EcommerceItem ecommerceItem = new EcommerceItem();
            ecommerceItem.setDescription(matcher.group(1).trim());
            ecommerceItem.setQuantity(new Integer(matcher.group(2)));
            items.add(ecommerceItem);
        }
        return items;
    }

    private static Address parseShippingAddress(Document doc) {
        return AddressParser.parse(prepareAddressString(doc));
    }

    private static String prepareAddressString(Document doc) {
        Elements tableData = doc.getElementsContainingOwnText("Shipping address")
                .stream().filter(e -> e.parent().html().matches("<td.*?>Shipping address:</td>"))
                .findFirst()
                .get()
                .parent()
                .nextElementSibling()
                .getElementsByTag("td");
        return tableData.stream().filter(element -> element.children().isEmpty())
                .map(Element::text)
                .collect(Collectors.joining("\n"));
    }

}
