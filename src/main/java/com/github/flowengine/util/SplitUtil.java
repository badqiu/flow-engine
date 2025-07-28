package com.github.flowengine.util;

import org.springframework.util.StringUtils;

public class SplitUtil {
	private static final String[] EMPTY_STRING_ARRAY = {};
	
	public static String[] defaultSplit(String str) {
		if(StringUtils.hasText(str)) {
			return StringUtils.tokenizeToStringArray(str.trim(), ",，;； \t\n");
		}
		return EMPTY_STRING_ARRAY;
	}
	
}
