package com.mlp.syslogd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Hashtable;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/*
 * This class will need to be changed for every new
 * environment that the syslogd package is used in.
 * Specifically, the audio currently depends on an
 * available Applet, which is provided by the SyslogD
 * application in this instance.
 */

public class SyslogMedia implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2351921304492440970L;
	public static final String RCS_ID = "$Id: SyslogMedia.java,v 1.1.1.1 1998/02/22 05:47:54 time Exp $";
	public static final String RCS_REV = "$Revision: 1.1.1.1 $";
	public static final String RCS_NAME = "$Name:  $";

	static public final String sunAudioAPIName = "SunAudio";
	static public final String jmfAudioAPIName = "JMFAudio";

	static private Hashtable audioCache;

	static {
		SyslogMedia.audioCache = new Hashtable();
	}

	static public void loadAudioClip(String apiName, String audioPath) {
		if (apiName.equalsIgnoreCase(SyslogMedia.sunAudioAPIName)) {
			SyslogMedia.loadSunAudioClip(audioPath);
		} else {
			System.err.println("ERROR loading, unknown audio API '" + apiName
					+ "'");
		}
	}

	static public void playAudioClip(String apiName, String audioPath) {
		if (apiName.equalsIgnoreCase(SyslogMedia.sunAudioAPIName)) {
			SyslogMedia.playSunAudioClip(audioPath);
		} else {
			System.err.println("ERROR playing, unknown audio API '" + apiName
					+ "'");
		}
	}

	static public void loadSunAudioClip(String audioPath) {
		//** add this into your application code as appropriate
		File audioFile = new File(audioPath);
		if (!audioFile.exists()) {
			System.err.println("ERROR could not load audio clip '" + audioPath
					+ "', it does not exist.");
		} else {
			SyslogMedia.audioCache.put(audioPath, audioFile);
		}
	}

	static public void playSunAudioClip(String audioPath) {
		File audioFile = (File) SyslogMedia.audioCache.get(audioPath);

		if (audioFile != null) {
			InputStream in = null;
			AudioStream audioClip = null;

			try {
				in = new FileInputStream(audioFile);
				audioClip = new AudioStream(in);
				AudioPlayer.player.start(audioClip);
			} catch (IOException ex) {
				System.err.println("ERROR playing audioClip '" + audioPath
						+ "':\n\t" + ex.getMessage());
			}
		}
	}

}
