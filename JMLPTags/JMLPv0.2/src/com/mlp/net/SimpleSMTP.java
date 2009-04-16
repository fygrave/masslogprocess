/*
** Tim Endres' net package.
** 
** This program is free software.
** 
** You may redistribute it and/or modify it under the terms of the GNU
** General Public License as published by the Free Software Foundation.
** Version 2 of the license should be included with this distribution in
** the file LICENSE, as well as License.html. If the license is not
** included	with this distribution, you may find a copy at the FSF web
** site at 'www.gnu.org' or 'www.fsf.org', or you may write to the
** Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139 USA.
**
** THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND,
** NOT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR
** OF THIS SOFTWARE, ASSUMES _NO_ RESPONSIBILITY FOR ANY
** CONSEQUENCE RESULTING FROM THE USE, MODIFICATION, OR
** REDISTRIBUTION OF THIS SOFTWARE. 
** 
*/


package com.mlp.net;


import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;
import java.text.*;

public class SimpleSMTP
	{
	private static final String		RCS_ID = "$Id: SimpleSMTP.java,v 1.1.1.1 1997/11/03 18:03:12 time Exp $";
	private static final String		RCS_REV = "$Revision: 1.1.1.1 $";

	public static final int			SMTP_PORT = 25;
	public static final String		EOL = "\r\n";

	protected Socket			sock = null;
	protected BufferedReader	recv = null;
	protected PrintWriter		send = null;


	/**
	 *   Create an smtp object connected to the specified host
	 *
	 *   @param hostid The host to connect to.
	 *   @exception UnknownHostException
	 *   @exception IOException
	 */
	public
	SimpleSMTP( String hostname )
			throws UnknownHostException, IOException
		{
        this( hostname, SimpleSMTP.SMTP_PORT );
		}

    public
	SimpleSMTP( String hostid, int port )
			throws UnknownHostException, IOException
		{
		this.sock = new Socket( hostid, port );
        
		this.send =
			new PrintWriter( sock.getOutputStream() );

		this.recv = new BufferedReader(
			new InputStreamReader( sock.getInputStream() ) );

        String replyStr = this.readLine();
        if ( ! replyStr.startsWith( "220" ) )
			{
			throw new ProtocolException( replyStr );
			}

        for ( ; replyStr.indexOf( '-' ) == 3 ; )
			{
            replyStr = this.readLine();
            if ( ! replyStr.startsWith("220") )
				{
				throw new ProtocolException( replyStr );
				}
			}
		}

	public
	SimpleSMTP( InetAddress address )
			throws IOException
		{
		this( address, SimpleSMTP.SMTP_PORT );
		}

    public
	SimpleSMTP( InetAddress address, int port )
			throws IOException
		{
		this.sock = new Socket( address, port );
		
		this.send =
			new PrintWriter( sock.getOutputStream() );

		this.recv = new BufferedReader(
			new InputStreamReader( sock.getInputStream() ) );

		String replyStr = this.readLine();
		if ( ! replyStr.startsWith("220") )
			{
			throw new ProtocolException( replyStr );
			}

		for ( ; replyStr.indexOf('-') == 3 ; )
			{
			replyStr = this.readLine();
			if ( ! replyStr.startsWith("220") )
				{
				throw new ProtocolException( replyStr );
				}
			}
		}

	private String
	readLine()
		{
		String	line = null;

		try { line = this.recv.readLine(); }
		catch ( IOException ex )
			{
			line = null;
			System.err.println
				( "ERROR reading smtp reply:\n    " + ex.getMessage() );
			}

		return line;
		}

	private boolean
	sendLine( String line )
		{
		boolean result = true;

		this.send.print( line );
		this.send.print( EOL );

		// checkError() does a flush also...
		if ( this.send.checkError() )
			{
			result = false;
			// UNDONE - what do you want to do when there's an error.
			// It can be a "format" error, but I think the only thing
			// we'll ever see here is an io error.
			}

		return result;
		}

    public void
	sendMailMsg(
			String fromAddress, String toAddress,
			String subject, String body )
		throws IOException, ProtocolException
		{
		String			response;
		InetAddress		localAddr;

		try { localAddr = InetAddress.getLocalHost(); }
		catch ( UnknownHostException ex )
			{
			System.err.println
				("ERROR Can not determine local address.");
			throw ex;
			}

		String host = localAddr.getHostName();

		int index = fromAddress.indexOf( '@' );
		if ( index < 0 || index == (fromAddress.length() - 1) )
			{
			fromAddress = fromAddress + "@" + host;
			}

		this.sendLine( "HELO " + host );

		response = this.readLine();
		if ( ! response.startsWith("250") )
			{
			throw new ProtocolException( response );
			}

		this.sendLine( "MAIL FROM: " + fromAddress );

		response = this.readLine();
		if ( ! response.startsWith("250") )
			{
			throw new ProtocolException( response );
			}

		this.sendLine( "RCPT TO: " + toAddress );

		response = this.readLine();
		if ( ! response.startsWith("250") )
			{
			throw new ProtocolException( response );
			}

		this.sendLine( "DATA" );

		response = this.readLine();
		if ( ! response.startsWith("354") )
			{
			throw new ProtocolException( response );
			}

		this.sendLine( "From: " + fromAddress );
		this.sendLine( "To: " + toAddress );
		this.sendLine( "Subject: " + subject );

		// UNDONE - Handling of the timezone here and in the
		//          formatSMTPDate() method!
		TimeZone tz = TimeZone.getTimeZone( "GMT" );
		Date now = new Date();
		this.sendLine( "Date: " + this.formatSMTPDate( now, tz ) );

		this.sendLine( "X-Mailer: com.careersite.jobsearch.SimpleSMTP" );

		// Send a blank line to terminate the header
		this.sendLine( "" );

		// Now send the message proper
		this.sendLine( body );
		this.sendLine( "." );

		response = this.readLine();
		if ( ! response.startsWith( "250" ) )
			{
			throw new ProtocolException( response );
			}
		}

	public void
	close()
		{
		try {
			this.sendLine( "QUIT" );
			sock.close();
			}
		catch (IOException ioe)
			{
			}
		}

    protected void
	finalize()
			throws Throwable
		{
		this.close();
		super.finalize();
		}

	/* Tue, 16 Sep 1997 15:39:01 -0400 (EDT) */

	public String
	formatSMTPDate( Date date, TimeZone tz )
			throws IllegalArgumentException
		{
		SimpleDateFormat dateFormat;
		Locale loc = Locale.US;

		dateFormat = new SimpleDateFormat( "EEE", loc );
		dateFormat.setTimeZone( tz );
		String day = dateFormat.format( date );
		day = day.substring( 0, 3 );

		dateFormat = new SimpleDateFormat( "MMM", loc );
		dateFormat.setTimeZone( tz );
		String month = dateFormat.format( date );
		month = month.substring( 0, 3 );

		dateFormat = new SimpleDateFormat( "dd", loc );
		dateFormat.setTimeZone( tz );
		String dayNum = dateFormat.format( date );

		dateFormat = new SimpleDateFormat( "yyyy HH:mm:ss", loc );
		dateFormat.setTimeZone( tz );
		String rest = dateFormat.format( date );

		String result = new String
			( day + ", " + dayNum + " " + month + " " + rest + " +0000" );

		return result;
		}

	}
