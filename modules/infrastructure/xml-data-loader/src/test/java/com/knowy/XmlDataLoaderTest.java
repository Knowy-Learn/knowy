package com.knowy;

import com.knowy.core.exception.KnowyValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class XmlDataLoaderTest {

	private final XmlDataLoader xmlDataLoader = new XmlDataLoader();

	@Test
	void given_xmlAndXsdFile_when_loadData_then_returnMapStringObject() {
		InputStream xml = ClassLoader.getSystemClassLoader().getResourceAsStream("data.xml");
		URL xsd = ClassLoader.getSystemClassLoader().getResource("data.xsd");

		Map<String, Object> result = assertDoesNotThrow(() -> xmlDataLoader.loadData(
				Objects.requireNonNull(xml),
				Objects.requireNonNull(xsd)
			)
		);

		@SuppressWarnings("unchecked") List<Map<String, Object>> items = (List<Map<String, Object>>) result.get("item");
		assertAll(
			() -> assertEquals(3, items.size(), "Debe haber 3 items"),
			// --- Item 1 ---
			() -> assertEquals("1", items.getFirst().get("id")),
			() -> assertEquals("Item One", items.getFirst().get("name")),
			() -> assertEquals("Value One", items.getFirst().get("value")),
			// --- Item 2 ---
			() -> assertEquals("2", items.get(1).get("id")),
			() -> assertEquals("Item Two", items.get(1).get("name")),
			() -> assertEquals("Value Two", items.get(1).get("value")),

			// --- Item 3 ---
			() -> assertEquals("3", items.get(2).get("id")),
			() -> assertEquals("Item Three", items.get(2).get("name")),
			() -> assertEquals("Value Three", items.get(2).get("value"))
		);
	}

	@Test
	void given_invalidXmlFile_when_loadData_then_throwKnowyValidationException() {
		InputStream xml = ClassLoader.getSystemClassLoader().getResourceAsStream("invaliddata.xml");
		URL xsd = ClassLoader.getSystemClassLoader().getResource("data.xsd");

		assertThrows(
			KnowyValidationException.class,
			() -> xmlDataLoader.loadData(Objects.requireNonNull(xml), Objects.requireNonNull(xsd))
		);
	}
}
