/*
** Tim Endres' utilities package.
** Copyright (c) 1997 by Tim Endres
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

package com.mlp.util;

import java.io.*;
import java.net.*;


public class
ResourceUtilities
	{
	/**
	 * Copies a named resource to a File.
	 *
	 * @param resourceURL The name of the resource to copy.
	 * @param destFile The File to copy the resource's contents into.
	 */

	public static void
	copyResourceToFile( String resourceURL, File destFile )
		throws IOException
		{
		BufferedInputStream in =
			new BufferedInputStream(
				ResourceUtilities.openNamedResource
					( resourceURL ) );

		BufferedOutputStream out =
			new BufferedOutputStream(
				new FileOutputStream( destFile ) );

		byte[] buf = new byte[ 4096 ];

		for ( ; ; )
			{
			int numRead = in.read( buf, 0, buf.length );
			
			if ( numRead == -1 )
				break;

			out.write( buf, 0, numRead );
			}

		in.close();
		out.close();
		}

	/**
	 * Opens a resource and return an InputStream that will read
	 * the contents of the resource. A resource URL is used to name
	 * the resource. The URL can be any valid URL to which you can
	 * establish a connect, including web pages, ftp site files, and
	 * files in the CLASSPATH including JAR files.
	 * <p>
	 * To open a file on the CLASSPATH, use a full class name, with
	 * the slash syntax, such "/com/ice/util/ResourceUtilities.class".
	 * Note the leading slash.
	 *
	 * @param path The properties resource's name.
	 * @param props The system properties to add properties into.
	 * @return The InputStream that will read the resource's contents.
	 */

	public static InputStream
	openNamedResource( String resourceURL )
		throws java.io.IOException
		{
		InputStream	in = null;
		boolean		result = false;
		boolean		httpURL = false;
		URL			propsURL = null;

		//
		// UNDONE REVIEW
		// I really should be getting the URL's protocol, when it
		// is a "full" URL, and checking for the different possible
		// error returns for http, ftp, et.al.
		//
		try { propsURL = new URL( resourceURL ); }
			catch ( MalformedURLException ex )
				{ propsURL = null; }

		if ( propsURL == null )
			{
			propsURL =
				ResourceUtilities.class.getResource( resourceURL );

			if ( propsURL == null
					&& resourceURL.startsWith( "FILE:" ) )
				{
				try {
					in = new FileInputStream
							( resourceURL.substring( 5 ) );
					return in;
					}
				catch ( FileNotFoundException ex )
					{
					in = null;
					propsURL = null;
					}
				}
			}
		else
			{
			String protocol = propsURL.getProtocol();
			httpURL = protocol.equals( "http" );
			}

		if ( propsURL != null )
			{
			URLConnection urlConn =
				propsURL.openConnection();

			if ( httpURL )
				{
				String hdrVal = urlConn.getHeaderField(0);
				if ( hdrVal != null )
					{
					String code =
						HTTPUtilities.getResultCode( hdrVal );

					if ( code != null )
						{
						if ( ! code.equals( "200" ) )
							{
							throw new java.io.IOException
								( "status code = " + code );
							}
						}
					}
				}

			in = urlConn.getInputStream();
			}

		if ( in == null )
			throw new java.io.IOException
				( "could not locate resource '"
					+ resourceURL + "'" );

		return in;
		}

	}

