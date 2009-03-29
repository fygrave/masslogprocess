
package com.mlp.syslogd;

import java.io.Serializable;

import com.oroinc.text.regex.*;


public class
SyslogMatch implements Serializable
	{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4082180493261862394L;
	public static final String		RCS_ID = "$Id: SyslogMatch.java,v 1.1.1.1 1998/02/22 05:47:54 time Exp $";
	public static final String		RCS_REV = "$Revision: 1.1.1.1 $";
	public static final String		RCS_NAME = "$Name:  $";

	private boolean			match;
	private String			expression;
	private Pattern			pattern;
	private MatchResult		result;

	public
	SyslogMatch()
		{
		this.match = false;
		this.pattern = null;
		}

	public void
	compile( String expr )
			throws MatchCompileException
		{
		Perl5Compiler compiler = new Perl5Compiler();

		this.expression = expr;

		try {
			this.pattern = compiler.compile( expr );
			}
		catch( MalformedPatternException ex )
			{
			this.pattern = null;
			throw new MatchCompileException( ex.getMessage() );
			}
		}

	public boolean
	matchMessage( String message )
		{
		if ( this.pattern == null )
			{
			this.match = false;
			return false;
			}

		MatchResult		result = null;
		Perl5Matcher	matcher = new Perl5Matcher();

		PatternMatcherInput input =
			new PatternMatcherInput( message );

		this.match = matcher.contains( input, this.pattern );

		if ( this.match )
			{
			this.result = matcher.getMatch();
			}

		return this.match;
		}

	public String
	getExpression()
		{
		return this.expression;
		}

	public boolean
	didMatch()
		{
		return this.match;
		}

	public String
	getMatchSubExpr( int index )
		{
		return this.result.group( index );
		}

	public String[]
	getMatchVariables()
		{
		int size = result.groups();
		String[] vars = new String[ size ];

		for( int i = 0 ; i < size ; i++ )
			{
			vars[ i ] = this.result.group( i );
			}

		return vars;
		}
	}


