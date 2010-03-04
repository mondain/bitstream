package net.sf.fmj.utility;

import java.net.FileNameMap;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Used to add mappings that are not in content-types.properties
 * 
 * @author Ken Larson
 * 
 */
public class CustomFileNameMap implements FileNameMap {
	// The "real" one is accessed using URLConnection.getFileNameMap().
	// This class can be used together with the "real" to make one fill in the
	// defaults for/override the other.
	// then use URLConnection.setFileNameMap().

	private final Map map = new HashMap();

	public void map(String[] exts, String type) {
		for (int i = 0; i < exts.length; ++i) {
			map(exts[i], type);
		}
	}

	public void map(String ext, String type) {
		if (ext.startsWith("."))
			throw new IllegalArgumentException(
					"Extension should not include dot");

		map.put(ext.toLowerCase(), type);
	}

	public String getType(String ext) {
		if (ext == null)
			return null;
		return (String) map.get(ext.toLowerCase());
	}

	public String getContentTypeFor(String fileName) {
		return getType(PathUtils.extractExtension(fileName));
	}
}
