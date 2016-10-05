package com.edatasource.receipts.parsers.groupon;

import com.edatasource.receipts.model.Address;
import com.edatasource.receipts.model.ecommerce.EcommerceItem;
import com.edatasource.receipts.model.ecommerce.EcommerceReceipt;
import com.edatasource.receipts.parser.groupon.GrouponPurchaseParserV2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class GrouponPurchaseParserTest {

	@Test
	public void testV2RegexUpdate1() throws Exception {
		byte[] encoded = Files
				.readAllBytes(Paths.get("./app-receipts/test-data/groupon/valid_v2/V2_regexUpdate1.html"));
		String HTML = new String(encoded, Charset.defaultCharset());
		Document doc = Jsoup.parse(HTML);

		EcommerceReceipt receipt = GrouponPurchaseParserV2.parse(doc);
		EcommerceItem item = receipt.getShipments().get(0).getItems().get(0);

		Address address = receipt.getShipping();

		assertEquals("12345 Test St", address.getStreetOrBoxInfo());
		assertEquals("Sun Valley", address.getCity());
		assertEquals("California", address.getState());
		assertEquals("12345-1234", address.getPostalCode());

		assertEquals("Sirius Beauty Sonic Skincare System", item.getDescription());
		assertEquals(24.0, receipt.getOrderSubTotal(), 0);
		assertEquals(28.74, receipt.getOrderTotal(), 0);
	}

}
