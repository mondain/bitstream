package net.sf.fmj.utility;

import java.net.FileNameMap;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows the creation of a FileNameMap using a list of FileNameMap. This allows
 * us to extend the default FileNameMap used by URLDataSource, for example, to
 * add missing mappings, without editing content-types.properties.
 * 
 * @author Ken Larson
 * 
 */
public class ChainedFileNameMap implements FileNameMap {

	private final List maps = new ArrayList(); // List of FileNameMap

	public ChainedFileNameMap(FileNameMap m1, FileNameMap m2) {
		maps.add(m1);
		maps.add(m2);
	}

	public String getContentTypeFor(String fileName) {
		for (int i = 0; i < maps.size(); ++i) {
			final FileNameMap m = (FileNameMap) maps.get(i);
			final String result = m.getContentTypeFor(fileName);
			if (result != null)
				return result;
		}
		return null;
	}

}
