

package com.mlp.syslogd;

import java.awt.Dimension;
import java.awt.Point;

import com.mlp.util.ClassUtilities;


public class
DisplayEntry
	{
	public static final String		RCS_ID = "$Id: DisplayEntry.java,v 1.1.1.1 1998/02/22 05:47:54 time Exp $";
	public static final String		RCS_REV = "$Revision: 1.1.1.1 $";
	public static final String		RCS_NAME = "$Name:  $";

	private String			name;
	private String			className;
	private String			title;
	private int				bufLength;
	private int				x;
	private int				y;
	private int				width;
	private int				height;

	private String[]		parameters;

	private SyslogDisplayInterface	display;


	public
	DisplayEntry(
			String name, String className, String title,
			int bufLength, int x, int y, int width, int height,
			String[] parameters )
		{
		this.name = name;
		this.title = title;
		this.className = className;
		this.bufLength = bufLength;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.display = null;
		this.parameters = parameters;
		}

	public String
	getName()
		{
		return this.name;
		}

	public String
	getTitle()
		{
		return this.title;
		}

	public String
	getClassName()
		{
		return this.className;
		}

	public Point
	getLocation()
		{
		return new Point( this.x, this.y );
		}

	public Dimension
	getSize()
		{
		return new Dimension( this.width, this.height );
		}

	public SyslogDisplayInterface
	createDisplay()
		{
		String cName = this.className;

		if ( cName.equals( "STD" ) )
			cName = "com.mlp.syslogd.StdDisplayFrame";
		
		Class displayClass = null;

		try {
			displayClass = Class.forName( cName );
			}
		catch ( ClassNotFoundException ex )
			{
			System.err.println
				( "ERROR failed loading class '"
					+ cName + "':\n\t" + ex.getMessage() );
			return null;
			}

		String interfaceName =
			"com.mlp.syslogd.SyslogDisplayInterface";

		if ( ! ClassUtilities.implementsInterface
				( displayClass, interfaceName ) )
			{
			System.err.println
				( "ERROR class '" + cName
					+ "' does not implement the interface '"
					+ interfaceName + "'." );
			return null;
			}

		SyslogDisplayInterface result = null;

		try {
			result =
				(SyslogDisplayInterface)
					displayClass.newInstance();
			}
		catch ( Exception ex )
			{
			System.err.println
				( "ERROR failed instantiating object '"
					+ cName + "':\n\t" + ex.getMessage() );
			return null;
			}

		result.openDisplay
			( this.title, this.bufLength,
				this.x, this.y, this.width, this.height,
					this.parameters );

		return result;
		}

	}


