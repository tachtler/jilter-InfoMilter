/**
 * Copyright (c) 2018 Klaus Tachtler. All Rights Reserved.
 * Klaus Tachtler. <klaus@tachtler.net>
 * http://www.tachtler.net
 * 
 * Copyright (c) 2001-2004 Sendmail, Inc. All Rights Reserved.
 */
package com.sendmail.jilter;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.util.regex.Pattern;

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
class CustomJilterStatus extends JilterStatus {

	private static Logger log = LogManager.getLogger();

	String reply = null;

	private static void validateRcode(String rcode) throws IllegalArgumentException {
		if (rcode == null) {
			throw new IllegalArgumentException("rcode cannot be null");
		}
		if (!Pattern.matches("[45]\\d\\d", rcode)) {
			throw new IllegalArgumentException("rcode must be a 4xx or 5xx code");
		}
	}

	private static void validateXcode(String xcode, String rcode) throws IllegalArgumentException {
		if (xcode == null) {
			return;
		}

		/*
		 * From RFC 2034 Section 4
		 * 
		 * status-code ::= class "." subject "." detail class ::= "2" / "4" / "5"
		 * subject ::= 1*3digit detail ::= 1*3digit
		 */

		if (!Pattern.matches("[45]\\.\\d{1,3}\\.\\d{1,3}", xcode)) {
			throw new IllegalArgumentException("xcode must be a 4.x.x or 5.x.x code");
		}

		// Classes must match -- 4xx rcode must match 4.x.x xcode, and 5xx rcode must
		// match 5.x.x xcode

		if (rcode.charAt(0) != xcode.charAt(0)) {
			throw new IllegalArgumentException("xcode class must match rcode class");
		}
	}

	CustomJilterStatus(String rcode, String xcode, String[] messageLines) throws IllegalArgumentException {
		validateRcode(rcode);
		validateXcode(xcode, rcode);

		if (messageLines.length == 0) {
			this.reply = rcode + " ";
			if (xcode != null) {
				this.reply += xcode;
			}
		} else {
			this.reply = "";
			for (int counter = 0; counter < messageLines.length; ++counter) {
				boolean isLastLine = (counter == (messageLines.length - 1));

				this.reply += (rcode + ((isLastLine) ? " " : "-"));
				if (xcode != null) {
					this.reply += xcode;
					this.reply += " ";
				}

				this.reply += messageLines[counter];

				this.reply += ((isLastLine) ? "" : "\r\n");
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("Completed reply is: \"" + this.reply + "\"");
		}
	}

	public void sendReplyPacket(WritableByteChannel writeChannel) throws IOException {
		JilterServerPacketUtil.sendReplyCodePacket(writeChannel, this.reply);
	}
}
