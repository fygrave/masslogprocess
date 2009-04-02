/*_############################################################################
  _## 
  _##  SNMP4J - UsmUserTable.java  
  _## 
  _##  Copyright (C) 2003-2008  Frank Fock and Jochen Katz (SNMP4J.org)
  _##  
  _##  Licensed under the Apache License, Version 2.0 (the "License");
  _##  you may not use this file except in compliance with the License.
  _##  You may obtain a copy of the License at
  _##  
  _##      http://www.apache.org/licenses/LICENSE-2.0
  _##  
  _##  Unless required by applicable law or agreed to in writing, software
  _##  distributed under the License is distributed on an "AS IS" BASIS,
  _##  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  _##  See the License for the specific language governing permissions and
  _##  limitations under the License.
  _##  
  _##########################################################################*/

package org.snmp4j.security;

import java.io.Serializable;
import java.util.TreeMap;

import org.snmp4j.smi.OctetString;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.util.Collection;
import org.snmp4j.log.*;

/**
 * The <code>UsmUserTable</code> class stores USM user
 * information as part of the Local Configuration Datastore (LCD).
 *
 * @author Frank Fock
 * @version 1.1
 */
public class UsmUserTable implements Serializable {

  private static final long serialVersionUID = 6936547777550957622L;

  private static final LogAdapter logger = LogFactory.getLogger(UsmUserTable.class);

  private Map table = new TreeMap();

  public UsmUserTable() {
  }

  public synchronized UsmUserEntry addUser(UsmUserEntry user) {
    if (logger.isDebugEnabled()) {
      logger.debug("Adding user "+user.getUserName()+" = "+user.getUsmUser());
    }
    return (UsmUserEntry) table.put(new UsmUserKey(user), user);
  }

  public synchronized void setUsers(Collection c) {
    if (logger.isDebugEnabled()) {
      logger.debug("Setting users to "+c);
    }
    table.clear();
    for (Iterator it = c.iterator(); it.hasNext(); ) {
      UsmUserEntry user = (UsmUserEntry) it.next();
      table.put(new UsmUserKey(user), user);
    }
  }

  /**
   * Gets all user entries with the supplied user name.
   * @param userName
   *    an <code>OctetString</code> denoting the user name.
   * @return
   *    a possibly empty <code>List</code> containing all user entries with
   *    the specified <code>userName</code>.
   */
  public synchronized List getUserEntries(OctetString userName) {
    LinkedList users = new LinkedList();
    for (Iterator it = table.values().iterator(); it.hasNext();) {
      UsmUserEntry value = (UsmUserEntry) it.next();
      if (userName.equals(value.getUserName())) {
        users.add(value);
      }
    }
    if (logger.isDebugEnabled()) {
      logger.debug("Returning user entries for "+userName+" = "+users);
    }
    return users;
  }

  public synchronized List getUserEntries() {
    LinkedList l = new LinkedList();
    for (Iterator it = table.values().iterator(); it.hasNext(); ) {
      l.add(it.next());
    }
    return l;
  }

  public synchronized UsmUserEntry removeUser(OctetString engineID,
                                              OctetString securityName) {
    UsmUserEntry entry =
        (UsmUserEntry) table.remove(new UsmUserKey(engineID, securityName));
    if (logger.isDebugEnabled()) {
      logger.debug("Removed user with secName="+securityName+
                   " and engineID="+engineID);
    }
    return entry;
  }

  public synchronized UsmUserEntry getUser(OctetString engineID,
                                           OctetString securityName) {
    return (UsmUserEntry)table.get(new UsmUserKey(engineID, securityName));
  }

  public synchronized UsmUserEntry getUser(OctetString securityName) {
    return (UsmUserEntry)table.get(new UsmUserKey(new OctetString(), securityName));
  }

  public synchronized void clear() {
    table.clear();
    if (logger.isDebugEnabled()) {
      logger.debug("Cleared UsmUserTable");
    }
  }

  public static class UsmUserKey implements Comparable {
    OctetString engineID;
    OctetString securityName;

    public UsmUserKey(UsmUserEntry entry) {
      setEngineID(entry.getEngineID());
      this.securityName = entry.getUsmUser().getSecurityName();
    }

    public UsmUserKey(OctetString engineID, OctetString securityName) {
      setEngineID(engineID);
      this.securityName = securityName;
    }

    private void setEngineID(OctetString engineID) {
      if (engineID == null) {
        this.engineID = new OctetString();
      }
      else {
        this.engineID = engineID;
      }
    }

    public int hashCode() {
      return engineID.hashCode()^2 + securityName.hashCode();
    }

    public boolean equals(Object o) {
      if ((o instanceof UsmUserEntry) || (o instanceof UsmUserKey)) {
        return (compareTo(o) == 0);
      }
      return false;
    }

    public int compareTo(Object o) {
      if (o instanceof UsmUserEntry) {
        return compareTo(new UsmUserKey((UsmUserEntry)o));
      }
      UsmUserKey other = (UsmUserKey)o;
      int result = 0;
      if ((engineID != null) && (other.engineID != null)) {
        result = engineID.compareTo(other.engineID);
      }
      else if ((engineID != null) && (other.engineID == null)) {
        result = 1;
      }
      else if ((engineID == null) && (other.engineID != null)) {
        result = -1;
      }
      if (result == 0) {
        result = securityName.compareTo(other.securityName);
      }
      return result;
    }
  }
}

