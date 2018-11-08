/**
 * Copyright (c) 2018 Klaus Tachtler. All Rights Reserved.
 * Klaus Tachtler. <klaus@tachtler.net>
 * http://www.tachtler.net
 * 
 * Copyright (c) 2001-2004 Sendmail, Inc. All Rights Reserved.
 */
package com.sendmail.jilter;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*******************************************************************************
 * Sendmail-Jilter is an Open Source implementation of the Sendmail milter
 * protocol, for implementing milters in Java that can interface with the
 * Sendmail MTA based on a project of sendmail-jilter
 * http://sendmail-jilter.sourceforge.net/
 * 
 * The guts of handling the filter side of the Milter protocol. If you have your
 * own way that you like to handle communicating with the MTA side of the Milter
 * protocol, you can feed an instance of this class the bytes from the MTA, and
 * it will handle calling methods in a {@link JilterHandler}, as well as sending
 * data back to the MTA via an arbitrary {@link WritableByteChannel}.
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
public class JilterProcessor {

	private static Logger log = LogManager.getLogger();

	private JilterHandler handler = null;
	private JilterPacket packet = new JilterPacket();
	private Properties lastProperties = null;

	/**
	 * Public constructor.
	 * 
	 * @param handler the underlying handler that will receive calls based on the
	 *                Milter conversation.
	 */
	public JilterProcessor(JilterHandler handler) {
		this.handler = handler;
	}

	/**
	 * Process more data from the MTA.
	 * 
	 * @param writeChannel the data channel for communicating back to the MTA.
	 * @param dataBuffer   the next chunk of data from the MTA.
	 * @return <code>false</code> if processing is completed.
	 */
	public boolean process(WritableByteChannel writeChannel, ByteBuffer dataBuffer) throws IOException {
		while (this.packet.process(dataBuffer)) {
			if (!processCurrentPacket(writeChannel)) {
				return false;
			}

			this.packet.reset();
		}

		return true;
	}

	private static boolean isBitSet(int bit, int position) {
		return (bit & position) != 0;
	}

	private boolean processCurrentPacket(WritableByteChannel writeChannel) throws IOException {
		boolean returnCode = true;

		if (log.isDebugEnabled()) {
//            log.debug(">SMFIC command is '" + ((char) this.packet.getCommand()) + "', Raw packet data:" + Util.newline() + Util.hexDumpLong(this.packet.getData()));
			log.debug(">SMFIC command is '" + ((char) this.packet.getCommand()) + "'");
		}

		switch (this.packet.getCommand()) {
		case JilterConstants.SMFIC_CONNECT:
			log.debug("SMFIC_CONNECT");
			processConnectPacket(writeChannel);
			break;

		case JilterConstants.SMFIC_MACRO:
			log.debug("SMFIC_MACRO");
			processMacroPacket(writeChannel);
			break;

		case JilterConstants.SMFIC_HELO:
			log.debug("SMFIC_HELO");
			processHeloPacket(writeChannel);
			break;

		case JilterConstants.SMFIC_MAIL:
			log.debug("SMFIC_MAIL");
			processMailPacket(writeChannel);
			break;

		case JilterConstants.SMFIC_RCPT:
			log.debug("SMFIC_RCPT");
			processRcptPacket(writeChannel);
			break;

		case JilterConstants.SMFIC_BODYEOB:
			log.debug("SMFIC_BODYEOB");
			processBodyEOBPacket(writeChannel);
			break;

		case JilterConstants.SMFIC_HEADER:
			log.debug("SMFIC_HEADER");
			processHeaderPacket(writeChannel);
			break;

		case JilterConstants.SMFIC_EOH:
			log.debug("SMFIC_EOH");
			processEOHPacket(writeChannel);
			break;

		case JilterConstants.SMFIC_OPTNEG:
			log.debug("SMFIC_OPTNEG");
			processOptnegPacket(writeChannel);
			break;

		case JilterConstants.SMFIC_QUIT:
			log.debug("SMFIC_QUIT");
			returnCode = false;
			break;

		case JilterConstants.SMFIC_BODY:
			log.debug("SMFIC_BODY");
			processBodyPacket(writeChannel);
			break;

		case JilterConstants.SMFIC_ABORT:
			log.debug("SMFIC_ABORT");
			processAbortPacket(writeChannel);
			break;

		default:
			log.error("Unhandled case", new Exception());
			JilterServerPacketUtil.sendPacket(writeChannel, JilterConstants.SMFIR_CONTINUE, null);
			break;
		}

		return returnCode;
	}

