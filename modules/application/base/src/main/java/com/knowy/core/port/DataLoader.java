package com.knowy.core.port;

import com.knowy.core.exception.KnowyValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface DataLoader {
	default Map<String, Object> loadData(InputStream inputStream) throws IOException {
		throw new UnsupportedOperationException("This loader does not support this loading method.");
	}

	default Map<String, Object> loadData(InputStream inputStream, InputStream schemaStream) throws KnowyValidationException, IOException {
		throw new UnsupportedOperationException("This loader does not support schema validation.");
	}
}
