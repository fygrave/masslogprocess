

package com.mlp.syslogd;

import java.io.*;
import java.lang.*;
import java.text.*;
import java.util.*;


public class
SyslogConfig
	{
	public static final String		RCS_ID = "$Id: SyslogConfig.java,v 1.1.1.1 1998/02/22 05:47:55 time Exp $";
	public static final String		RCS_REV = "$Revision: 1.1.1.1 $";
	public static final String		RCS_NAME = "$Name:  $";

	public static final String		DEFAULT_CONFIG_PATH = "conf/syslog.conf";

	private String				pathName;
	private ConfigEntryVector	entries;
	private Hashtable			displays;

	private boolean				debugDisplays;


	public
	SyslogConfig()
		{
		this( SyslogConfig.DEFAULT_CONFIG_PATH );
		}

	public
	SyslogConfig( String pathName )
		{
		this.pathName = pathName;
		this.displays = new Hashtable();
		this.entries = new ConfigEntryVector();

		this.debugDisplays = false;
		}

	public void
	setConfigPathname( String pathName )
		{
		this.pathName = pathName;
		}

	public ConfigEntryVector
	getConfigEntries()
		{
		return this.entries;
		}

	public void
	addConfigEntry( ConfigEntry entry )
		{
		this.entries.addConfigEntry( entry );
		}

	public void
	addDisplayEntry( DisplayEntry entry )
		{
		this.displays.put( entry.getName(), entry );
		}

	public DisplayEntry
	getDisplayEntry( String name )
		{
		DisplayEntry entry = null;

		try { entry = (DisplayEntry) this.displays.get( name ); }
		catch ( NoSuchElementException ex )
			{ entry = null; }

		return entry;
		}

	public Enumeration
	getDisplayEnumeration()
		{
		return this.displays.elements();
		}

	public boolean
	loadConfiguration()
		{
		return this.loadConfiguration( this.pathName );
		}

	private boolean
	loadConfiguration( String pathName )
		{
		ConfigEntry		entry;
		BufferedReader	reader;
		boolean			result = true;

		try {
			reader = new BufferedReader(
						new FileReader( pathName ) );
			}
		catch ( Exception ex )
			{
			System.err.println
				( "ERROR opening configuration file '"
					+ pathName + "'\n\t" + ex.getMessage() );
			return false;
			}
		
		ConfigFormat parser = ConfigFormat.getInstance();

		for ( int lineNumber = 1 ; ; )
			{
			String			configLine = null;
			StringBuffer	configLineBuf = null;

			for ( ; ; )
				{
				boolean lineContinued = false;

				try {
					configLine = reader.readLine();
					}
				catch ( IOException ex )
					{
					result = false;
					configLineBuf = null;
					System.err.println
						( "ERROR reading configuration line #"
							+ lineNumber + ":\n\t" + ex.getMessage() );
					break;
					}

				if ( configLine == null )
					break;

				++lineNumber;

				if ( configLine.endsWith( "\\" ) )
					{
					lineContinued = true;
					configLine =
						configLine.substring
							( 0, configLine.length()-1 );
					}

				if ( configLineBuf != null )
					configLineBuf.append( configLine );
				else
					configLineBuf = new StringBuffer( configLine );

				if ( ! lineContinued )
					break;
				}

			// Check for end of file (EOF),
			if ( configLineBuf == null )
				break;

			configLine = configLineBuf.toString();

			// Skip blank lines.
			configLine = configLine.trim();
			if ( configLine.length() < 1 )
				continue;

			// Check for comment.
			if ( configLine.startsWith( "#" ) )
				{
				continue;
				}
			else if ( configLine.startsWith( "@" ) )
				{
				// REVIEW We are treating disply entries a little
				//        different, without its own Format class.
				//        If we add the ability to re-write the
				//        config file, we may re-think this!
				String[] args =
					com.mlp.util.StringUtilities.parseArgumentString
						( configLine.substring(1) );

				if ( args.length < 8 )
					{
					System.err.println
						( "ERROR bad display definition line #"
							+ lineNumber + ": wrong number of arguments." );
					}
				else
					{
					String name = args[0];
					String className = args[1];
					String title = args[2];

					int x = 20;
					int y = 30;
					int width = 500;
					int height = 300;
					int bufLength = 50;

					try {
						bufLength = Integer.parseInt( args[3] );
						x = Integer.parseInt( args[4] );
						y = Integer.parseInt( args[5] );
						width = Integer.parseInt( args[6] );
						height = Integer.parseInt( args[7] );
						}
					catch ( NumberFormatException ex )
						{
						System.err.println
							( "ERROR bad display geometry line #"
								+ lineNumber + ":\n\t" + ex.getMessage() );
						}

					String[] parameters = null;
					if ( args.length > 8 )
						{
						parameters = new String[ args.length - 8 ];
						for ( int i = 8 ; i < args.length ; ++i )
							{
							parameters[i-8] = args[i];
							}
						}

					DisplayEntry display =
						new DisplayEntry
							( name, className, title,
								bufLength, x, y, width, height,
									parameters );

					if ( display != null )
						{
						this.displays.put( name, display );
						if ( this.debugDisplays )
							System.err.println( "ADDED DISPLAY '" + name + "'" );
						}
					}
				}
			else
				{
				try {
					entry = parser.parseEntry( configLine );
					}
				catch ( ParseException ex )
					{
					result = false;
					System.err.println
						( "ERROR bad configuration line #"
							+ lineNumber + ":\n\t" + ex.getMessage() );
					continue;
					}

				this.addConfigEntry( entry );
				}
			}

		try { reader.close(); }
			catch ( IOException ex ) { }

		return result;
		}
	}
