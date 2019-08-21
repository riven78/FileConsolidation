package com.riven.fileutils;

import java.util.ArrayList;

public class CSVObject {
	private final ArrayList<String> strings = new ArrayList<>();

	public CSVObject() {

	}

	public CSVObject(String input) {
		if (input != null && input.length() > 0) {
			String[] tmps = input.split(",");
			if (tmps != null && tmps.length > 0) {
				String str = null;
				boolean isString = false;
				for (String tmp : tmps) {
					if (tmp == null || tmp.length() == 0) {
						if (str == null) {
							strings.add("");
							isString = false;
						} else {
							str += ",";
							isString = true;
						}
					} else {
						String yinhao = "\"";
						int startYinhaoCount = 0;
						if (tmp.startsWith(yinhao)) {
							yinhao += "\"";
							startYinhaoCount++;
						}
						yinhao = "\"";
						int endYinhaoCount = 0;
						if (tmp.endsWith(yinhao)) {
							yinhao += "\"";
							endYinhaoCount++;
						}

						if (endYinhaoCount % 2 == 1) {
							tmp = tmp.substring(0, tmp.length() - 1);
						}
						if (startYinhaoCount % 2 == 1) {
							isString = true;
							tmp = tmp.substring(1);
						}
						tmp = tmp.replaceAll("\"{2}", "\"");

						if (str == null) {
							str = tmp;
						} else {
							str += "," + tmp;
						}

						if (!isString || isString && endYinhaoCount % 2 == 1) {
							strings.add(str);
							str = null;
							isString = false;
						}
					}
				}
			}
		}
	}

	public String getString(int index) {
		String ret = null;
		if (strings.size() > index) {
			ret = strings.get(index);
		}
		return ret;
	}

	public void add(String value) {
		if (value == null) {
			strings.add("");
		} else {
			strings.add(value);
		}
	}

	public void set(int index, String value) {
		if (value == null) {
			strings.set(index, "");
		} else {
			strings.set(index, value);
		}
	}

	public int size() {
		return strings.size();
	}

	@Override
	public String toString() {
		String ret = "";
		for (String str : strings) {
			if (str.length() == 0) {
				ret += ",";
			} else {
				str = str.replaceAll("\"", "\"\"");
				ret += ",\"" + str + "\"";
			}
		}
		if (ret.length() > 0) {
			ret = ret.substring(1);
		}
		return ret;
	}

	@Override
	protected void finalize() throws Throwable {
		strings.clear();
	}

}
