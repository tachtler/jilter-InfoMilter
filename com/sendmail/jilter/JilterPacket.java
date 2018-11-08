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
public class JilterPacket {

	private static Logger log = LogManager.getLogger();

	private static final int STATE_COLLECTING_LENGTH = 0;
	private static final int STATE_COLLECTING_COMMAND = 1;
	private static final int STATE_COLLECTING_DATA = 2;
	private static final int STATE_COMPLETED = 3;

	private int currentState = STATE_COLLECTING_LENGTH;
	private int currentLength = 0;
	private int currentLengthLength = 0;
	private int currentCommand = 0;
	private ByteBuffer currentData = null;
	private int currentDataLength = 0;

	private static int unsignedByteToInt(byte b) {
		return (((int) b) & 0x0FF);
	}

	public boolean process(ByteBuffer dataBuffer) throws IOException {
		int bytesToUse = 0;

		do {
			switch (this.currentState) {
			case STATE_COLLECTING_LENGTH:
				log.debug("STATE_COLLECTING_LENGTH");
				bytesToUse = Math.min(4 - this.currentLengthLength, dataBuffer.remaining());

				for (int counter = 0; counter < bytesToUse; ++counter) {
					this.currentLength <<= 8;
					this.currentLength += unsignedByteToInt(dataBuffer.get());
					++this.currentLengthLength;
				}

				if (this.currentLengthLength == 4) {
					currentState = STATE_COLLECTING_COMMAND;
					--this.currentLength; // Minus one for the command byte
					log.debug("Collected length is " + this.currentLength);
					this.currentData = ByteBuffer.allocate(this.currentLength);
				}

				break;

			case STATE_COLLECTING_COMMAND:
				log.debug("STATE_COLLECTING_COMMAND");

				this.currentCommand = unsignedByteToInt(dataBuffer.get());
				log.debug("Collected command is '" + ((char) this.currentCommand) + "'");

				this.currentState = (this.currentLength == 0) ? STATE_COMPLETED : STATE_COLLECTING_DATA;
				log.debug("New state is " + this.currentState);
				break;

			case STATE_COLLECTING_DATA:
				log.debug("STATE_COLLECTING_DATA");
				bytesToUse = Math.min(this.currentLength - this.currentDataLength, dataBuffer.remaining());

				this.currentData
						.put((ByteBuffer) dataBuffer.asReadOnlyBuffer().limit(dataBuffer.position() + bytesToUse));
				dataBuffer.position(dataBuffer.position() + bytesToUse);

				this.currentDataLength += bytesToUse;
				log.debug("Found " + bytesToUse + " bytes to apply to data");

				if (this.currentDataLength == this.currentLength) {
					log.debug("Collected all the data");

					this.currentData.flip();
					this.currentState = STATE_COMPLETED;
				}

				break;

			case STATE_COMPLETED:
				log.debug("STATE_COMPLETED");
				break;

			default:
				log.fatal("Unhandled case", new Exception());
				break;
			}
		} while ((dataBuffer.remaining() > 0) && (this.currentState != STATE_COMPLETED));

		return this.currentState == STATE_COMPLETED;
	}

	public int getCommand() {
		return this.currentCommand;
	}

	public ByteBuffer getData() {
		return this.currentData;
	}

	public void reset() {
		this.currentState = STATE_COLLECTING_LENGTH;
		this.currentLength = 0;
		this.currentLengthLength = 0;
		this.currentDataLength = 0;
		this.currentData = null;
	}
}
