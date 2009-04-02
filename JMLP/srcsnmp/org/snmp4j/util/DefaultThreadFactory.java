/*_############################################################################
  _## 
  _##  SNMP4J - DefaultThreadFactory.java  
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

package org.snmp4j.util;

import org.snmp4j.util.*;

public class DefaultThreadFactory implements ThreadFactory {

  public DefaultThreadFactory() {
  }

  /**
   * Creates a new thread of execution for the supplied task.
   *
   * @param name the name of the execution thread.
   * @param task the task to be executed in the new thread.
   * @return the <code>WorkerTask</code> wrapper to control start and
   *   termination of the thread.
   */
  public WorkerTask createWorkerThread(String name, WorkerTask task,
                                       boolean daemon) {
    WorkerThread wt = new WorkerThread(name, task);
    wt.setDaemon(daemon);
    return wt;
  }

  public class WorkerThread extends Thread implements WorkerTask {

    private WorkerTask task;
    private boolean started = false;

    public WorkerThread(String name, WorkerTask task) {
      super(task, name);
      this.task = task;
    }

    public void terminate() {
      task.terminate();
    }

    public void run() {
      if (!started) {
        started = true;
        super.start();
      }
      else {
        super.run();
      }
    }
  }
}