	private void processOptnegPacket(WritableByteChannel writeChannel) throws IOException {
		int smfif = this.handler.getRequiredModifications();
		int smfip = 0;
		int supported = this.handler.getSupportedProcesses();

		if (!isBitSet(supported, JilterHandler.PROCESS_CONNECT)) {
			smfip |= JilterConstants.SMFIP_NOCONNECT;
		}
		if (!isBitSet(supported, JilterHandler.PROCESS_HEADER)) {
			smfip |= JilterConstants.SMFIP_NOHDRS;
		}
		if (!isBitSet(supported, JilterHandler.PROCESS_HELO)) {
			smfip |= JilterConstants.SMFIP_NOHELO;
		}
		if (!isBitSet(supported, JilterHandler.PROCESS_BODY)) {
			smfip |= JilterConstants.SMFIP_NOBODY;
		}
		if (!isBitSet(supported, JilterHandler.PROCESS_ENVRCPT)) {
			smfip |= JilterConstants.SMFIP_NORCPT;
		}
		if (!isBitSet(supported, JilterHandler.PROCESS_ENVFROM)) {
			smfip |= JilterConstants.SMFIP_NOMAIL;
		}

		log.debug("Supported flags " + Integer.toHexString(supported) + " maps to SMFIP_ flags "
				+ Integer.toHexString(smfip));

		ByteBuffer optionData = ByteBuffer.wrap(new byte[] { 0x00, 0x00, 0x00, 0x02, // version
				0x00, 0x00, 0x00, (byte) smfif, // SMFIF_
				0x00, 0x00, 0x00, (byte) smfip, // SMFIP_
		});
		JilterServerPacketUtil.sendPacket(writeChannel, JilterConstants.SMFIC_OPTNEG, optionData);
	}

	private void processBodyPacket(WritableByteChannel writeChannel) throws IOException {
		sendReplyPacket(writeChannel, this.handler.body(this.packet.getData()));
	}

	private void processEOHPacket(WritableByteChannel writeChannel) throws IOException {
		sendReplyPacket(writeChannel, this.handler.eoh());
	}

	private void processHeaderPacket(WritableByteChannel writeChannel) throws IOException {
		String name = null;
		String value = null;
		ByteBuffer dataBuffer = this.packet.getData();

		// char name[]

		name = JilterServerPacketUtil.getZeroTerminatedString(dataBuffer);

		// char value[]

		value = JilterServerPacketUtil.getZeroTerminatedString(dataBuffer);

		sendReplyPacket(writeChannel, this.handler.header(name, value));
	}

	private void processBodyEOBPacket(WritableByteChannel writeChannel) throws IOException {
		JilterStatus status;
		JilterEOMActions eomactions;

		eomactions = new JilterEOMActionsImpl(writeChannel);

		status = this.handler.eom(eomactions, this.lastProperties);
		if (status != null) {
			eomactions.finish(status);
		}
	}

	private void processRcptPacket(WritableByteChannel writeChannel) throws IOException {
		String argv[] = null;
		ByteBuffer dataBuffer = this.packet.getData();

		// char args[][]

		argv = JilterServerPacketUtil.getZeroTerminatedStringArray(dataBuffer);
		log.debug("Recipient is \"" + argv[0] + "\"");

		sendReplyPacket(writeChannel, this.handler.envrcpt(argv, this.lastProperties));
	}

	private void processMailPacket(WritableByteChannel writeChannel) throws IOException {
		String argv[] = null;
		ByteBuffer dataBuffer = this.packet.getData();

		// char args[][]

		argv = JilterServerPacketUtil.getZeroTerminatedStringArray(dataBuffer);
		log.debug("Sender is \"" + argv[0] + "\"");

		sendReplyPacket(writeChannel, this.handler.envfrom(argv, this.lastProperties));
	}

	private void processHeloPacket(WritableByteChannel writeChannel) throws IOException {
		String helohost = null;
		ByteBuffer dataBuffer = this.packet.getData();

		// char helo[]

		helohost = JilterServerPacketUtil.getZeroTerminatedString(dataBuffer);
		log.debug("Client identifier parsed as \"" + helohost + "\"");

		sendReplyPacket(writeChannel, this.handler.helo(helohost, this.lastProperties));
	}

	private void processMacroPacket(WritableByteChannel writeChannel) {
		ByteBuffer dataBuffer = this.packet.getData();
		String[] propertiesStrings = null;

		// char cmdcode

		dataBuffer.get();

		// char nameval[][]

		propertiesStrings = JilterServerPacketUtil.getZeroTerminatedStringArray(dataBuffer);
		this.lastProperties = new Properties();

		for (int counter = 0; counter < propertiesStrings.length; counter += 2) {
			log.debug("Setting property " + propertiesStrings[counter] + " = " + propertiesStrings[counter + 1]);
			this.lastProperties.setProperty(propertiesStrings[counter], propertiesStrings[counter + 1]);
		}

		// No reply at all...
	}

	private void processConnectPacket(WritableByteChannel writeChannel) throws IOException {
		InetAddress address = null;
		ByteBuffer dataBuffer = this.packet.getData();
		String hostname = null;

		// char hostname[]

		hostname = JilterServerPacketUtil.getZeroTerminatedString(dataBuffer);

		// char family

		if (dataBuffer.get() == JilterConstants.SMFIA_INET) {
			// uint16 port

			dataBuffer.getShort();

			// char address[]

			{
				String stringAddress = null;

				stringAddress = JilterServerPacketUtil.getZeroTerminatedString(dataBuffer);
				log.debug("Parsed IP address is " + stringAddress);
				address = InetAddress.getByName(stringAddress);
			}
		}

		sendReplyPacket(writeChannel, this.handler.connect(hostname, address, this.lastProperties));
	}

	private void sendReplyPacket(WritableByteChannel writeChannel, JilterStatus status) throws IOException {
		status.sendReplyPacket(writeChannel);
	}

	private void processAbortPacket(WritableByteChannel writeChannel) {
		this.handler.abort();

		// No reply at all...
	}

	/**
	 * Closes this processor. Will do the right thing to communicate to the
	 * underlying handler that processing is completed.
	 */
	public void close() {
		this.packet.reset();
		this.handler.close();
		this.lastProperties = null;
	}
}
