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

    private static Pattern itemQuantityPattern = Pattern.compile("\\(x(\\d+)\\)");

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
        String dollars = doc.getElementsMatchingOwnText(category).get(0).nextElementSibling().text();
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
        return doc.getElementsMatchingOwnText(itemQuantityPattern).stream()
                .map(element -> {
                    EcommerceItem item = new EcommerceItem();
                    item.setQuantity(getItemQuantity(element.text()));
                    item.setDescription(element.firstElementSibling().text());
                    return item;
                }).collect(Collectors.toList());
    }

    private static Address parseShippingAddress(Document doc) {
        return AddressParser.parse(prepareAddressString(doc));
    }

    private static String prepareAddressString(Document doc) {
        Elements tableData = doc.getElementsContainingOwnText("Shipping address")
                .get(0)
                .parent()
                .nextElementSibling()
                .getElementsByTag("td");
        return tableData.stream().filter(element -> element.children().isEmpty())
                .map(Element::text)
                .collect(Collectors.joining("\n"));
    }

    private static Integer getItemQuantity(String quantity) {
        Matcher matcher = itemQuantityPattern.matcher(quantity);
        if (matcher.find()) {
            return new Integer(matcher.group(1));
        }
        return null;
    }


}
