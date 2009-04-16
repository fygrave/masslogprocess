package com.mlp.syslogd;

import java.util.Vector;

public class ConfigEntryVector extends Vector<Object> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7820407271888539951L;
	public static final String RCS_ID = "$Id: ConfigEntryVector.java,v 1.1.1.1 1998/02/22 05:47:54 time Exp $";
	public static final String RCS_REV = "$Revision: 1.1.1.1 $";
	public static final String RCS_NAME = "$Name:  $";

	public ConfigEntryVector() {
		super();
	}

	public ConfigEntry entryAt(int index) {
		return (ConfigEntry) get(index);
	}

	public void addConfigEntry(ConfigEntry entry) {
		this.add(entry);
	}

}
