package com.knowy;

import com.knowy.core.exception.KnowyException;
import com.knowy.core.exception.KnowyValidationException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

class XmlValidation {

	public void validate(InputStream xml, URL xsd) throws KnowyValidationException, IOException {
		try {
			Schema schema = createSchema(xsd);
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(xml));
		} catch (SAXException e) {
			throw new KnowyValidationException("The XML does not comply with the XSD: " + e.getMessage(), e);
		}
	}

	private Schema createSchema(URL xsd) throws SAXException {
		SchemaFactory schemaFactory = createSchemaFactory();
		return schemaFactory.newSchema(xsd);
	}

	private SchemaFactory createSchemaFactory() throws SAXNotSupportedException, SAXNotRecognizedException {
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

		return factory;
	}
}
