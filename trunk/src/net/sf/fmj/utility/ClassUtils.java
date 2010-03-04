package net.sf.fmj.utility;

/**
 * 
 * @author Ken Larson
 * 
 */
public class ClassUtils {
	public static String getShortClassName(Class c) {
		String fullName = c.getName();
		return getShortClassName(fullName);
	}

	public static String getShortClassName(String fullName) {

		int dotIndex = fullName.lastIndexOf('.');
		if (dotIndex < 0)
			return fullName;

		return fullName.substring(dotIndex + 1);
	}
}
