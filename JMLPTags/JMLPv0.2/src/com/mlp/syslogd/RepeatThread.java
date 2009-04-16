package com.mlp.syslogd;

import java.util.Date;

public class RepeatThread extends Thread {
	public static final String RCS_ID = "$Id: RepeatThread.java,v 1.1.1.1 1998/02/22 05:47:55 time Exp $";
	public static final String RCS_REV = "$Revision: 1.1.1.1 $";
	public static final String RCS_NAME = "$Name:  $";

	private ConfigEntryVector entries;

	public RepeatThread(ConfigEntryVector entries) {
		super();

		this.entries = entries;
	}

	public void run() {
		ConfigEntry entry;

		for (;;) {
			try {
				sleep(5000);
			} catch (InterruptedException ex) {
			}

			Date now = new Date();

			for (int idx = 0; idx < entries.size(); ++idx) {
				entry = this.entries.entryAt(idx);
				entry.checkRepeatTimeout(now);
			}
		}
	}

}
