/*******************************************************************************
 * openthinclient.org ThinClient suite
 * 
 * Copyright (C) 2004, 2007 levigo holding GmbH. All Rights Reserved.
 * 
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 *******************************************************************************/
package org.openthinclient.syslogd;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.jboss.system.ServiceMBeanSupport;

/**
 * @author levigo
 */
public class SyslogService extends ServiceMBeanSupport
    implements
      SyslogServiceMBean {
  private static final Logger logger = Logger.getLogger(SyslogService.class);

  private SyslogDaemon daemon;
  private int syslogPort = 0;

  private Thread daemonThread;

  public void startService() throws Exception {
    try {
      daemon = new Log4JSyslogDaemon(0 != syslogPort
          ? syslogPort
          : SyslogDaemon.SYSLOG_PORT);

      daemonThread = new Thread(daemon, "Syslog daemon");
      daemonThread.setDaemon(true);
      daemonThread.start();
      logger.info("Syslog service launched");
    } catch (IOException e) {
      logger.error("Exception launching Syslog service", e);
      throw e;
    }
  }

  public void stopService() throws Exception {
    if (null != daemon) {
      daemon.shutdown();
      daemonThread.join(5000);
      if (daemonThread.isAlive())
        logger.error("Syslog daemon did not shut down in time.");
      else
        logger.info("Syslog service shut down.");
    }
  }

  public int getPort() {
    return syslogPort;
  }

  public void setPort(int syslogPort) {
    this.syslogPort = syslogPort;
  }
}
