package com.mlp.syslogd;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import com.mlp.syslog.SyslogDefs;

/**
 * The ConfigFormat class implements the code necessary
 * to format and parse syslog configuration entries.
 *
 * @version $Revision: 1.1.1.1 $
 * @author Timothy Gerard Endres,Colin doug
 *   <a href="mailto:colindoug09@gmail.com">colindoug09@gmail.com</a>.
 * @see SyslogServer
 */

public class ConfigFormat extends Format {
	public static final String RCS_ID = "$Id: ConfigFormat.java,v 1.1.1.1 1998/02/22 05:47:54 time Exp $";
	public static final String RCS_REV = "$Revision: 1.1.1.1 $";
	public static final String RCS_NAME = "$Name:  $";

	static public final char configSeparator = '.';
	static public final String paramSeparator = " \t";
	static public final String facilitySeparator = ",";

	static public final ConfigFormat getInstance() {
		return new ConfigFormat();
	}

	public ConfigFormat() {
		super();
	}

	public String format(ConfigEntry entry) throws IllegalArgumentException {
		StringBuffer result = new StringBuffer();

		result.append("UNIMPLEMENTED: ConfigFormat.format( ConfigEntry )");

		return result.toString();
	}

	public StringBuffer format(Object entry, StringBuffer appendTo,
			FieldPosition fieldPos) throws IllegalArgumentException {
		// UNDONE - handle fieldPos!
		String tmpFormat = this.format((ConfigEntry) entry);
		appendTo.append(tmpFormat);
		return appendTo;
	}

	public ConfigEntry parse(String source) throws ParseException {
		return this.parseEntry(source);
	}

	public Object parseObject(String source, ParsePosition pos) {
		ConfigEntry entry = null;

		try {
			entry = this.parseEntry(source);
		} catch (ParseException ex) {
			entry = null;
		}

		return (Object) entry;
	}

	// UNDONE - all the positions in ParseExceptions are zero.

	public ConfigEntry parseEntry(String source) throws ParseException {
		String entryStr = source;
		String facilityStr = null;
		String priorityStr = null;
		String actionStr = null;
		String matchStr = null;
		String parameterStr = null;
		String[] params = null;
		int index, tabIdx, slashIdx;
		int facility, priority;
		int tokeCount;
		StringTokenizer toker;

		boolean hasMatch = false;

		index = entryStr.indexOf(ConfigFormat.configSeparator);
		if (index < 0) {
			throw new ParseException(
					"configuration entry has no facility field", 0);
		}

		facilityStr = entryStr.substring(0, index);
		entryStr = entryStr.substring(index + 1);

		index = entryStr.indexOf(ConfigFormat.configSeparator);
		if (index < 0) {
			throw new ParseException(
					"configuration entry has no priority field", 0);
		}

		priorityStr = entryStr.substring(0, index);
		entryStr = entryStr.substring(index + 1);

		index = entryStr.indexOf(' ');
		tabIdx = entryStr.indexOf('\t');
		slashIdx = entryStr.indexOf('/');

		if (slashIdx > 0 && (index < 0 || slashIdx < index)
				&& (tabIdx < 0 || slashIdx < tabIdx)) {
			// We have a regeular expression to match...
			actionStr = entryStr.substring(0, slashIdx);
			entryStr = entryStr.substring(slashIdx + 1);
			slashIdx = entryStr.indexOf('/');
			if (slashIdx < 0) {
				throw new ParseException(
						"configuration entry has bad match expression", 0);
			} else {
				hasMatch = true;
				matchStr = entryStr.substring(0, slashIdx);
				if ((slashIdx + 1) < entryStr.length())
					parameterStr = entryStr.substring(slashIdx + 1);
				else
					parameterStr = null;
			}
		} else if (index < 0 && tabIdx < 0) {
			actionStr = entryStr;
			parameterStr = null;
		} else {
			if (tabIdx >= 0 && tabIdx < index)
				index = tabIdx;
			actionStr = entryStr.substring(0, index);
			parameterStr = entryStr.substring(index + 1);
		}

		if (parameterStr != null) {
			params = com.mlp.util.StringUtilities
					.parseArgumentString(parameterStr);
		}

		ConfigEntry result = new ConfigEntry();

		SyslogAction action = new SyslogAction(actionStr, params);

		result.setAction(action);

		if (hasMatch) {
			result.setMatchExpr(matchStr);
		}

		if (priorityStr.equals("*")) {
			priority = SyslogDefs.LOG_ALL;
		} else {
			priority = SyslogDefs.getPriority(priorityStr);
		}

		for (int i = 0; i < SyslogDefs.LOG_NFACILITIES; ++i) {
			result.setFacilityLevel(i, -1); // UNDONE !!!
		}

		if (facilityStr.equals("*")) {
			for (int i = 0; i < SyslogDefs.LOG_NFACILITIES; ++i) {
				result.setFacilityLevel(i, priority);
			}
		} else {
			toker = new StringTokenizer(facilityStr,
					ConfigFormat.facilitySeparator);

			tokeCount = toker.countTokens();

			if (tokeCount > 0) {
				for (int pIdx = 0; pIdx < tokeCount; ++pIdx) {
					String facilityName = null;

					try {
						facilityName = toker.nextToken();
					} catch (NoSuchElementException ex) {
						break;
					}

					facility = SyslogDefs.getFacility(facilityName);

					result.setFacilityLevel(facility, priority);
				}
			}
		}

		return result;
	}

}
