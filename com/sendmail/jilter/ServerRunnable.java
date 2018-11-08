/**
 * Copyright (c) 2018 Klaus Tachtler. All Rights Reserved.
 * Klaus Tachtler. <klaus@tachtler.net>
 * http://www.tachtler.net
 * 
 * Copyright (c) 2001-2004 Sendmail, Inc. All Rights Reserved.
 */
package com.sendmail.jilter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*******************************************************************************
 * Sendmail-Jilter is an Open Source implementation of the Sendmail milter
 * protocol, for implementing milters in Java that can interface with the
 * Sendmail MTA based on a project of sendmail-jilter
 * http://sendmail-jilter.sourceforge.net/
 * 
 * Sample implementation of a handler for a socket based Milter protocol
 * connection.
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
public class ServerRunnable implements Runnable {

	private static Logger log = LogManager.getLogger();

	private SocketChannel socket = null;
	private JilterProcessor processor = null;

	/**
	 * Constructor.
	 * 
	 * @param socket  the incoming socket from the MTA.
	 * @param handler the handler containing callbacks for the milter protocol.
	 */
	public ServerRunnable(SocketChannel socket, JilterHandler handler) throws IOException {
		this.socket = socket;
		this.socket.configureBlocking(true);
		this.processor = new JilterProcessor(handler);
	}

	public void run() {
		ByteBuffer dataBuffer = ByteBuffer.allocateDirect(4096);

		try {
			while (this.processor.process(this.socket, (ByteBuffer) dataBuffer.flip())) {
				dataBuffer.compact();
				log.debug("Going to read");
				if (this.socket.read(dataBuffer) == -1) {
					log.debug("socket reports EOF, exiting read loop");
					break;
				}
				log.debug("Back from read");
			}
		} catch (IOException e) {
			log.debug("Unexpected exception, connection will be closed", e);
		} finally {
			log.debug("Closing processor");
			this.processor.close();
			log.debug("Processor closed");
			try {
				log.debug("Closing socket");
				this.socket.close();
				log.debug("Socket closed");
			} catch (IOException e) {
				log.debug("Unexpected exception", e);
			}
		}
	}
}
