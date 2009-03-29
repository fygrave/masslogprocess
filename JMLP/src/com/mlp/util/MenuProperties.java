
package com.mlp.util;

import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

//import com.sun.java.swing.JMenu;
//import com.sun.java.swing.JPopupMenu;
//import com.sun.java.swing.JComponent;
//import com.sun.java.swing.JMenuItem;


public class
MenuProperties extends Object
	{
	static public JPopupMenu
    loadPopupMenu( String menuPropertyName, ActionListener listener )
		{
		JPopupMenu popup = new JPopupMenu();

		MenuProperties.addMenuItems( popup, menuPropertyName, listener );

		return popup;
		}

	static public void
	addGenericItem( JComponent menu, JComponent item )
		{
		if ( menu instanceof JMenu )
			{
			JMenu jm = (JMenu) menu;

			if ( item == null )
				jm.addSeparator();
			else
				jm.add( item );
			}
		else
			{
			JPopupMenu jp = (JPopupMenu) menu;

			if ( item == null )
				jp.addSeparator();
			else
				jp.add( item );
			}
		}

	static public void
	addMenuItems(
			JComponent menu, String menuPropertyName,
			ActionListener listener )
		{
		String[]	itemList;
		String		itemString;
		String		menuString;
		String		itemNameStr; 
		JMenuItem	mItem;

		menuString = 
			UserProperties.getProperty
				( "menu." + menuPropertyName, null );

		if ( menuString == null )
			{
			ICETracer.traceWithStack
				( "Menu definition property '"
					+ menuPropertyName + "' is not defined." );
			return;
			}

		itemList = StringUtilities.splitString( menuString, ":" );

		if ( itemList != null )
			{
			for ( int iIdx = 0 ; iIdx < itemList.length ; ++iIdx )
				{
				itemNameStr =
					"item." + menuPropertyName + "." + itemList[iIdx];

				itemString =
					UserProperties.getProperty( itemNameStr, null );

				if ( itemString == null )
					{
					ICETracer.traceWithStack
						( "Menu definition '" + menuPropertyName
							+ "' is missing item definition property '"
							+ itemNameStr + "'." );
					}
				else
					{
					int colonIdx = itemString.indexOf( ':' );

					if ( itemString.equals( "-" ) )
						{
						MenuProperties.addGenericItem( menu, null );
						}
					else if ( colonIdx < 0 )
						{
						ICETracer.traceWithStack
							( "Menu '" + menuPropertyName
								+ "' Item '" + itemNameStr
								+ "' has invalid definition." );
						}
					 else
						{
						String title =
							itemString.substring( 0, colonIdx );

						String command =
							itemString.substring( colonIdx + 1 );

						if ( command.equals( "@" ) )
							{
							JMenu subMenu = new JMenu( title );

							String subMenuName =
								menuPropertyName + "." + itemList[iIdx];

							MenuProperties.addMenuItems
								( subMenu, subMenuName, listener );

							MenuProperties.addGenericItem( menu, subMenu );
							}
						else if ( title.equals( "-" ) )
							{
							MenuProperties.addGenericItem( menu, null );
							}
						else
							{
							mItem = new JMenuItem( title );

							if ( listener != null )
								{
								mItem.addActionListener( listener );
								mItem.setActionCommand( command );
								}

							MenuProperties.addGenericItem( menu, mItem );
							}
						}
					} // itemString != null
				} // foreach item
			} // itemList != null
		}

	}


