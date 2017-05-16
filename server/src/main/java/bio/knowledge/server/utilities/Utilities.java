package bio.knowledge.server.utilities;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

public class Utilities {
	private final static String ENCODING = "UTF-8";
	
	public static String[] buildArray(String string) {
		return string != null && !string.isEmpty() ? string.split(" ") : null;
	}
	
	public static int fixPageSize(Integer pageSize) {
		return pageSize != null && pageSize >= 1 ? pageSize : 10;
	}
	
	public static int fixPageNumber(Integer pageNumber) {
		return pageNumber != null && pageNumber >= 1 ? pageNumber : 1;
	}

	public static String urlDecode(String urlEncodedString) {
		if (urlEncodedString == null) {
			return null;
		}

		try {
			return URLDecoder.decode(urlEncodedString, ENCODING);
		} catch (UnsupportedEncodingException e) {
			// It should not be possible for this exception to be thrown
			throw new RuntimeException(e.getMessage(), e.getCause());
		}
	}

	public static List<String> urlDecode(List<String> urlEncodedStrings) {
		if (urlEncodedStrings == null) {
			return null;
		}
		
		for (int i = 0; i < urlEncodedStrings.size(); i++) {
			String string = urlEncodedStrings.get(i);
			urlEncodedStrings.set(i, urlDecode(string));
		}

		return urlEncodedStrings;
	}
	
}
