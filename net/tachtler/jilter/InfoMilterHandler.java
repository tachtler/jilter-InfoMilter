/**
 * Copyright (c) 2018 Klaus Tachtler. All Rights Reserved.
 * Klaus Tachtler. <klaus@tachtler.net>
 * http://www.tachtler.net
 */
package net.tachtler.jilter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sendmail.jilter.JilterEOMActions;
import com.sendmail.jilter.JilterHandler;
import com.sendmail.jilter.JilterHandlerAdapter;
import com.sendmail.jilter.JilterStatus;

/*******************************************************************************
 * Simple Jilter server handler for handling connections from an MTA.
 *
 * Run Java Console Application with Start Arguments (SimpleJilterServer.java):
 * -p "inet:port@ip-address" -c net.tachtler.jilter.InfoJilterHandler -v
 *
 * Sendmail-Jilter is an Open Source implementation of the Sendmail milter
 * protocol, for implementing milters in Java that can interface with the
 * Sendmail or Postfix MTA.
 *
 * Java Milter Project based on http://sendmail-jilter.sourceforge.net/
 *
 * @author Klaus Tachtler. <klaus@tachtler.net>
 *
 *         Homepage : http://www.tachtler.net
 *
 *         This program is free software; you can redistribute it and/or modify
 *         it under the terms of the GNU General Public License as published by
 *         the Free Software Foundation; either version 2 of the License, or (at
 *         your option) any later version.
 *
 *         Copyright (c) 2018 by Klaus Tachtler.
 ******************************************************************************/
public class InfoMilterHandler extends JilterHandlerAdapter {

	private static Logger log = LogManager.getLogger();

	public final static char CR = (char) 0x0D;
	public final static char LF = (char) 0x0A;

