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


public class
BorderPanel extends Panel
	{
	static public final int			NONE = 0;
	static public final int			FLAT = 1;
	static public final int			RIDGE = 2;
	static public final int			GROOVE = 3;
	static public final int			RAISED = 4;
	static public final int			SUNKEN = 5;
			
	protected int		insetWidth;
	protected int		marginWidth;
	protected int		borderWidth;
	protected int		borderStyle;

	protected double	darkFactor;
	protected double	liteFactor;


	public
	BorderPanel( int inset, int border, int margin, int style )
		{
		super();

		// Sanity
		switch ( this.borderStyle )
			{
			case BorderPanel.RIDGE:
			case BorderPanel.GROOVE:
			case BorderPanel.RAISED:
			case BorderPanel.SUNKEN:
				if ( (border & 1) != 0 ) border++;
				if ( border < 2 ) border = 2;
				break;

			case BorderPanel.FLAT:
				break;
			case BorderPanel.NONE:
				break;
			}

		if ( border >= inset )
			{
			margin = 0;
			border = inset;
			}
		else if ( border + margin >= inset )
			{
			margin = inset - border;
			}

		this.insetWidth = inset;
		this.marginWidth = margin;
		this.borderWidth = border;
		this.borderStyle = style;

		this.darkFactor = 0.70; // larger multiplier makes lighter
		this.liteFactor = 0.82; // larger multiplier makes lighter
		}
	
	public Insets
	getInsets()
		{
		Insets peer = super.getInsets();

		Insets result =
			new Insets(
				peer.top + this.insetWidth,
				peer.left + this.insetWidth,
				peer.bottom + this.insetWidth,
				peer.right + this.insetWidth );

		return result;
		}

	public double
	getDarkFactor()
		{
		return this.darkFactor;
		}

	public void
	setDarkFactor( double factor )
		{
		this.darkFactor = factor; // larger multiplier makes lighter
		}

	public double
	getLiteFactor()
		{
		return this.liteFactor;
		}

	public void
	setLiteFactor( double factor )
		{
		this.liteFactor = factor; // larger multiplier makes lighter
		}

	public void
	update( Graphics updateG )
		{
		this.paint( updateG );
		}

	public void
	paint( Graphics g )
		{
		super.paint( g );

		Dimension sz = this.getSize();

		Insets insets = super.getInsets();

		// The origin and dimensions of our frame's area
		// (which is inside the panel's insets.
		int xorigin = insets.left;
		int yorigin = insets.top;
		int width = sz.width - (insets.left + insets.right);
		int height = sz.height - (insets.top + insets.bottom);

		g.setClip( xorigin, yorigin, width, height );

		switch ( this.borderStyle )
			{
			case BorderPanel.RIDGE:
				this.drawRidgeBorder
					( g, xorigin, yorigin, width, height );
				break;
			case BorderPanel.GROOVE:
				this.drawGrooveBorder
					( g, xorigin, yorigin, width, height );
				break;
			case BorderPanel.RAISED:
				this.drawRaisedBorder
					( g, xorigin, yorigin, width, height );
				break;
			case BorderPanel.SUNKEN:
				this.drawSunkenBorder
					( g, xorigin, yorigin, width, height );
				break;
			case BorderPanel.FLAT:
				this.drawFlatBorder
					( g, xorigin, yorigin, width, height );
				break;
			}
		}

	protected void
	drawRidgeBorder( Graphics g, int xorigin, int yorigin, int width, int height )
		{
		Color bgColor = this.getBackground();

		Color liteColor = this.lighten( bgColor );
		Color darkColor = this.darken( bgColor );

		int inside =
			this.insetWidth -
				( this.borderWidth + this.marginWidth );

		int	bX = xorigin + inside;
		int	bY = yorigin + inside;
		int	bW = width -  (2 * inside);
		int	bH = height -  (2 * inside);
		
		if ( this.borderWidth == 2 )
			{
			g.setColor( liteColor );
			g.drawLine( bX, bY, (bX + (bW - 1)), bY );	// top
			g.drawLine( bX, bY, bX, (bY + (bH - 1)) );	// left

			g.drawLine( (bX + (bW - 2)), (bY + 1),
						(bX + (bW - 2)), (bY + (bH - 2)) );	// right
			g.drawLine( (bX + 1), (bY + (bH - 2)),
						(bX + (bW - 2)), (bY + (bH - 2)) );	// bottom

			g.setColor( darkColor );
			g.drawLine( (bX + 1), (bY + 1),
						(bX + (bW - 2)), (bY + 1) );	// top
			g.drawLine( (bX + 1), (bY + 1),
						(bX + 1), (bY + (bH - 2)) );	// left

			g.drawLine( (bX + (bW - 1)), (bY + 1),
						(bX + (bW - 1)), (bY + (bH - 2)) );	// right
			g.drawLine( (bX + 1), (bY + (bH - 1)),
						(bX + (bW - 2)), (bY + (bH - 1)) );	// bottom
			}
		else
			{
			}
		}

	protected void
	drawGrooveBorder( Graphics g, int xorigin, int yorigin, int width, int height )
		{
		}

	protected void
	drawRaisedBorder( Graphics g, int xorigin, int yorigin, int width, int height )
		{
		}

	protected void
	drawSunkenBorder( Graphics g, int xorigin, int yorigin, int width, int height )
		{
		}

	protected void
	drawFlatBorder( Graphics g, int xorigin, int yorigin, int width, int height )
		{
		Color saveColor = g.getColor();
		g.setColor( Color.black );

		if ( this.borderWidth == 1 )
			{
			g.drawRect( 0, 0, width - 1, height - 1 );
			}
		else
			{
			g.fillRect( 0, 0, width - 1, this.borderWidth - 1 ); // top
			g.fillRect( width - (this.borderWidth + 1), 0,
						this.borderWidth - 1, height - 1 ); // right
			g.fillRect( 0, (height - this.borderWidth),
						 width - 1, this.borderWidth - 1 ); // bottom
			g.fillRect( 0, 0, this.borderWidth - 1, height - 1 ); // left
			}
		}

	protected Color
	darken( Color color )
		{
		int		newval;
		double minFactor, factor;

		minFactor = 1.0;

		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();

		factor = ((double)red / 255.0);
		if ( factor < minFactor ) minFactor = factor;
		factor = ((double)green / 255.0);
		if ( factor < minFactor ) minFactor = factor;
		factor = ((double)blue / 255.0);
		if ( factor < minFactor ) minFactor = factor;

		factor = minFactor * this.darkFactor;

		newval = (int)((double)red * factor);
		red = Math.max( newval, 0 );
		newval = (int)((double)green * factor);
		green = Math.max( newval, 0 );
		newval = (int)((double)blue * factor);
		blue = Math.max( newval, 0 );

		return new Color( red, green, blue );
		}

	protected Color
	lighten( Color color )
		{
		int		newval;
		double	maxFactor, factor;

		maxFactor = 0.0;

		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();

		factor = ((double)red / 255.0);
		if ( factor > maxFactor ) maxFactor = factor;
		factor = ((double)green / 255.0);
		if ( factor > maxFactor ) maxFactor = factor;
		factor = ((double)blue / 255.0);
		if ( factor > maxFactor ) maxFactor = factor;

		if ( maxFactor > this.liteFactor )
			maxFactor = this.liteFactor;

		factor = (1.0 / maxFactor);

		newval = (int)((double)red * factor);
		red = Math.min( newval, 255 );
		newval = (int)((double)green * factor);
		green = Math.min( newval, 255 );
		newval = (int)((double)blue * factor);
		blue = Math.min( newval, 255 );

		return new Color( red, green, blue );
		}

	}

