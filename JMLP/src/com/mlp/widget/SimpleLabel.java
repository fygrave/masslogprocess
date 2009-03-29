/*
** A simple Label widget.
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

public class
SimpleLabel extends Component
		implements FocusListener
	{
	protected String  	label;
	protected int		labelWidth;
	protected int		labelHeight;

	private boolean		kludgeStyle;
	private int			styleToKludge;

	protected int		vMargin;
	protected int		hMargin;
	protected int		borderWidth;

	protected int		fontHeight;
	protected int		fontAscent;
	protected int		fontDescent;
	protected int		fontLeading;

	protected Color		borderColor;
	protected Dimension	mDim, pDim;


	public
	SimpleLabel( String label )
		{
		super();

		this.borderWidth = 0;
		this.borderColor = Color.black;

		this.kludgeStyle = false;
		this.styleToKludge = 0;

		this.hMargin = 2;
		this.vMargin = 0;

		this.labelWidth = 0;
		this.fontHeight = 0;
		this.fontAscent = 0;
		this.fontDescent = 0;

		this.label = label;
		}
	
	public String
	getText()
		{
		return this.label;
		}

	public void
	setText( String text )
		{
		this.label = text;
		this.repaint();
		}

	public void
	setStyle( int style )
		{
		Font font = this.getFont();

		if ( font != null ) // in case we're not graphics-ed yet.
			{
			Font newFont = new
				Font( font.getName(), style, font.getSize() );
		
			this.setFont( newFont );
			this.kludgeStyle = false;
			}
		else
			{
			this.kludgeStyle = true;
			this.styleToKludge = style;
			}
		}

	public void
	setBorderWidth( int width )
		{
		this.borderWidth = width;
		}

	public void
	setBorderColor( Color color )
		{
		this.borderColor = color;
		}

	public void
	setHMargin( int margin )
		{
		this.hMargin = margin;
		}

	public void
	setVMargin( int margin )
		{
		this.vMargin = margin;
		}

	public void
	update( Graphics updateG )
		{
		this.paint( updateG );
		}

	public synchronized void
	paint( Graphics g )
		{
		if ( ! this.isVisible() )
			return;

		int         i, x, y;
		int			xorig = 0;
		int			yorig = 0;
		Dimension   d = this.getSize();

		Color saveColor = g.getColor();

		// FILL	   UNDONE
		g.setColor( this.getBackground() );
		g.fillRect( xorig, yorig, xorig + d.width, yorig + d.height );

		// FRAME
		if ( this.borderWidth == 1 )
			{
			g.setColor( this.borderColor );
			g.drawLine( xorig,					yorig + (d.height-1),
						xorig + (d.width-1),	yorig + (d.height-1) );

			g.drawLine( xorig + (d.width-1),	yorig,
						xorig + (d.width-1),	yorig + (d.height-1) );

			g.drawLine( xorig,					yorig,
						xorig + (d.width-1),	yorig );

			g.drawLine( xorig,					yorig,
						xorig,					yorig + (d.height-1) );
			}
		else if ( this.borderWidth > 1 )
			{
			g.setColor( this.borderColor );
			// bottom
			g.fillRect( xorig,				yorig + (d.height - this.borderWidth),
						d.width,			this.borderWidth );
			// left
			g.fillRect( xorig,				yorig,
						this.borderWidth,	d.height );
			//top
			g.fillRect( xorig,				yorig,
						d.width,			this.borderWidth );
			//right
			g.fillRect( xorig + (d.width - this.borderWidth),	yorig,
						this.borderWidth,						d.height );
			}

		// TEXT
		// UNDONE - switch ( this.alignment )...
		//
		x = ( xorig + this.borderWidth + hMargin );
		y = ( yorig + d.height )
				- ( this.borderWidth + this.vMargin + this.fontDescent );

		g.setColor( this.getForeground() );

		g.setFont( this.getFont() );
		g.drawString( this.label, x, y );

        // Restore
		g.setColor( saveColor );
		}

	public void
	focusLost( FocusEvent event )
		{
		}

	public void
	focusGained( FocusEvent event )
		{
		this.transferFocus();
		}

	public boolean
	isFocusTraversable()
		{
		return false;
		}


	public void
	computeDimensions()
		{
		Dimension	sz = this.getSize();
		Graphics	g = this.getGraphics();
		FontMetrics fm = g.getFontMetrics( this.getFont() );

		this.fontHeight = fm.getHeight();
		this.fontDescent = fm.getDescent();
		this.fontAscent = fm.getAscent();

		this.labelWidth = fm.stringWidth( this.label ) + 1;

		this.mDim = new
			Dimension(	this.labelWidth + ( this.hMargin * 2 )
							+ ( this.borderWidth * 2),
						this.fontAscent + this.fontDescent
							+ ( this.vMargin * 2 )
							+ ( this.borderWidth * 2) );

		this.pDim = new
			Dimension( this.mDim.width, this.mDim.height );
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

		if ( this.kludgeStyle )
			{
			this.setStyle( this.styleToKludge );
			}

		computeDimensions();
		}

	}