	/**
	 * Constructor.
	 */
	public InfoMilterHandler() {
		super();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sendmail.jilter.JilterHandler#getSupportedProcesses()
	 */
	@Override
	public int getSupportedProcesses() {
		return JilterHandler.PROCESS_CONNECT | JilterHandler.PROCESS_HELO | JilterHandler.PROCESS_ENVFROM
				| JilterHandler.PROCESS_ENVRCPT | JilterHandler.PROCESS_HEADER | JilterHandler.PROCESS_BODY;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sendmail.jilter.JilterHandlerAdapter#getRequiredModifications()
	 */
	@Override
	public int getRequiredModifications() {
		// By default, do not modify anything
		return SMFIF_NONE;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sendmail.jilter.JilterHandlerAdapter#connect(java.lang.String,
	 * java.net.InetAddress, java.util.Properties)
	 */
	public JilterStatus connect(String hostname, InetAddress hostaddr, Properties properties) {

		log.info("----------------------------------------: ");
		log.info(
				"sendmail-jilter - ENTRY: connect        : String hostname, InetAddress hostaddr, Properties properties");
		log.info("----------------------------------------: ");

		log.info("*hostname                               : " + hostname);
		log.info("*hostaddr.getCanonicalHostName()        : " + hostaddr.getCanonicalHostName());
		log.info("*hostaddr.getHostAddress()              : " + hostaddr.getHostAddress());
		log.info("*hostaddr.getHostName()                 : " + hostaddr.getHostName());

		byte[] addr = hostaddr.getAddress();
		short[] octet = new short[4];

		for (int i = 0; i <= addr.length - 1; i++) {
			if (addr[i] <= 127 && addr[i] >= -127 && addr[i] < 0) {
				octet[i] = (short) (addr[i] + 256);
			} else if (addr[i] <= 127 && addr[i] >= -127 && addr[i] > 0) {
				octet[i] = addr[i];
			} else {
				octet[i] = 0;
			}
		}

		log.info("*hostaddr.getAddress()                  : " + "Octet: " + Arrays.toString(octet) + " / Byte: "
				+ Arrays.toString(addr));
		log.info("*hostaddr.isAnyLocalAddress()           : " + hostaddr.isAnyLocalAddress());
		log.info("*hostaddr.isLinkLocalAddress()          : " + hostaddr.isLinkLocalAddress());
		log.info("*hostaddr.isLoopbackAddress()           : " + hostaddr.isLoopbackAddress());
		log.info("*hostaddr.isMCGlobal()                  : " + hostaddr.isMCGlobal());
		log.info("*hostaddr.isMCLinkLocal()               : " + hostaddr.isMCLinkLocal());
		log.info("*hostaddr.isMCNodeLocal()               : " + hostaddr.isMCNodeLocal());
		log.info("*hostaddr.isMCOrgLocal()                : " + hostaddr.isMCOrgLocal());
		log.info("*hostaddr.isMCSiteLocal()               : " + hostaddr.isMCSiteLocal());
		log.info("*hostaddr.isMulticastAddress()          : " + hostaddr.isMulticastAddress());

		int timeout = 3;

		try {
			log.info("*hostaddr.isReachable(timeout)          : " + hostaddr.isReachable(timeout));
		} catch (IOException eIsReachableTimeout) {
			log.error("IOException                             : " + eIsReachableTimeout);
			eIsReachableTimeout.printStackTrace();
		}

		int ttl = 64;

		NetworkInterface netif = null;
		try {
			netif = NetworkInterface.getByInetAddress(hostaddr);
		} catch (SocketException eNetif) {
			log.error("SocketException                         : " + eNetif);
			eNetif.printStackTrace();
		}

		try {
			log.info("*hostaddr.isReachable(netif, ttl, tim...: " + hostaddr.isReachable(netif, ttl, timeout));
		} catch (IOException eIsReachableNetifTtlTimeout) {
			log.error("IOException                             : " + eIsReachableNetifTtlTimeout);
			eIsReachableNetifTtlTimeout.printStackTrace();
		}
		log.info("*hostaddr.isSiteLocalAddress()          : " + hostaddr.isSiteLocalAddress());
		log.info("*poperties.toString()                   : " + properties.toString());
		log.info("*properties.getProperty(\"v\")            : " + properties.getProperty("v"));
		log.info("*properties.getProperty(\"j\")            : " + properties.getProperty("j"));
		log.info("*properties.getProperty(\"{daemon_name}\"): " + properties.getProperty("{daemon_name}"));

		log.info("----------------------------------------: ");
		log.info(
				"sendmail-jilter - LEAVE: connect        : String hostname, InetAddress hostaddr, Properties properties");
		log.info("----------------------------------------: ");

		return JilterStatus.SMFIS_CONTINUE;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sendmail.jilter.JilterHandlerAdapter#helo(java.lang.String,
	 * java.util.Properties)
	 */
	public JilterStatus helo(String helohost, Properties properties) {

		log.info("----------------------------------------: ");
		log.info("sendmail-jilter - ENTRY: helo           : String helohost, Properties properties");
		log.info("----------------------------------------: ");

		log.info("*helohost                               : " + helohost);
		log.info("*poperties.toString()                   : " + properties.toString());

		log.info("----------------------------------------: ");
		log.info("sendmail-jilter - LEAVE: helo           : String helohost, Properties properties");
		log.info("----------------------------------------: ");

		return JilterStatus.SMFIS_CONTINUE;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sendmail.jilter.JilterHandlerAdapter#envfrom(java.lang.String[],
	 * java.util.Properties)
	 */
	public JilterStatus envfrom(String[] argv, Properties properties) {

		log.info("----------------------------------------: ");
		log.info("sendmail-jilter - ENTRY: envfrom        : String[] argv, Properties properties");
		log.info("----------------------------------------: ");

		for (int i = 0; i <= argv.length - 1; i++) {
			log.info("*argv[i]                                : " + "[" + i + "] " + argv[i]);
		}

		log.info("*poperties.toString()                   : " + properties.toString());
		log.info("*poperties.getProperty(\"{mail_host}\")   : " + properties.getProperty("{mail_host}"));
		log.info("*poperties.getProperty(\"{mail_addr}\")   : " + properties.getProperty("{mail_addr}"));
		log.info("*poperties.getProperty(\"{mail_mailer}...: " + properties.getProperty("{mail_mailer}"));

		log.info("----------------------------------------: ");
		log.info("sendmail-jilter - LEAVE: envfrom        : String[] argv, Properties properties");
		log.info("----------------------------------------: ");

		return JilterStatus.SMFIS_CONTINUE;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sendmail.jilter.JilterHandlerAdapter#envrcpt(java.lang.String[],
	 * java.util.Properties)
	 */
	public JilterStatus envrcpt(String[] argv, Properties properties) {

		log.info("----------------------------------------: ");
		log.info("sendmail-jilter - ENTRY: envrcpt        : String[] argv, Properties properties");
		log.info("----------------------------------------: ");

		for (int i = 0; i <= argv.length - 1; i++) {
			log.info("*argv[i]                                : " + "[" + i + "] " + argv[i]);
		}

		log.info("*poperties.toString()                   : " + properties.toString());
		log.info("*poperties.getProperty(\"{rcpt_host}\")   : " + properties.getProperty("{rcpt_host}"));
		log.info("*poperties.getProperty(\"{rcpt_mailer}...: " + properties.getProperty("{rcpt_mailer}"));
		log.info("*poperties.getProperty(\"{rcpt_addr}\")   : " + properties.getProperty("{rcpt_addr}"));
		log.info("*poperties.getProperty(\"i\")             : " + properties.getProperty("i"));

		log.info("----------------------------------------: ");
		log.info("sendmail-jilter - LEAVE: envrcpt        : String[] argv, Properties properties");
		log.info("----------------------------------------: ");

		return JilterStatus.SMFIS_CONTINUE;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sendmail.jilter.JilterHandlerAdapter#header(java.lang.String,
	 * java.lang.String)
	 */
	public JilterStatus header(String headerf, String headerv) {

		log.info("----------------------------------------: ");
		log.info("sendmail-jilter - ENTRY: header         : String headerf, String headerv");
		log.info("----------------------------------------: ");

		log.info("*headerf: headerv                       : " + headerf + ": " + headerv);

		log.info("----------------------------------------: ");
		log.info("sendmail-jilter - LEAVE: header         : String headerf, String headerv");
		log.info("----------------------------------------: ");

		return JilterStatus.SMFIS_CONTINUE;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sendmail.jilter.JilterHandlerAdapter#eoh()
	 */
	public JilterStatus eoh() {

		log.info("----------------------------------------: ");
		log.info("sendmail-jilter - ENTRY: eoh            : ");
		log.info("----------------------------------------: ");

		log.info("----------------------------------------: ");
		log.info("sendmail-jilter - LEAVE: eoh            : ");
		log.info("----------------------------------------: ");

		return JilterStatus.SMFIS_CONTINUE;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sendmail.jilter.JilterHandlerAdapter#body(java.nio.ByteBuffer)
	 */
	public JilterStatus body(ByteBuffer bodyp) {

		log.info("----------------------------------------: ");
		log.info("sendmail-jilter - ENTRY: body           : ByteBuffer bodyp");
		log.info("----------------------------------------: ");

		log.info("bodyp                                   : " + bodyp.toString());

		String bodypString = null;
		try {
			bodypString = new String(bodyp.array(), "UTF-8");
		} catch (UnsupportedEncodingException eBodypString) {
			log.error("UnsupportedEncodingException            : " + eBodypString);
			eBodypString.printStackTrace();
		}

		log.info("bodyp      <-- (Start at next line) --> : " + CR + LF + bodypString);

		log.info("----------------------------------------: ");
		log.info("sendmail-jilter - LEAVE: body           : ByteBuffer bodyp");
		log.info("----------------------------------------: ");

		return JilterStatus.SMFIS_CONTINUE;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sendmail.jilter.JilterHandlerAdapter#eom(com.sendmail.jilter.
	 * JilterEOMActions, java.util.Properties)
	 */
	public JilterStatus eom(JilterEOMActions eomActions, Properties properties) {

		/*
		 * Here is the best place to modify anything.
		 */
		try {
			eomActions.addheader("X-Logged", "Jilter");
		} catch (IOException eEomAction) {
			log.error("IOException                             : " + eEomAction);
			eEomAction.printStackTrace();
		}

		log.info("----------------------------------------: ");
		log.info("sendmail-jilter - ENTRY: eom            : JilterEOMActions eomActions, Properties properties");
		log.info("----------------------------------------: ");

		log.info("*poperties.toString()                   : " + properties.toString());
		log.info("*poperties.getProperty(\"i\")             : " + properties.getProperty("i"));

		log.info("----------------------------------------: ");
		log.info("sendmail-jilter - LEAVE: eom            : JilterEOMActions eomActions, Properties properties");
		log.info("----------------------------------------: ");

		return JilterStatus.SMFIS_CONTINUE;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sendmail.jilter.JilterHandlerAdapter#abort()
	 */
	public JilterStatus abort() {

		log.info("----------------------------------------: ");
		log.info("sendmail-jilter - ENTRY: abort          : ");
		log.info("----------------------------------------: ");

		log.info("----------------------------------------: ");
		log.info("sendmail-jilter - LEAVE: abort          : ");
		log.info("----------------------------------------: ");

		return JilterStatus.SMFIS_CONTINUE;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sendmail.jilter.JilterHandlerAdapter#close()
	 */
	public JilterStatus close() {

		log.info("----------------------------------------: ");
		log.info("sendmail-jilter - ENTRY: close          : ");
		log.info("----------------------------------------: ");

		log.info("----------------------------------------: ");
		log.info("sendmail-jilter - LEAVE: close          : ");
		log.info("----------------------------------------: ");

		return JilterStatus.SMFIS_CONTINUE;
	}

}
