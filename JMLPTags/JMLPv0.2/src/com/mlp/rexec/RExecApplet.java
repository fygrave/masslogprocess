
package com.mlp.rexec;

import java.awt.*;
import java.util.*;
import java.applet.*;

class RExecApplet extends Applet
	{
	static public void main( String argv[] )
		{
		Applet app = new RExecApplet();
		Frame frm = new RExecAppletFrame( "RExec", app, 300, 100 );
		frm.pack();
		frm.show();
		}
	}

