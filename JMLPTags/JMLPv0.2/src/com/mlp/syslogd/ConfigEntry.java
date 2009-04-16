package com.mlp.syslogd;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import com.mlp.syslog.SyslogDefs;
import com.mlp.util.Global;

public class ConfigEntry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3741263296099870148L;
	public static final String RCS_ID = "$Id: ConfigEntry.java,v 1.1.1.1 1998/02/22 05:47:54 time Exp $";
	public static final String RCS_REV = "$Revision: 1.1.1.1 $";
	public static final String RCS_NAME = "$Name:  $";

	private static final int[] repeatBackoff = { 15, 30, 60, 180, 600, 3600 };

	private boolean hasMatch;
	private boolean notMatch;
	private SyslogMatch matcher;
	private SyslogAction action;

	private int[] facilities;

	private String[] parameters;

	private int backOffIdx;
	private int repeatCount;
	private Date repeatTimeout;

	private Date prevTime;
	private SyslogMessage prevMsg;

	public ConfigEntry() {
		this.matcher = null;
		this.hasMatch = false;
		this.notMatch = false;

		this.facilities = new int[SyslogDefs.LOG_NFACILITIES];

		this.action = null;
		this.parameters = null;

		this.backOffIdx = 0;
		this.repeatCount = 0;
		this.prevTime = null;
		this.prevMsg = null;
	}

	public void setAction(SyslogAction action) {
		this.action = action;
	}

	public boolean afterRepeatTimeout(Date now) {
		return now.after(this.repeatTimeout);
	}

	public Date getRepeatTimeout(Date now) {
		return this.repeatTimeout;
	}

	public  void setFacilityLevel(int facility, int level) {
		this.facilities[facility] = level;
	}

	public void setMatchExpr(String expr) {
		this.hasMatch = true;
		this.notMatch = false;
		if (expr.startsWith("!")) {
			this.notMatch = true;
			expr = expr.substring(1);
		}

		this.matcher = new SyslogMatch();

		try {
			this.matcher.compile(expr);
		} catch (MatchCompileException ex) {
			this.matcher = null;
			this.hasMatch = false;
			this.notMatch = false;
		}
	}

	/**
	 * 
	 * @param message
	 * @return
	 */
	private boolean matchMessage(String message) {
		boolean result = false;

		if (this.hasMatch && this.matcher != null) {
			result = this.matcher.matchMessage(message);
			if (this.notMatch) {
				result = !result;
			}
		}

		return result;
	}

	public void openAction() {
		if (this.action != null) {
			this.action.openAction();
		}
	}

	public void closeAction() {
		if (this.action != null) {
			this.action.closeAction();
		}
	}

	public void registerActionDisplay(String name,
			SyslogDisplayInterface display) {
		if (this.action != null) {
			this.action.registerActionDisplay(name, display);
		}
	}

	private  SyslogMessage createRepeatMsg(SyslogMessage logMsg, Date now) {
		SyslogMessage repeatMsg = (SyslogMessage) logMsg.clone();

		long repSecs = (now.getTime() - this.prevTime.getTime()) / 1000;

		repeatMsg.isRepeat = true;
		repeatMsg.matchVars = null;
		repeatMsg.message = "msg repeated " + this.repeatCount + " times in "
				+ repSecs + " seconds.";

		return repeatMsg;
	}

	public  void incrementBackOff() {
		if (ConfigEntry.repeatBackoff.length > (this.backOffIdx + 1)) {
			this.backOffIdx++;
		}
	}

	public synchronized void computeRepeatTimer(Date now) {
		Calendar cal = Calendar.getInstance();

		cal.setTime(now);
		cal.add(Calendar.SECOND, repeatBackoff[this.backOffIdx]);

		this.repeatTimeout = cal.getTime();
	}

	public  void checkRepeatTimeout(Date now) {
		if (this.prevMsg != null && this.repeatCount > 0) {
			if (now.after(this.repeatTimeout)) {
				SyslogMessage repeatMsg;

				if (this.repeatCount == 1)
					repeatMsg = this.prevMsg;
				else
					repeatMsg = this.createRepeatMsg(this.prevMsg, now);

				this.invokeMessageAction(repeatMsg);

				this.prevMsg = null;
				this.repeatCount = 0;
				this.backOffIdx = 0;
			}
		}
	}

	/*
	 * 
	 * @return True, if the message should be processed. otherwise, False,
	 * indicating a repeat.
	 */

	public synchronized boolean checkForRepeats(SyslogMessage logMsg) {
		boolean result = true;
		boolean newMsg = true;
		Date now = new Date();
		SyslogMessage repeatMsg = null;

		if (this.prevMsg != null
				&& this.prevMsg.message.length() == logMsg.message.length()
				&& this.prevMsg.hostName.equals(logMsg.hostName)
				&& this.prevMsg.message.equals(logMsg.message)) {
			result = false;
			newMsg = false;

			if (this.repeatCount > 0) {
				this.repeatCount++;
				if (now.after(this.repeatTimeout)) {
					result = true;

					repeatMsg = this.createRepeatMsg(logMsg, now);

					this.invokeMessageAction(repeatMsg);

					this.computeRepeatTimer(now);

					this.incrementBackOff();

					this.prevTime = now;
					this.repeatCount = 0;
				}
			} else {
				this.repeatCount++;
				this.computeRepeatTimer(now);
			}
		}

		if (newMsg) {
			if (this.repeatCount > 0) {
				repeatMsg = this.createRepeatMsg(logMsg, now);
				this.invokeMessageAction(repeatMsg);
			}

			this.backOffIdx = 0;
			this.prevTime = now;
			this.repeatCount = 0;
		}

		this.prevMsg = logMsg;

		return result;
	}

	public void invokeMessageAction(SyslogMessage logMsg) {
		if(Global.isDebug){
			System.out.println("Enter "+this.getClass().getName()+".invokeMessageAction()");
		}
		if (this.action.isThreaded()) {
			ActionThread thread = new ActionThread(this.action, logMsg);
			if(Global.isDebug)
			System.out.println("action.isThreaded");
			thread.start();
		} else {
			if(Global.isDebug)
			System.out.println("action.is not Threaded");
			this.action.processMessage(logMsg);
			
		}
		if(Global.isDebug){
			System.out.println("Out "+this.getClass().getName()+".invokeMessageAction()");
		}
	}

	public void processMessage(SyslogMessage logMsg) {
		if(Global.isDebug){
			System.out.println("Enter "+this.getClass().getName()+".processMessage()");
		}
		boolean processIt = true;

		try{
			
		if (facilities[logMsg.facility] >= logMsg.priority
				&& this.action != null && this.action.isOpen()) {
			if (this.hasMatch) {
				processIt = this.matchMessage(logMsg.message);
				if(Global.isDebug){
					System.out.println(this.getClass().getName()+".processMessage() hasMatch");
				}
			}

			if (processIt) {
				if (this.checkForRepeats(logMsg)) {
					if(Global.isDebug){
						System.out.println(this.getClass().getName()+".processMessage() checkForRepeats");
					}
					logMsg.matchVars = null;
					if (this.hasMatch && !this.notMatch) {
						logMsg.matchVars = this.matcher.getMatchVariables();
					}

					this.invokeMessageAction(logMsg);
				}
			}
		}
		}catch(Exception e){
			System.err.println("logMsg.facility="+logMsg.facility);
			e.printStackTrace();
		}
		if(Global.isDebug){
			System.out.println("Out "+this.getClass().getName()+".processMessage()");
		}
	}

}
