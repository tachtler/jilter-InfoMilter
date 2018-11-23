/**
 * Copyright (c) 2018 Klaus Tachtler. All Rights Reserved.
 * Klaus Tachtler. <klaus@tachtler.net>
 * http://www.tachtler.net
 */
package net.tachtler.jilter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import com.sendmail.jilter.JilterHandler;
import com.sendmail.jilter.ServerRunnable;

/*******************************************************************************
 * Jilter Server for connections from an MTA.
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
public class InfoMilter implements Runnable {

	private static Logger log = LogManager.getLogger();

	private ServerSocketChannel serverSocketChannel = null;
	private Class<?> handlerClass = null;

	private JilterHandler newHandler() throws InstantiationException, IllegalAccessException {
		return (JilterHandler) this.handlerClass.newInstance();
	}

	/**
	 * Constructor.
	 */
	public InfoMilter() {
		super();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws InfoMilterCLIArgParserException {

		/*
		 * Read the arguments from command line into the dwuaFileBean.
		 */
		InfoMilterCLIArgsParserBean argsBean = new InfoMilterCLIArgsParserBean(null, 0, null, null);

		try {
			argsBean = InfoMilterCLIArgsParser.readArgs(argsBean, args);
		} catch (ParseException eArgsBean) {
			throw new InfoMilterCLIArgParserException(
					"***** Program stop, because InfoMilter could not be initialized! ***** (For more details, see error messages and caused by below).",
					eArgsBean);
		}

		/*
		 * Start JMilter only, if all required args are set.
		 */
		if (argsBean.getInetAddress() != null && argsBean.getPort() != 0 && argsBean.getLoggingEnabled() != null
				&& argsBean.getLogLevel() != null) {
			try {
				new InfoMilter(new InetSocketAddress(argsBean.getInetAddress(), argsBean.getPort()),
						"net.tachtler.jilter.InfoMilterHandler").run();
			} catch (ClassNotFoundException eClassNotFoundException) {
				log.error("ClassNotFoundException                  : " + eClassNotFoundException);
				throw new InfoMilterCLIArgParserException(
						"***** Program stop, because InfoMilter could not be initialized! ***** (For more details, see error messages and caused by below).",
						eClassNotFoundException);
			} catch (InstantiationException eInstantiationException) {
				log.error("InstantiationException                  : " + eInstantiationException);
				throw new InfoMilterCLIArgParserException(
						"***** Program stop, because InfoMilter could not be initialized! ***** (For more details, see error messages and caused by below).",
						eInstantiationException);
			} catch (IllegalAccessException eIllegalAccessException) {
				log.error("IllegalAccessException                  : " + eIllegalAccessException);
				throw new InfoMilterCLIArgParserException(
						"***** Program stop, because InfoMilter could not be initialized! ***** (For more details, see error messages and caused by below).",
						eIllegalAccessException);
			} catch (IOException eIOException) {
				log.error("IOException                             : " + eIOException);
				throw new InfoMilterCLIArgParserException(
						"***** Program stop, because InfoMilter could not be initialized! ***** (For more details, see error messages and caused by below).",
						eIOException);
			}

			/*
			 * Set the translated Log-Level.
			 */
			if (argsBean.getLoggingEnabled()) {
				if (argsBean.getLogLevel().equalsIgnoreCase("ALL")) {
					Configurator.setRootLevel(Level.ALL);
				} else if (argsBean.getLogLevel().equalsIgnoreCase("INFO")) {
					Configurator.setRootLevel(Level.INFO);
				} else if (argsBean.getLogLevel().equalsIgnoreCase("WARN")) {
					Configurator.setRootLevel(Level.WARN);
				} else if (argsBean.getLogLevel().equalsIgnoreCase("ERROR")) {
					Configurator.setRootLevel(Level.ERROR);
				} else if (argsBean.getLogLevel().equalsIgnoreCase("TRACE")) {
					Configurator.setRootLevel(Level.TRACE);
				} else if (argsBean.getLogLevel().equalsIgnoreCase("DEBUG")) {
					Configurator.setRootLevel(Level.DEBUG);
				} else if (argsBean.getLogLevel().equalsIgnoreCase("OFF")) {
					Configurator.setRootLevel(Level.OFF);
				}

			} else {
				Configurator.setRootLevel(Level.INFO);
			}
		}

	}

	@Override
	public void run() {
		while (true) {
			SocketChannel connection = null;

			try {
				log.debug("Going to accept");
				connection = this.serverSocketChannel.accept();
				log.debug("Got a connection from " + connection.socket().getInetAddress().getHostAddress());

				log.debug("Firing up new thread");
				new Thread(new ServerRunnable(connection, newHandler()),
						"Jilter " + connection.socket().getInetAddress().getHostAddress()).start();
				log.debug("Thread started");
			} catch (IOException e) {
				log.debug("Unexpected exception", e);
			} catch (InstantiationException e) {
				log.debug("Unexpected exception", e);
			} catch (IllegalAccessException e) {
				log.debug("Unexpected exception", e);
			}

		}

	}

	public SocketAddress getSocketAddress() {
		return this.serverSocketChannel.socket().getLocalSocketAddress();
	}

	public InfoMilter(SocketAddress endpoint, String handlerClassName)
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		this.handlerClass = Class.forName(handlerClassName);

		// Fire up a test handler and immediately close it to make sure everything's OK.

		newHandler().close();

		log.debug("Opening socket");
		this.serverSocketChannel = ServerSocketChannel.open();
		this.serverSocketChannel.configureBlocking(true);
		log.debug("Binding to endpoint " + endpoint);
		this.serverSocketChannel.socket().bind(endpoint);
		log.debug("Bound to " + getSocketAddress());
	}

}
