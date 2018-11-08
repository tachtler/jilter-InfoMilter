/**
 * Copyright (c) 2018 Klaus Tachtler. All Rights Reserved.
 * Klaus Tachtler. <klaus@tachtler.net>
 * http://www.tachtler.net
 * 
 * Copyright (c) 2001-2004 Sendmail, Inc. All Rights Reserved.
 */
package com.sendmail.jilter;

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
public class JilterConstants {

	public static final int SMFIC_ABORT = 'A'; // Abort current filter checks
	public static final int SMFIC_BODY = 'B'; // Body chunk
	public static final int SMFIC_CONNECT = 'C'; // SMTP connection information
	public static final int SMFIC_MACRO = 'D'; // Define macros
	public static final int SMFIC_BODYEOB = 'E'; // End of body marker
	public static final int SMFIC_HELO = 'H'; // HELO/EHLO name
	public static final int SMFIC_QUIT_NC = 'K'; // QUIT but new connection follows
	public static final int SMFIC_HEADER = 'L'; // Mail header
	public static final int SMFIC_MAIL = 'M'; // MAIL FROM: information
	public static final int SMFIC_EOH = 'N'; // End of headers marker
	public static final int SMFIC_OPTNEG = 'O'; // Option negotiation
	public static final int SMFIC_QUIT = 'Q'; // Quit milter communication
	public static final int SMFIC_RCPT = 'R'; // RCPT TO: information
	public static final int SMFIC_DATA = 'T'; // DATA
	public static final int SMFIC_UNKNOWN = 'U'; // Any unknown command

	public static final int SMFIR_ADDRCPT = '+'; // Add recipient (modification action)
	public static final int SMFIR_DELRCPT = '-'; // Remove recipient (modification action)
	public static final int SMFIR_ADDRCPT_PAR = '2'; // Add recipient (incl. ESMTP args)
	public static final int SMFIR_SHUTDOWN = '4'; // 421: shutdown (internal to MTA)
	public static final int SMFIR_ACCEPT = 'a'; // Accept message completely (accept/reject action)
	public static final int SMFIR_REPLBODY = 'b'; // Replace body (modification action)
	public static final int SMFIR_CONTINUE = 'c'; // Accept and keep processing (accept/reject action)
	public static final int SMFIR_DISCARD = 'd'; // Set discard flag for entire message (accept/reject action)
	public static final int SMFIR_CHGFROM = 'e'; // Change envelope sender (from)
	public static final int SMFIR_CONN_FAIL = 'f'; // Cause a connection failure
	public static final int SMFIR_ADDHEADER = 'h'; // Add header (modification action)
	public static final int SMFIR_INSHEADER = 'i'; // Insert header
	public static final int SMFIR_SETSYMLIST = 'l'; // Set list of symbols (macros)
	public static final int SMFIR_CHGHEADER = 'm'; // Change header (modification action)
	public static final int SMFIR_PROGRESS = 'p'; // Progress (asynchronous action)
	public static final int SMFIR_QUARANTINE = 'q'; // Quarantine message (modification action)
	public static final int SMFIR_REJECT = 'r'; // Reject command/recipient with a 5xx (accept/reject action)
	public static final int SMFIR_SKIP = 's'; // Skip
	public static final int SMFIR_TEMPFAIL = 't'; // Reject command/recipient with a 4xx (accept/reject action)
	public static final int SMFIR_REPLYCODE = 'y'; // Send specific Nxx reply message (accept/reject action)

	public static final int SMFIA_INET = '4';
	public static final int SMFIA_INET6 = '6';

	/* old */
	public static final int SMFIP_NOCONNECT = 0x0001;
	public static final int SMFIP_NOHELO = 0x0002;
	public static final int SMFIP_NOMAIL = 0x0004;
	public static final int SMFIP_NORCPT = 0x0008;
	public static final int SMFIP_NOBODY = 0x0010;
	public static final int SMFIP_NOHDRS = 0x0020;
	public static final int SMFIP_NOEOH = 0x0040;

	/* new */
	public static final int NO_CONNECT = 0x00000001;
	public static final int NO_HELO = 0x00000002;
	public static final int NO_MAIL_FROM = 0x00000004;
	public static final int NO_RECIPIENTS = 0x00000008;
	public static final int NO_BODY = 0x00000010;
	public static final int NO_HEADERS = 0x00000020;
	public static final int NO_EOH = 0x00000040;
	public static final int NO_REPLY_FOR_HEADERS = 0x00000080;
	public static final int NO_UNKNOWN = 0x00000100;
	public static final int NO_DATA = 0x00000200;
	public static final int UNDERSTAND_SKIP = 0x00000400;
	public static final int SEND_REJECT_RECIPIENTS = 0x00000800;
	public static final int NO_REPLY_FOR_CONNECT = 0x00001000;
	public static final int NO_REPLY_FOR_HELO = 0x00002000;
	public static final int NO_REPLY_FOR_MAIL_FROM = 0x00004000;
	public static final int NO_REPLY_FOR_RECIPIENTS = 0x00008000;
	public static final int NO_REPLY_FOR_DATA = 0x00010000;
	public static final int NO_REPLY_FOR_UNKNOWN = 0x00020000;
	public static final int NO_REPLY_FOR_EOH = 0x00040000;
	public static final int NO_REPLY_FOR_BODY = 0x00080000;
	public static final int HEADER_VALUE_LEADING_SPACE = 0x00100000;

	private JilterConstants() {
	}
}
