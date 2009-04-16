package com.mlp.syslogd;

import java.applet.Applet;
import java.awt.Frame;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import com.mlp.util.Global;
import com.mlp.util.UserProperties;
import com.mlp.util.util.GUtil;

/**
 * 
 * @version $Revision: 1.1.1.1 $
 * @author Timothy Gerard Endres,Colin Doug
 *   <a href="mailto:colindoug09@gmail.com">colindoug09@gmail.com</a>.
 * @see SyslogServer
 * 
 */
public class SyslogD{
	public static final String		RCS_ID = "$Id: SyslogD.java,v 1.1.1.1 1998/02/22 05:47:54 time Exp $";
	public static final String		RCS_REV = "$Revision: 1.1.1.1 $";
	public static final String		RCS_NAME = "$Name:  $";

	static public final String		VERSION_STR = Global.VERSION_STR;

	static private Frame			frame;
	static private Applet			applet;
	static private SyslogServer		server;
	static private RepeatThread		repeat;

	static Logger logger=Logger.getLogger(SyslogD.class.getName()) ;
	static public void
	main( String argv[] )
		{
		boolean		isDaemon = true;
		String		configPath = null;
		String		prefix = "SyslogD";
		Properties	props = new Properties();

		for ( int iArg = 0 ; iArg < argv.length ; ++iArg )
			{
			if ( argv[iArg].equals( "-daemon" ) )
				{
				isDaemon = true;
				}
			else if ( argv[iArg].equals( "-config" )
						&& (iArg + 1) < argv.length )
				{
				configPath = argv[++iArg];
				}else if ( argv[iArg].equals( "-debug" )
						 )
				{
					Global.isDebug = true;
				}
			else if ( argv[iArg].equals( "-prefix" )
						&& (iArg + 1) < argv.length )
				{
				prefix = argv[++iArg];
				}
			else if ( argv[iArg].equals( "-propfile" )
						&& (iArg + 1) < argv.length )
				{
				props.put
					( "SyslogD.global.localPropertyFile",
						argv[++iArg] );
				}
			else if ( argv[iArg].equals( "-?" )
						|| argv[iArg].equals( "-usage" ) )
				{
				SyslogD.printUsage();
				return;
				}
			else
				{
				System.err.println
					( "ERROR unknown option '" + argv[iArg] + "'" );
				SyslogD.printUsage();
				return;
				}
			}

		TimeZone.setDefault( TimeZone.getDefault() );

		UserProperties.setDefaultsResource("/com/mlp/syslogd/properties.txt");
		//UserProperties.setLocalPropertyFile();
		UserProperties.setPropertyPrefix( "SyslogD." );

		UserProperties.loadProperties( "com.mlp.syslogd", props );

		SyslogConfig config = new SyslogConfig();

		if ( configPath != null )
		{
			config.setConfigPathname( configPath );
		}

		config.loadConfiguration();

		SyslogD.server = new SyslogServer();
		
		SyslogD.server.setConfiguration( config );

		SyslogD.repeat =
			new RepeatThread( config.getConfigEntries() );
		
		if ( ! isDaemon )
			{
			SyslogD.applet =
				new SyslogDApplet( SyslogD.server );

			int x =
				UserProperties.getProperty
					( "mainWindow.x", 20 );
			int y =
				UserProperties.getProperty
					( "mainWindow.y", 20 );
			int w =
				UserProperties.getProperty
					( "mainWindow.width", -1 );
			int h =
				UserProperties.getProperty
					( "mainWindow.height", -1 );
			String title =
				UserProperties.getProperty
					( "mainWindow.title", "SyslogD" );

			SyslogD.frame =
				new SyslogDFrame
					( title, SyslogD.applet, x, y, w, h );
			}

		SyslogD.server.start();

		SyslogD.repeat.start();

		Timer timer = new Timer();
		TimerTask task = new TimerTask(){
			private long lastCount=0;
			private boolean first= false;
			@Override
			public void run() {
//				System.out.println("lastCount="+lastCount+" packetCount="+SyslogD.server.packetCount);
				long qsize = SyslogD.server.getQueue().size();
				long largestPool = SyslogD.server.getThreadPool().getLargestPoolSize();
				long completed=SyslogD.server.getThreadPool().getCompletedTaskCount();
				long taskCount=	SyslogD.server.getThreadPool().getTaskCount();
				long activeCount = SyslogD.server.getThreadPool().getActiveCount();
				if(lastCount==taskCount && qsize<1 && first ){
						return;
				}else{
				first=false;
				if(lastCount==taskCount && qsize<1 ){
					first = true;
				}
				lastCount = completed;
				String s="Recvd:"+lastCount
				+" QueSize="+qsize
				+" Task="+taskCount
				+" Active="+activeCount
				+" LargestPSize="+largestPool
				+" Completed="+completed;
				
				System.out.println(getTimef().format(new Date())+" "+s);
//				logger.log(Level.INFO,s);
				}
			}
			
		};
		timer.schedule(task, 3000, 3000);
		
		if ( false )
			{
			Runtime rt = Runtime.getRuntime();
			System.err.println
				( "TOTAL: " + rt.totalMemory() +"\n"
					+ "FREE: " + rt.freeMemory() );
			}
		}

	public static SimpleDateFormat df=GUtil.createDateFormat("HH:mm:ss.sss");
	public static SimpleDateFormat getTimef(){
		return df;		
	}
	public static void
	printUsage()
		{
		System.err.println( "usage: Syslogd [options...]" );
		System.err.println( "options:" );
		System.err.println
			( "   -daemon             -- "
				+ "run as a daemon (no windows)" );
		System.err.println
		( "   -debug             -- "
			+ "run in debug mode" );
		System.err.println
			( "   -prefix prefix      -- "
				+ "sets property prefix" );
		System.err.println
			( "   -config filename    -- "
				+ "sets syslog configuration file name" );
		System.err.println
			( "   -propfile filename  -- "
				+ "sets user property file name" );
		}

	}

