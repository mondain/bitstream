/**
 * 
 */
package com.aoc;

/**
 * @author Saleem
 * 
 */
public class History {

	private String lastCreateDir = "user.home";
	private String lastDownDir = "user.home";
	private static History INSTANCE = null;

	private History() {
	}

	public void setLastDownDir(String path) {
		lastDownDir = path;
	}

	public String getLastDownDir() {
		return lastDownDir;
	}

	public void setLastCreateDir(String path) {
		lastCreateDir = path;
	}

	public String getLastCreateDir() {
		return lastCreateDir;
	}

	public static History getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new History();
		}
		return INSTANCE;
	}

}
