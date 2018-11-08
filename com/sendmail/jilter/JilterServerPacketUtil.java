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
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*******************************************************************************
 * Sendmail-Jilter is an Open Source implementation of the Sendmail milter
 * protocol, for implementing milters in Java that can interface with the
 * Sendmail MTA based on a project of sendmail-jilter
 * http://sendmail-jilter.sourceforge.net/
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
public class JilterServerPacketUtil {

	private static Logger log = LogManager.getLogger();

	private static final ByteBuffer zeroBuffer = ByteBuffer.wrap(new byte[0]);

	private JilterServerPacketUtil() {
	}

	public static void sendPacket(WritableByteChannel writeChannel, int command, ByteBuffer dataBuffer)
			throws IOException {
		ByteBuffer headerBuffer = ByteBuffer.allocate(5);
		int totalDataLength = 0;

		if (dataBuffer == null) {
			dataBuffer = zeroBuffer;
		}

		totalDataLength = (dataBuffer.remaining() + 1);

		log.debug("Sending packet");
		headerBuffer.putInt(totalDataLength);
		headerBuffer.put((byte) command);
		headerBuffer.flip();
		writeChannel.write(headerBuffer);
		writeChannel.write(dataBuffer);

		log.debug("Done sending packet");
	}

	public static int zeroTerminatedStringLength(String string) {
		return (string == null) ? 1 : (string.length() + 1);
	}

	public static void writeZeroTerminatedString(ByteBuffer dataBuffer, String string) throws IOException {
		if (string != null) {
			dataBuffer.put(string.getBytes("ASCII"));
		}
		dataBuffer.put((byte) 0);
	}

	public static void sendAddRcptPacket(WritableByteChannel writeChannel, String recipient) throws IOException {
		ByteBuffer dataBuffer = ByteBuffer.allocate(zeroTerminatedStringLength(recipient));

		// char rcpt[] New recipient, NUL terminated

		writeZeroTerminatedString(dataBuffer, recipient);

		sendPacket(writeChannel, JilterConstants.SMFIR_ADDRCPT, (ByteBuffer) dataBuffer.flip());
	}

	public static void sendDelRcptPacket(WritableByteChannel writeChannel, String recipient) throws IOException {
		ByteBuffer dataBuffer = ByteBuffer.allocate(zeroTerminatedStringLength(recipient));

		// char rcpt[] Recipient to remove, NUL terminated
		// (string must match the one in SMFIC_RCPT exactly)

		writeZeroTerminatedString(dataBuffer, recipient);

		sendPacket(writeChannel, JilterConstants.SMFIR_DELRCPT, (ByteBuffer) dataBuffer.flip());
	}

	public static void sendAddHeaderPacket(WritableByteChannel writeChannel, String header, String value)
			throws IOException {
		ByteBuffer dataBuffer = ByteBuffer
				.allocate(zeroTerminatedStringLength(header) + zeroTerminatedStringLength(value));

		// char name[] Name of header, NUL terminated
		// char value[] Value of header, NUL terminated

		writeZeroTerminatedString(dataBuffer, header);
		writeZeroTerminatedString(dataBuffer, value);

		sendPacket(writeChannel, JilterConstants.SMFIR_ADDHEADER, (ByteBuffer) dataBuffer.flip());
	}

	public static void sendChgHeaderPacket(WritableByteChannel writeChannel, int index, String header, String value)
			throws IOException {
		ByteBuffer dataBuffer = ByteBuffer
				.allocate(4 + zeroTerminatedStringLength(header) + zeroTerminatedStringLength(value));

		// uint32 index Index of the occurrence of this header
		// char name[] Name of header, NUL terminated
		// char value[] Value of header, NUL terminated

		dataBuffer.putInt(index);
		writeZeroTerminatedString(dataBuffer, header);
		writeZeroTerminatedString(dataBuffer, value);

		sendPacket(writeChannel, JilterConstants.SMFIR_CHGHEADER, (ByteBuffer) dataBuffer.flip());
	}

	public static void sendReplBodyPacket(WritableByteChannel writeChannel, ByteBuffer dataBuffer) throws IOException {
		sendPacket(writeChannel, JilterConstants.SMFIR_REPLBODY, dataBuffer);
	}

	public static void sendProgressPacket(WritableByteChannel writeChannel) throws IOException {
		sendPacket(writeChannel, JilterConstants.SMFIR_PROGRESS, null);
	}

	public static void sendReplyCodePacket(WritableByteChannel writeChannel, String reply) throws IOException {
		ByteBuffer dataBuffer = ByteBuffer.allocate(zeroTerminatedStringLength(reply));

		// char smtpcode[3] Nxx code (ASCII), not NUL terminated
		// char space ' '
		// char text[] Text of reply message, NUL terminated

		// ('%' characters present in "text" must be doubled to prevent problems
		// with printf-style formatting that may be used by the MTA.)

		writeZeroTerminatedString(dataBuffer, reply);

		sendPacket(writeChannel, JilterConstants.SMFIR_REPLYCODE, (ByteBuffer) dataBuffer.flip());
	}

	public static String getZeroTerminatedString(ByteBuffer dataBuffer) {
		StringBuffer newString = new StringBuffer();

		while (dataBuffer.remaining() > 0) {
			byte thisByte = dataBuffer.get();
			if (thisByte == 0) {
				break;
			}
			newString.append((char) thisByte);
		}

		return newString.toString();
	}

	public static String[] getZeroTerminatedStringArray(ByteBuffer dataBuffer) {
		ArrayList<String> array = new ArrayList<String>();

		while (dataBuffer.remaining() > 0) {
			array.add(getZeroTerminatedString(dataBuffer));
		}

		return (String[]) array.toArray(new String[array.size()]);
	}
}
