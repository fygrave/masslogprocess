
package com.mlp.syslogd;

import java.lang.*;
import java.text.*;
import java.util.*;

/**
 * The TimestampFormat class implements the code necessary
 * to format and parse syslog timestamps, which come in the
 * flavor of 'Sep 14 15:43:06'.
 *
 * @version $Revision: 1.1.1.1 $
 * @author Timothy Gerard Endres,
 *   <a href="mailto:time@ice.com">time@ice.com</a>.
 * @see SyslogServer
 */

public class
TimestampFormat extends Format
	{																
	public static final String		RCS_ID = "$Id: TimestampFormat.java,v 1.1.1.1 1998/02/22 05:47:54 time Exp $";
	public static final String		RCS_REV = "$Revision: 1.1.1.1 $";
	public static final String		RCS_NAME = "$Name:  $";

	static public final String		DEFAULT_GMT_TZID = "GMT+00";


    static public final TimestampFormat
	getInstance()
		{
		return new TimestampFormat();
		}

	public
	TimestampFormat()
		{
		super();
		}

	public String
	format( Date date )
		throws IllegalArgumentException
		{
		TimeZone tz = TimeZone.getTimeZone
						( TimestampFormat.DEFAULT_GMT_TZID );

		return formatTimeZone( date, tz );
		}

	public String
	formatTimeZone( Date date, TimeZone tz )
		throws IllegalArgumentException
		{
		SimpleDateFormat dateFormat;
		Locale loc = Locale.US;		// UNDONE

		dateFormat = new SimpleDateFormat( "MMM", loc );
		dateFormat.setTimeZone( tz );
		String month = dateFormat.format( date );
		month = month.substring( 0, 3 );

		dateFormat = new SimpleDateFormat( "dd HH:mm:ss", loc );
		dateFormat.setTimeZone( tz );
		String rest = dateFormat.format( date );

		String result = new String
			( month + " " + rest );

		return result;
		}

	public StringBuffer
	format( Object date, StringBuffer appendTo, FieldPosition fieldPos )
		throws IllegalArgumentException
		{
		// UNDONE - handle fieldPos!
		String tmpFormat = this.format( (Date)date );
		appendTo.append( tmpFormat );
		return appendTo;
		}

	public Date
	parse( String source )
		throws ParseException
		{
		return parseTimestamp( source );
		}

	public Object
	parseObject( String source, ParsePosition pos )
		{
		Date stamp = null;

		try {
			stamp = this.parseTimestamp( source );
			}
		catch ( ParseException ex )
			{
			stamp = null;
			}

		return (Object) stamp;
		}

	// UNDONE - all the positions in ParseExceptions are zero.

	public Date
	parseTimestamp( String source )  
		throws ParseException
		{
		String		monName = null;
		String		dateStr = null;
		String		hmsStr = null;
		String		hourStr = null;
		String		minStr = null;
		String		secStr = null;

		StringTokenizer toker =
			new StringTokenizer( source, " " );

		int tokeCount = toker.countTokens();

		if ( tokeCount != 3 )
			{
			throw new ParseException
				( "a valid timestamp has 3 fields, not " + tokeCount, 0 );
			}

		try { monName = toker.nextToken(); }
		catch ( NoSuchElementException ex )
			{
			throw new ParseException
				( "could not parse month name (field 1)", 0 );
			}

		try { dateStr = toker.nextToken(); }
		catch ( NoSuchElementException ex )
			{
			throw new ParseException
				( "could not parse day of month (field 2)", 0 );
			}

		try { hmsStr = toker.nextToken(); }
		catch ( NoSuchElementException ex )
			{
			throw new ParseException
				( "could not parse time hh:mm:ss (field 3)", 0 );
			}

		toker = new StringTokenizer( hmsStr, ":" );

		tokeCount = toker.countTokens();

		if ( tokeCount != 3 )
			{
			throw new ParseException
				( "'" +hmsStr+ "' is not a valid timestamp time string", 0 );
			}

		try { hourStr = toker.nextToken(); }
		catch ( NoSuchElementException ex )
			{
			throw new ParseException
				( "could not parse time hour (field 3.1)", 0 );
			}
		try { minStr = toker.nextToken(); }
		catch ( NoSuchElementException ex )
			{
			throw new ParseException
				( "could not parse time minute (field 3.2)", 0 );
			}
		try { secStr = toker.nextToken(); }
		catch ( NoSuchElementException ex )
			{
			throw new ParseException
				( "could not parse time second (field 3.3)", 0 );
			}

		int month = 0;
		int date = 0;
		int hour = 0;
		int minute = 0;
		int second = 0;

		try { month = this.monthNameToInt( monName ); }
		catch ( ParseException ex )
			{
			throw new ParseException
				( "could not convert month name (field 1)", 0 );
			}

		try { date = Integer.parseInt( dateStr ); }
		catch ( NumberFormatException ex )
			{
			throw new ParseException
				( "could not convert month day (field 2)", 0 );
			}
		if ( date < 1 || date > 31 )
			{
			throw new ParseException
				( "month day '" + date + "' is out of range", 0 );
			}

		try { hour = Integer.parseInt( hourStr ); }
		catch ( NumberFormatException ex )
			{
			throw new ParseException
				( ( "could not convert hour (field 3.1) '"
					+ hourStr + "' - " + ex.getMessage() ), 0 );
			}
		if ( hour < 0 || hour > 24 )
			{
			throw new ParseException
				( "hour '" + hour + "' is out of range", 0 );
			}

		try { minute = Integer.parseInt( minStr ); }
		catch ( NumberFormatException ex )
			{
			throw new ParseException
				( ( "could not convert minute (field 3.2) '"
					+ minStr + "' - " + ex.getMessage() ), 0 );
			}
		if ( minute < 0 || minute > 59 )
			{
			throw new ParseException
				( "minute '" + minute + "' is out of range", 0 );
			}

		try { second = Integer.parseInt( secStr ); }
		catch ( NumberFormatException ex )
			{
			throw new ParseException
				( ( "could not convert second (field 3.3) '"
					+ secStr + "' - " + ex.getMessage() ), 0 );
			}
 		if ( second < 0 || second > 59 )
			{
			throw new ParseException
				( "second '" + second + "' is out of range", 0 );
			}

		Locale loc = Locale.US;		// UNDONE

		TimeZone tz = TimeZone.getTimeZone
						( TimestampFormat.DEFAULT_GMT_TZID );

		Calendar cal = Calendar.getInstance( tz, loc );

		cal.setTime( new Date() );

		cal.set( cal.get(Calendar.YEAR), month, date,
					hour, minute, second );

		Date result = new Date( cal.getTime().getTime() );

		return result;
		}

	private int
	monthNameToInt( String name )
		throws ParseException
		{
		// UNDONE - this could be optimized by checking the
		//          first character, since this resolves all
		//          by the 'A', 'J' and 'M' months.
		//
		if ( name.equalsIgnoreCase( "Jan" ) )
			return 0;
		else if ( name.equalsIgnoreCase( "Feb" ) )
			return 1;
		else if ( name.equalsIgnoreCase( "Mar" ) )
			return 2;
		else if ( name.equalsIgnoreCase( "Apr" ) )
			return 3;
		else if ( name.equalsIgnoreCase( "May" ) )
			return 4;
		else if ( name.equalsIgnoreCase( "Jun" ) )
			return 5;
		else if ( name.equalsIgnoreCase( "Jul" ) )
			return 6;
		else if ( name.equalsIgnoreCase( "Aug" ) )
			return 7;
		else if ( name.equalsIgnoreCase( "Sep" ) )
			return 8;
		else if ( name.equalsIgnoreCase( "Oct" ) )
			return 9;					  
		else if ( name.equalsIgnoreCase( "Nov" ) )
			return 10;
		else if ( name.equalsIgnoreCase( "Dec" ) )
			return 11;
		
		throw new ParseException
			( "unknown month name '" + name + "'", 0 );
		}
	}

