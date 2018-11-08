/**
 * Copyright (c) 2018 Klaus Tachtler. All Rights Reserved.
 * Klaus Tachtler. <klaus@tachtler.net>
 * http://www.tachtler.net
 */
package net.tachtler.jilter;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*******************************************************************************
 * Command Line Interface Argument Parser for JMilter Bean.
 * 
 * @author Klaus Tachtler. <klaus@tachtler.net>
 * 
 *         Homepage : http://www.tachtler.net
 * 
 *         Licensed under the Apache License, Version 2.0 (the "License"); you
 *         may not use this file except in compliance with the License. You may
 *         obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *         implied. See the License for the specific language governing
 *         permissions and limitations under the License..
 * 
 *         Copyright (c) 2018 by Klaus Tachtler.
 ******************************************************************************/
public class InfoMilterCLIArgsParserBean {

	private static Logger log = LogManager.getLogger();

	/**
	 * Returns the IPv4-Address.
	 */
	private InetAddress inetAddress;

	/**
	 * Returns the Port.
	 */
	private int port;

	/**
	 * Returns if TcpLogging is enabled.
	 */
	private Boolean loggingEnabled;

	/**
	 * Return the log level for the jmilter.
	 */
	private String logLevel;

	/**
	 * Constructor.
	 */
	public InfoMilterCLIArgsParserBean(InetAddress inetAddress, int port, Boolean tcpLoggingEnabled,
			String tcpLogLevel) {
		super();
		this.inetAddress = inetAddress;
		this.port = port;
		this.loggingEnabled = tcpLoggingEnabled;
		this.logLevel = tcpLogLevel;
	}

	/**
	 * Initialize all variables to default or unseeded values.
	 */
	public final void init() {
		try {
			this.inetAddress = InetAddress.getByName("127.0.0.1");
		} catch (UnknownHostException eInetAddress) {
			log.error("UnknownHostException                    : " + eInetAddress);
			eInetAddress.printStackTrace();
			throw new RuntimeException(
					"***** Program stop, because InfoMilter could not be initialized! ***** (For more details, see error messages and caused by below).",
					eInetAddress);

		}

		this.port = 10099;
		this.loggingEnabled = false;
		this.logLevel = "INFO";
	}

	/**
	 * @return the inetAddress
	 */
	public InetAddress getInetAddress() {
		return inetAddress;
	}

	/**
	 * @param inetAddress the inetAddress to set
	 */
	public void setInetAddress(InetAddress inetAddress) {
		this.inetAddress = inetAddress;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the loggingEnabled
	 */
	public Boolean getLoggingEnabled() {
		return loggingEnabled;
	}

	/**
	 * @param loggingEnabled the loggingEnabled to set
	 */
	public void setLoggingEnabled(Boolean tcpLoggingEnabled) {
		this.loggingEnabled = tcpLoggingEnabled;
	}

	/**
	 * @return the logLevel
	 */
	public String getLogLevel() {
		return logLevel;
	}

	/**
	 * @param logLevel the logLevel to set
	 */
	public void setLogLevel(String tcpLogLevel) {
		this.logLevel = tcpLogLevel;
	}

}
