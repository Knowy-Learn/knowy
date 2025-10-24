package com.knowy.core.port;

import com.knowy.core.exception.KnowyValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

public interface DataLoader {
	Map<String, Object> loadData(InputStream inputStream, URL schema) throws KnowyValidationException, IOException;
}
