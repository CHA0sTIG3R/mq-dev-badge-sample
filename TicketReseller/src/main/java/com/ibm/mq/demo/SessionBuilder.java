/*
* (c) Copyright IBM Corporation 2018
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.ibm.mq.demo;

import java.util.logging.*;
import java.lang.NumberFormatException;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;

/**
  A <code>SessionBuilder</code> is used to establish and close a
  connection to a MQ Server.
 */
public class SessionBuilder
{
  private static final Logger logger = Logger.getLogger("com.ibm.mq.demo");

  // Settings for the connection to MQ
  // Should be obtained from either a lookup or a .json file
  private static final String HOST = "localhost"; // Host name or IP address
  private static final int PORT = 1414; // Listener port for your queue manager
  private static String CHANNEL = "DEV.APP.SVRCONN";
  private static final String QMGR = "QM1"; // Queue manager name
  private static String USER = "app";
  private static String PASSWORD = "passw0rd";
  private static String SUBSCRIPTION_NAME = "SampleSubscriber";

  /**
   * Uses the JMS Client classes to establish a connection to the Queue Manager
   * running on the MQ Server
   *
   * Challenge : Connect to a queue manager
   *
   * @return Session if the connection is established, null if the Connection
   * is unsuccessful
   */

  public static Session connect() {
    Session session = null;
    logger.fine("Initialising JMS session connection");

    try {
      // Create a connection factory
      logger.finest("Creating Connection Factory");
      String host = getSystemEnvString("MQ_BADGE_QM_HOSTNAME", HOST);;
      int port = PORT;
      String qmgr = getSystemEnvString("MQ_BADGE_QM_NAME", QMGR);;
      String user = getSystemEnvString("MQ_BADGE_USER", USER);;
      String password = getSystemEnvString("MQ_BADGE_PASSWORD", PASSWORD);
      String channel = getSystemEnvString("MQ_BADGE_CHANNEL", CHANNEL);

      if (System.getenv("MQ_BADGE_QM_PORT") != null) {
        try {
			    port = Integer.parseInt(System.getenv("MQ_BADGE_QM_PORT"));
        }
        catch (NumberFormatException e) {
          logger.warning(String.format("Invalid port specified defaulting to %d", PORT));
        }
		  }

      System.out.println("Challenge : Subscribes to topic");
      System.out.println("Your code to create a subscription will go here");
      // The following code needs to be added here
      logger.finest("Challenge Add code to : Get an instance of JMS Factory factory");
      JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
      logger.finest("Challenge Add code to : Get an instance of JMS Connection factory");
      JmsConnectionFactory cf = ff.createConnectionFactory();

      // Set the properties
      logger.finest("Challenge Add code to : Set WMQ_ properties for Connection");
      cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, host);
      cf.setIntProperty(WMQConstants.WMQ_PORT, port);
      cf.setStringProperty(WMQConstants.WMQ_CHANNEL, channel);
      cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
      cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, qmgr);
      cf.setStringProperty(WMQConstants.CLIENT_ID, SUBSCRIPTION_NAME);
      cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
      cf.setStringProperty(WMQConstants.USERID, user);
      cf.setStringProperty(WMQConstants.PASSWORD, password);

      // Create JMS objects
      logger.finest("Creating Connection Session");
      logger.finest("Challenge Add code to : Create the Connection");
      Connection connection = cf.createConnection();
      logger.finest("Challenge Add code to : Create the Session");
      session = connection.createSession();
      logger.finest("Challenge Add code to : Start the Connection");
      connection.start();
      logger.fine("JMS session connection initialised successfully");
    }
    catch (JMSException jmsex) {
      logger.severe("Unable to create JMS Connection");
      jmsex.printStackTrace();
    }

    return session;
  }


  /**
   * Closes the session
   *
   * @param session to be closed
   * @return None
   */

  public static void close(Session session) {
    try {
      session.close();
      logger.finest("JMS Session closed successfully");
    }
    catch (JMSException jmsex)
    {
      logger.severe("Unable to close JMS Session");
      jmsex.printStackTrace();
    }
  }

  /**
   * Determines System Envrionment String
   *
   * @param envparam The envrionment string to find
   * @param defaultvalue The default value to use
   * @return environment string if found else default
   */

  private static String getSystemEnvString(String envparam, String defaultvalue) {
    return (System.getenv(envparam) != null) ?
          System.getenv(envparam) : defaultvalue;
  }

}
