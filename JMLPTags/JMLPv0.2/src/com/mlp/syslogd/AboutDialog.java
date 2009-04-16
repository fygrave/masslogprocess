package com.mlp.syslogd;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import com.mlp.util.AWTUtilities;
import com.mlp.util.UserProperties;
import com.mlp.util.util.GUtil;
import com.mlp.widget.BorderPanel;


/**
 * Shows the application's "About" dialog box.
 *
 * @version $Revision: 1.1.1.1 $
 * @author Timothy Gerard Endres,
 *  updated by Colin Doug
 */

public class
AboutDialog extends Dialog
		implements ActionListener, WindowListener
	{
	public static final String		RCS_ID = "$Id: AboutDialog.java,v 1.1.1.1 1998/02/22 05:47:55 time Exp $";
	public static final String		RCS_REV = "$Revision: 1.1.1.1 $";
	public static final String		RCS_NAME = "$Name:  $";

	private String		messageString;
	private TextArea	messageText;

	public
	AboutDialog(Frame parent)
		{
		super( parent, GUtil.getString("About JMassLogPro"), true );

		this.messageString = null;

		this.establishDialogContents();

		this.pack();

		int width =
			UserProperties.getProperty
				( "aboutDialog.width", 540 );
		int height =
			UserProperties.getProperty
				( "aboutDialog.height", 320 );

		this.setSize( width, height );

		Point location =
			AWTUtilities.computeDialogLocation
				( this, width, height );

		this.setLocation( location.x, location.y );

		this.addWindowListener( this );
		}

    public void
    actionPerformed( ActionEvent evt )
        {
	    String command = evt.getActionCommand();

		if ( command.compareTo( "OK" ) == 0 )
			{
			this.dispose();
			}
        }

	public void
	establishDialogContents() 
		{
		Button			button;
		BorderPanel		controlPanel;

 		this.messageText = new TextArea( 12, 64 );
		this.messageText.setEditable( false );
		this.messageText.setFont(
			UserProperties.getFont(
				"aboutDialog.font",
				new Font( "Serif", Font.BOLD, 12 ) ) );

		this.messageText.setText(
			"JMassLogPro, version " +
			SyslogD.VERSION_STR +
			", a Massive Log Process Server\n" +
			"\n" +
			"Written by Tim Endres,Colin Doug,GierWu,ShowRun, colindoug09@gmail.com\n" +
			"Copyright (c) 1997,2009 by Timothy Gerard Endres,Colin Doug,GierWu,ShowRun\n" +
			"\n" +
			"JMassLogPro is free software, which is licensed to you under the\n" +
			"GNU General Public License, version 3. Please see the file\n" +
			"LICENSEv3 for more details, or visit 'www.gnu.org'.\n" +
			"\n" +
			"This software is provided AS-IS, with ABSOLUTELY NO WARRANTY.\n" +
			"\n" +
			"YOU ASSUME ALL RESPONSIBILITY FOR ANY AND ALL CONSEQUENCES\n" +
			"THAT MAY RESULT FROM THE USE OF THIS SOFTWARE!"
			); 

		controlPanel = new BorderPanel
				( 7, 2, 5, BorderPanel.RIDGE );
		controlPanel.setLayout( new GridLayout( 1, 1, 20, 20 ) );

		button = new Button( "Ok" );
		button.addActionListener( this );
		button.setActionCommand( "OK" );
		controlPanel.add( button );

		this.setLayout( new GridBagLayout() );

		AWTUtilities.constrain(
			this, this.messageText,
			GridBagConstraints.BOTH,
			GridBagConstraints.CENTER,
			0, 0, 1, 1, 1.0, 1.0 );

		AWTUtilities.constrain(
			this, controlPanel,
			GridBagConstraints.HORIZONTAL,
			GridBagConstraints.CENTER,
			0, 1, 1, 1, 1.0, 0.0 );

		}

	public void
	windowOpened(WindowEvent e)
		{
		}

	public void
	windowClosing(WindowEvent e)
		{
		this.dispose();
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

	}
