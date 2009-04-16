/*
** AWT Widget Package.
** Copyright (c) 1997 by Timothy Gerard Endres
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

package com.mlp.widget;

import java.awt.*;
import java.awt.event.*;


/**
 * Shows an image in a canvas component.
 * When clicked, will send "IMAGECLICK" action to listeners.
 * Supports a popup menu on the image.
 *
 * @version $Revision: 1.2 $
 * @author Timothy Gerard Endres,
 *    <a href="mailto:time@ice.com">time@ice.com</a>.
 */

public class
ImageCanvas extends Canvas
		implements MouseListener, MouseMotionListener
	{
	static public final String		RCS_ID = "$Id: ImageCanvas.java,v 1.2 1998/02/21 23:55:27 time Exp $";
	static public final String		RCS_REV = "$Revision: 1.2 $";

	protected Image  			image;
	protected int				width, height;
	protected Dimension			mDim, pDim;
	protected boolean			fitImage;
	protected boolean			isPopupEvent;
	protected PopupMenu			popup;
    protected ActionListener	aListener;

	protected String			actionCommand;


	public
	ImageCanvas( Image image )
		{
		super();

		this.popup = null;
		this.fitImage = true;
		this.actionCommand = "IMAGECLICK";

		this.setImage( image );
        this.addMouseListener( this );

		this.repaint();
		}

	public void
	setActionCommand( String command )
		{
		this.actionCommand = command;
		}
	
	public String
	setActionCommand()
		{
		return this.actionCommand;
		}
	
	public void
	setPopupMenu( PopupMenu popup )
		{
		this.popup = popup;
		}

	public boolean
	getFitImage()
		{
		return this.fitImage;
		}

	public void
	setFitImage( boolean fitImage )
		{
		this.fitImage = fitImage;
		}

	public void
	setImage( Image image )
		{
		this.image = image;
		
		if ( this.image != null )
			{
			this.prepareImage( image, this );

			MediaTracker tracker = new MediaTracker( this );

			tracker.addImage( this.image, 0 );

			try { tracker.waitForAll(); }
			catch ( InterruptedException ex )
				{
				System.err.println
					( "ImageCanvas: media tracker interrupted!\n"
						+ "   " + ex.getMessage() );
				}

			this.computeDimensions();
			}
		}

	public synchronized void
	paint( Graphics g )
		{
		if ( ! this.getParent().isEnabled() )
			return;

		Dimension d = this.getSize();

		if ( this.image != null )
			{
			int y = 0;
			int x = 0;

			if ( this.fitImage )
				{
				Color bg = this.getBackground();

				int width = this.image.getWidth( this );
				int height = this.image.getHeight( this );

				double wPct = 0.0;
				if ( width > d.width )
					{
					wPct = ( (double)( width - d.width )
								/ (double)width );
					}

				double hPct = 0.0;
				if ( height > d.height )
					{
					hPct = ( (double)( height - d.height )
								/ (double)height );
					}

				double pct = 0.0;
				if ( hPct > 0.0 || wPct > 0.0 )
					{
					pct = (hPct > wPct) ? hPct : wPct;
					width -= (pct * width);
					height -= (pct * height);
					}

				g.drawImage
					( this.image, x, y,
						width, height, bg, null );
				}
			else
				{
				g.drawImage( this.image, x, y, null );
				}
			}
		}

	public boolean
	isFocusTraversable()
		{
		return false;
		}

	public void
	computeDimensions()
		{
		int width = this.image.getWidth( this );
		int height = this.image.getHeight( this );

		this.mDim = new Dimension( width, height );

		this.pDim =
			new Dimension
				( this.mDim.width, this.mDim.height );
		}

	public Dimension
	getPreferredSize()
		{
		return this.pDim;
		}

	public Dimension
	getMinimumSize()
		{
		return this.mDim;
		}

	public void
	addNotify()
		{
		super.addNotify();
		this.computeDimensions();
		}

	protected void
	processActionEvent( ActionEvent event )
		{
		if ( this.aListener != null)
			{
			this.aListener.actionPerformed( event );
			}
		}

    /**
     * Adds the specified action listener to receive action events
     * from this button.
     * @param l the action listener
     */ 
    public void
	addActionListener( ActionListener listener )
		{
		this.aListener =
			AWTEventMulticaster.add( this.aListener, listener );
		}

    /**
     * Removes the specified action listener so it no longer receives
     * action events from this button.
     * @param l the action listener
     */ 
    public void
	removeActionListener( ActionListener listener )
		{
		this.aListener =
			AWTEventMulticaster.remove( this.aListener, listener );
		}

	public void
	mousePressed( MouseEvent event )
		{
		}

	public void
	mouseReleased( MouseEvent event )
		{
		if ( event.isPopupTrigger() )
			{
			this.isPopupEvent = true;

			if ( this.popup != null )
				this.popup.show
					( this, event.getX(), event.getY() );
			}
		}

	public void
	mouseClicked( MouseEvent event )
		{
		if ( this.isPopupEvent )
			{
			this.isPopupEvent = false;
			return;
			}

		ActionEvent aEvent = new ActionEvent
			( this, ActionEvent.ACTION_PERFORMED, this.actionCommand );

		this.processActionEvent( aEvent );
		}

	public void
	mouseEntered( MouseEvent event )
		{
		}

	public void
	mouseExited( MouseEvent event )
		{
		}

	public void
	mouseDragged( MouseEvent event )
		{
		}

	public void
	mouseMoved( MouseEvent event )
		{
		}

	}
