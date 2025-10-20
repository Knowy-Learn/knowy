package com.knowy;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.knowy.core.exception.KnowyIllegalArgumentRuntimeException;
import com.knowy.core.exception.KnowyValidationException;
import com.knowy.core.port.DataLoader;

import java.io.*;
import java.util.Map;
import java.util.UUID;

public class XmlDataLoader implements DataLoader {

	private final XmlValidation xmlValidation;

	public XmlDataLoader() {
		this.xmlValidation = new XmlValidation();
	}

	@Override
	public Map<String, Object> loadData(InputStream inputStream, InputStream schemaStream) throws KnowyValidationException, IOException {
		try (InputStream markedStream = mark(inputStream)) {
			xmlValidation.validate(markedStream, schemaStream);
			markedStream.reset();
			return readXml(markedStream);
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> readXml(InputStream inputStream) throws IOException {
		XmlMapper xmlMapper = new XmlMapper();

		Map<String, Object> result = xmlMapper.readValue(inputStream, Map.class);
		result.remove("noNamespaceSchemaLocation");

		return result;
	}

	InputStream mark(InputStream is) {
		if (is.markSupported() && !(is instanceof BufferedInputStream)) {
			is.mark(100 * 1024 * 1024);
			return is;
		}
		try {
			File cache = File.createTempFile("xmlloader", UUID.randomUUID().toString());
			try (FileOutputStream cacheOutputStream = new FileOutputStream(cache)) {
				is.transferTo(cacheOutputStream);
				cacheOutputStream.flush();
			}
			return new ResettableFileInputStream(cache);
		} catch (IOException e) {
			throw new KnowyIllegalArgumentRuntimeException(e);
		}
	}

	static class ResettableFileInputStream extends InputStream {

		private final File cache;
		private InputStream current;

		ResettableFileInputStream(File cache) throws IOException {
			this.cache = cache;
			this.current = new FileInputStream(cache);
		}

		@Override
		public int read() throws IOException {
			return current.read();
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			return current.read(b, off, len);
		}

		@Override
		public boolean markSupported() {
			return true;
		}

		@Override
		public void reset() throws IOException {
			current.close();
			current = new FileInputStream(cache);
		}

		@Override
		public void close() throws IOException {
			current.close();
			super.close();
		}
	}
}
