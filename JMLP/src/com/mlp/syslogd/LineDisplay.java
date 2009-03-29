
package com.mlp.syslogd;

import com.mlp.syslog.SyslogDefs;
import com.mlp.util.AWTUtilities;
import com.mlp.util.UserProperties;
import com.mlp.widget.BorderPanel;
import com.mlp.widget.SimpleLabel;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;


public class
LineDisplay extends Frame
		implements SyslogDisplayInterface, WindowListener
	{
	public static final String		RCS_ID = "$Id: LineDisplay.java,v 1.1.1.1 1998/02/22 05:47:54 time Exp $";
	public static final String		RCS_REV = "$Revision: 1.1.1.1 $";
	public static final String		RCS_NAME = "$Name:  $";

	private static final String		DEFAULT_FIELD_LIST = "TFPHNIM";

	protected String				fieldList;
	protected SimpleLabel			messageLabel;


	public
	LineDisplay()
		{
		super();
		this.messageLabel = null;
		this.fieldList = LineDisplay.DEFAULT_FIELD_LIST;		
		}

	public void
	setFieldList( String fieldList )
		{
		if ( fieldList == null || fieldList.equals( "*" ) )
			this.fieldList = LineDisplay.DEFAULT_FIELD_LIST;
		else
			this.fieldList = fieldList;
		}

	public void
	openDisplay(
			String title, int bufLength,
			int x, int y, int w, int h,
			String[] parameters )
		{
		this.setTitle( title );

		this.establishContents();

		if ( parameters.length > 0 )
			this.setFieldList( parameters[0] );

		if ( parameters.length > 2 )
			{
			Font labelFont =
				com.mlp.util.AWTUtilities.getFont( parameters[2] );

			if ( labelFont != null )
				{
				this.messageLabel.setFont( labelFont );
				}
			}

		this.setLocation( x, y );

		this.setSize( w, h );

		if ( parameters.length < 2 )
			{
			this.show();
			}
		else if ( parameters[1].equalsIgnoreCase( "visible" ) )
			{
			this.show();
			}

		this.addWindowListener( this );
		}
	
	public void
	closeDisplay()
		{
		this.dispose();
		}

	public void
	bringToFront()
		{
		this.show();
		this.toFront();
		}

	public void
	sendToBack()
		{
		this.show();
		this.toBack();
		}

	public void
	hideDisplay()
		{
		this.setVisible( false );
		}

	public void
	showDisplay()
		{
		this.setVisible( true );
		}

	public void
	restart()
		{
		}

	public void
	processMessage( SyslogMessage logMsg )
		{
		this.messageLabel.setText
			( this.buildLogMessage( logMsg ) );
		}

	public void
	windowOpened(WindowEvent e)
		{
		}

	public void
	windowClosing(WindowEvent e)
		{
		this.hideDisplay();
		}

	public void
	windowClosed(WindowEvent e)
		{
		}

	public void
	windowActivated(WindowEvent e)
		{
		}

	public void
	windowDeactivated(WindowEvent e)
		{
		}

	public void
	windowIconified(WindowEvent e)
		{
		}

	public void
	windowDeiconified(WindowEvent e)
		{
		}

	private String
	buildLogMessage( SyslogMessage logMsg )
		{
		StringBuffer buf = new StringBuffer();

		int length = this.fieldList.length();

		for ( int fIdx = 0 ; fIdx < length ; ++fIdx )
			{
			String seper = "";

			char ch = this.fieldList.charAt( fIdx );

			switch ( ch )
				{
				case 'F': case 'f':
					if ( Character.isUpperCase(ch) )
						{
						seper = " ";
						buf.append( SyslogDefs.getFacilityName
											( logMsg.facility ) );
						}
					break;

				case 'P': case 'p':
					if ( Character.isUpperCase(ch) )
						{
						seper = " ";
						buf.append( SyslogDefs.getPriorityName
											( logMsg.priority ) );
						}
					break;

				case 'H': case 'h':
					if ( Character.isUpperCase(ch) )
						{
						seper = " ";
						buf.append( logMsg.hostName );
						}
					break;

				case 'M': case 'm':
					if ( Character.isUpperCase(ch) )
						{
						seper = " ";
						buf.append( logMsg.message );
						}
					break;

				case 'N': case 'n':
					seper = " ";
					if ( Character.isUpperCase(ch) )
						{
						seper = " ";
						buf.append( logMsg.processName );
						}
					break;

				case 'I': case 'i':
					if ( Character.isUpperCase(ch) )
						{
						seper = " ";
						buf.append( "[" );
						buf.append( logMsg.processId );
						buf.append( "]" );
						}
					break;

				case 'T': case 't':
					if ( Character.isUpperCase(ch) )
						{
						seper = " ";
						buf.append( logMsg.timestamp );
						}
					break;
				}

			buf.append( seper );
			}
		
		return buf.toString();
		}

	public void
	establishContents()
		{
		int			row;
		Label		lbl;
		Panel		pan;
		int			cols, rows;
		String		title;

		Color backColor =
			UserProperties.getColor(
				"lineDisplayWindow.bg",
				new Color( 200, 215, 250 ) );
		this.setBackground( backColor );

		Font labelFont =
			UserProperties.getFont(
				"lineDisplayWindow.font",
				new Font( "Serif", Font.BOLD, 12 ) );

		this.setLayout( new GridBagLayout() );

		pan = new BorderPanel( 5, 2, 2, BorderPanel.RIDGE );
		pan.setLayout( new GridBagLayout() );

		this.messageLabel = new SimpleLabel( "" );
		this.messageLabel.setFont( labelFont );
		AWTUtilities.constrain(
			pan, this.messageLabel,
			GridBagConstraints.BOTH,
			GridBagConstraints.CENTER,
			0, 0, 1, 1, 1.0, 1.0 );

		this.setLayout( new BorderLayout() );
		this.add( pan, "Center" );
		}
	}
