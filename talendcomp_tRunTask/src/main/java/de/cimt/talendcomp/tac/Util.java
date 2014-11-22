package de.cimt.talendcomp.tac;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
	
	private Util() {}

	public static String extractByRegexGroup(String content, String regex, int groupOccurrence) {
		if (regex != null) {
			if (content != null) {
				Pattern pattern = Pattern.compile(regex);
		        Matcher matcher = pattern.matcher(content);
		        while (matcher.find()) {
	            	for (int i = 1, n = matcher.groupCount(); i <= n; i++) {
			            if (matcher.start(i) < matcher.end(i)) {
				            if (i == groupOccurrence) {
				                return matcher.group(i);
				            }
			            }
	            	}
		        }
				return null;
			} else {
				return null;
			}
		} else {
			return content;
		}
	}
	
	public static String getCurrentTimeAsString() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
		return sdf.format(new Date());
	}

}
