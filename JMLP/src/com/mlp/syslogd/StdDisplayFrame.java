
package com.mlp.syslogd;

import com.mlp.syslog.SyslogDefs;
import com.mlp.util.AWTUtilities;
import com.mlp.util.UserProperties;
import com.mlp.widget.BorderPanel;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;


public class
StdDisplayFrame extends Frame
		implements ActionListener, ItemListener,
					SyslogDisplayInterface, WindowListener
	{
	public static final String		RCS_ID = "$Id: StdDisplayFrame.java,v 1.1.1.1 1998/02/22 05:47:54 time Exp $";
	public static final String		RCS_REV = "$Revision: 1.1.1.1 $";
	public static final String		RCS_NAME = "$Name:  $";

	private static final String		DEFAULT_FIELD_LIST = "TFPHNIM";

	protected Vector		logBuffer;
	protected int			logBufferSize;

	protected MenuBar		mBar;
	protected Menu			mFile;
	protected java.awt.List			msgList;

	protected String			fieldList;

	protected boolean			showFacility;
	protected boolean			showPriority;
	protected boolean			showTimestamp;
	protected boolean			showHostname;
	protected boolean			showProcessName;
	protected boolean			showProcessId;
	protected boolean			showMessage;

	protected CheckboxMenuItem	menuItemF;
	protected CheckboxMenuItem	menuItemP;
	protected CheckboxMenuItem	menuItemT;
	protected CheckboxMenuItem	menuItemH;
	protected CheckboxMenuItem	menuItemN;
	protected CheckboxMenuItem	menuItemI;
	protected CheckboxMenuItem	menuItemM;


	public
	StdDisplayFrame()
		{
		super();
		this.logBufferSize = 0;
		this.logBuffer = null;

		this.showFacility = true;
		this.showPriority = true;
		this.showTimestamp = true;
		this.showHostname = true;
		this.showProcessName = true;
		this.showProcessId = true;
		this.showMessage = true;

		this.fieldList = StdDisplayFrame.DEFAULT_FIELD_LIST;		
		}

    public void
    itemStateChanged( ItemEvent event )
        {
		boolean		redisplay = false;

		if ( event.getItemSelectable() == this.menuItemM )
			{
			redisplay = true;
			this.showMessage = this.menuItemM.getState();
			}
		else if ( event.getItemSelectable() == this.menuItemF )
			{
			redisplay = true;
			this.showFacility = this.menuItemF.getState();
			}
		else if ( event.getItemSelectable() == this.menuItemP )
			{
			redisplay = true;
			this.showPriority = this.menuItemP.getState();
			}
		else if ( event.getItemSelectable() == this.menuItemT )
			{
			redisplay = true;
			this.showTimestamp = this.menuItemT.getState();
			}
		else if ( event.getItemSelectable() == this.menuItemN )
			{
			redisplay = true;
			this.showProcessName = this.menuItemN.getState();
			}
		else if ( event.getItemSelectable() == this.menuItemI )
			{
			redisplay = true;
			this.showProcessId = this.menuItemI.getState();
			}
		else if ( event.getItemSelectable() == this.menuItemH )
			{
			redisplay = true;
			this.showHostname = this.menuItemH.getState();
			}

		if ( redisplay )
			{
			this.redisplayList();
			}
		}

	public void
	redisplayList()
		{
		this.msgList.setVisible( false );

		this.msgList.removeAll();

		int size = this.logBuffer.size();

		for ( int i = 0 ; i < size ; ++i )
			{
			String itemStr =
				this.getLogMessage( i );

			this.msgList.addItem( itemStr, i );
			}

		this.msgList.setVisible( true );
		}

    public void
    actionPerformed( ActionEvent event )
        {
	    String command = event.getActionCommand();
		
		if ( command.equals( "HIDE" ) )
			{
			this.hideDisplay();
			}
        }

	private void
	establishMenuBar()
		{
        MenuItem	mItem;

		this.mBar = new MenuBar();

		this.mFile = new Menu( "File", true );
		this.mBar.add( this.mFile );

        mItem = new MenuItem( "Hide Display" );
        mItem.setActionCommand( "HIDE" );
        mItem.addActionListener( this );
	    this.mFile.add( mItem );

		this.addAdditionalMenus( mBar );

		this.setMenuBar( this.mBar );
		}

	public void
	addAdditionalMenus( MenuBar menuBar )
		{
        CheckboxMenuItem	mItem;

		Menu methodMenu = new Menu( "Fields", true );
		this.mBar.add( methodMenu );

		this.fieldList = fieldList;
		int len = fieldList.length();
		
		for ( int fIdx = 0 ; fIdx < len ; ++fIdx )
			{
			mItem = null;
			char ch = this.fieldList.charAt( fIdx );

			switch ( ch )
				{
				case 'F': case 'f':
					mItem = new CheckboxMenuItem( "Facility" );
					mItem.setActionCommand( "FAC" );
					this.menuItemF = mItem;
					break;
				case 'P': case 'p':
					mItem = new CheckboxMenuItem( "Priority" );
					mItem.setActionCommand( "PRI" );
					this.menuItemP = mItem;
					break;
				case 'T': case 't':
					mItem = new CheckboxMenuItem( "Timestamp" );
					mItem.setActionCommand( "STAMP" );
					this.menuItemT = mItem;
					break;
				case 'H': case 'h':
					mItem = new CheckboxMenuItem( "Hostname" );
					mItem.setActionCommand( "HOST" );
					this.menuItemH = mItem;
					break;
				case 'N': case 'n':
					mItem = new CheckboxMenuItem( "ProcessName" );
					mItem.setActionCommand( "NAME" );
					this.menuItemN = mItem;
					break;
				case 'I': case 'i':
					mItem = new CheckboxMenuItem( "ProcessID" );
					mItem.setActionCommand( "ID" );
					this.menuItemI = mItem;
					break;
				case 'M': case 'm':
					mItem = new CheckboxMenuItem( "Message" );
					mItem.setActionCommand( "MSG" );
					this.menuItemM = mItem;
					break;
				}

			if ( mItem != null )
				{
				mItem.setState( Character.isUpperCase( ch ) );
				mItem.addItemListener( this );
				methodMenu.add( mItem );
				}
			}
		}

	public void
	setFieldList( String fieldList )
		{
		if ( fieldList == null
				|| fieldList.equals( "*" ) )
			this.fieldList = StdDisplayFrame.DEFAULT_FIELD_LIST;
		else
			this.fieldList = fieldList;

		int len = fieldList.length();
		for ( int fIdx = 0 ; fIdx < len ; ++fIdx )
			{
			char ch = this.fieldList.charAt( fIdx );
			switch ( ch )
				{
				case 'F': case 'f':
					this.showFacility = Character.isUpperCase( ch );
					break;
				case 'P': case 'p':
					this.showPriority = Character.isUpperCase( ch );
					break;
				case 'T': case 't':
					this.showTimestamp = Character.isUpperCase( ch );
					break;
				case 'H': case 'h':
					this.showHostname = Character.isUpperCase( ch );
					break;
				case 'N': case 'n':
					this.showProcessName = Character.isUpperCase( ch );
					break;
				case 'I': case 'i':
					this.showProcessId = Character.isUpperCase( ch );
					break;
				case 'M': case 'm':
					this.showMessage = Character.isUpperCase( ch );
					break;
				}
			}
		}

	public void
	openDisplay(
			String title, int bufLength,
			int x, int y, int w, int h,
			String[] parameters )
		{
		this.logBufferSize = bufLength;
		this.logBuffer =
			new Vector( this.logBufferSize );

		if ( parameters != null && parameters.length > 0 )
			this.setFieldList( parameters[0] );

		this.setTitle( title );

		this.establishMenuBar();

		this.setMenuBar( this.mBar );

		this.establishMenuBar();

		this.establishContents();

		if ( parameters != null && parameters.length > 2 )
			{
			Font listFont =
				com.mlp.util.AWTUtilities.getFont( parameters[2] );

			if ( listFont != null )
				{
				this.msgList.setFont( listFont );
				}
			}

		this.setLocation( x, y );

		this.setSize( w, h );

        if ( parameters != null )
            {
    		if ( parameters.length < 2 )
    			{
    			this.show();
    			}
    		else if ( parameters[1].equalsIgnoreCase( "visible" ) )
    			{
    			this.show();
    			}
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

	private String
	getLogMessage( int index )
		{
		SyslogMessage logMsg = (SyslogMessage)
				this.logBuffer.elementAt( index );

		if ( logMsg != null )
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
						if ( this.showFacility )
							{
							seper = " ";
							buf.append( SyslogDefs.getFacilityName
												( logMsg.facility ) );
							}
						break;

					case 'P': case 'p':
						if ( this.showPriority )
							{
							seper = " ";
							buf.append( SyslogDefs.getPriorityName
												( logMsg.priority ) );
							}
						break;

					case 'H': case 'h':
						if ( this.showHostname )
							{
							seper = " ";
							buf.append( logMsg.hostName );
							}
						break;

					case 'M': case 'm':
						if ( this.showMessage )
							{
							seper = " ";
							buf.append( logMsg.message );
							}
						break;

					case 'N': case 'n':
						if ( this.showProcessName )
							{
							seper = " ";
							buf.append( logMsg.processName );
							}
						break;

					case 'I': case 'i':
						if ( this.showProcessId )
							{
							seper = " ";
							buf.append( "[" );
							buf.append( logMsg.processId );
							buf.append( "]" );
							}
						break;

					case 'T': case 't':
						if ( this.showTimestamp )
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
		else
			{
			return "ERROR null logMsg at '" + index + "'";
			}
		}

	public void
	processMessage( SyslogMessage logMsg )
		{
		this.logBuffer.insertElementAt( logMsg, 0 );

		String itemStr =
			this.getLogMessage( 0 );

		this.msgList.addItem( itemStr, 0 );

		if ( this.logBuffer.size() > this.logBufferSize )
			{
			this.logBuffer.removeElementAt
				( this.logBuffer.size() - 1 );
			}
		if ( this.msgList.getItemCount() > this.logBufferSize )
			{
			this.msgList.delItem
				( this.msgList.getItemCount() - 1 );
			}
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
				"stdDisplayWindow.bg",
				new Color( 200, 215, 250 ) );
		this.setBackground( backColor );

		Font textFont =
			UserProperties.getFont(
				"stdDisplayWindow.text.font",
				new Font( "Serif", Font.PLAIN, 14 ) );
		Font labelFont =
			UserProperties.getFont(
				"stdDisplayWindow.labels.font",
				new Font( "Serif", Font.BOLD, 14 ) );
		Font listFont =
			UserProperties.getFont(
				"stdDisplayWindow.list.font",
				new Font( "Serif", Font.PLAIN, 14 ) );

		this.setLayout( new GridBagLayout() );

		pan = new BorderPanel( 5, 2, 2, BorderPanel.RIDGE );
		pan.setLayout( new GridBagLayout() );
		AWTUtilities.constrain(
			this, pan,
			GridBagConstraints.BOTH,
			GridBagConstraints.CENTER,
			0, 0, 1, 1, 1.0, 1.0 );

		this.msgList = new java.awt.List();
		this.msgList.setFont( listFont );
		AWTUtilities.constrain(
			pan, this.msgList,
			GridBagConstraints.BOTH,
			GridBagConstraints.CENTER,
			0, 0, 1, 1, 1.0, 1.0 );
		}
	}
