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
public class JilterEOMActionsImpl implements JilterEOMActions {
	WritableByteChannel writeChannel = null;

	public JilterEOMActionsImpl(WritableByteChannel writeChannel) {
		this.writeChannel = writeChannel;
	}

	public void addheader(String headerf, String headerv) throws IOException {
		JilterServerPacketUtil.sendAddHeaderPacket(this.writeChannel, headerf, headerv);
	}

	public void chgheader(String headerf, int hdridx, String headerv) throws IOException {
		JilterServerPacketUtil.sendChgHeaderPacket(this.writeChannel, hdridx, headerf, headerv);
	}

	public void addrcpt(String rcpt) throws IOException {
		JilterServerPacketUtil.sendAddRcptPacket(this.writeChannel, rcpt);
	}

	public void delrcpt(String rcpt) throws IOException {
		JilterServerPacketUtil.sendDelRcptPacket(this.writeChannel, rcpt);
	}

	public void replacebody(ByteBuffer bodyp) throws IOException {
		JilterServerPacketUtil.sendReplBodyPacket(this.writeChannel, bodyp);
	}

	public void progress() throws IOException {
		JilterServerPacketUtil.sendProgressPacket(this.writeChannel);
	}

	public void finish(JilterStatus status) throws IOException {
		status.sendReplyPacket(this.writeChannel);
		this.writeChannel = null;
	}
}
