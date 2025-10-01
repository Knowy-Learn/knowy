package com.knowy.core.port;

import java.io.InputStream;
import java.util.Map;

public interface DataLoader {
	Map<String, Object> loadData(InputStream inputStream);
}
