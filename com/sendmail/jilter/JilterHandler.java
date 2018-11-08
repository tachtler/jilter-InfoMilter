/*
 * Copyright (c) 2001-2004 Sendmail, Inc. All Rights Reserved
 */

package com.sendmail.jilter;

import java.net.InetAddress;

import java.nio.ByteBuffer;
import java.util.Properties;

/**
 * The main handler interface for writing a Java-based milter (Jilter).
 */

public interface JilterHandler
{
    /**
     * Flag for {@link #getSupportedProcesses getSupportedProcesses} to
     * indicate that {@link #connect connect} is supported. 
     */
    public static final int PROCESS_CONNECT = 0x0001;

    /**
     * Flag for {@link #getSupportedProcesses getSupportedProcesses} to
     * indicate that {@link #helo helo} is supported. 
     */
    public static final int PROCESS_HELO = 0x0002;

    /**
     * Flag for {@link #getSupportedProcesses getSupportedProcesses} to
     * indicate that {@link #envfrom envfrom} is supported. 
     */
    public static final int PROCESS_ENVFROM = 0x0004;

    /**
     * Flag for {@link #getSupportedProcesses getSupportedProcesses} to
     * indicate that {@link #envrcpt} is supported. 
     */
    public static final int PROCESS_ENVRCPT = 0x0008;

    /**
     * Flag for {@link #getSupportedProcesses getSupportedProcesses} to
     * indicate that {@link #header header} is supported. 
     */
    public static final int PROCESS_HEADER = 0x0010;

    /**
     * Flag for {@link #getSupportedProcesses getSupportedProcesses} to
     * indicate that {@link #body body} is supported. 
     */
    public static final int PROCESS_BODY = 0x0020;

    /**
     * Flag for {@link #getRequiredModifications getRequiredModifications} to
     * indicate that no modifications will be made. 
     */
    public static final int SMFIF_NONE = 0x0000;

    /**
     * Flag for {@link #getRequiredModifications getRequiredModifications} to
     * indicate that headers may be added. 
     */
    public static final int SMFIF_ADDHDRS = 0x0001;

    /**
     * Flag for {@link #getRequiredModifications getRequiredModifications} to
     * indicate that the body may be changed. 
     */
    public static final int SMFIF_CHGBODY = 0x0002;

    /**
     * Flag for {@link #getRequiredModifications getRequiredModifications} to
     * indicate that headers may be added. 
     */
    public static final int SMFIF_MODBODY = SMFIF_CHGBODY;

    /**
     * Flag for {@link #getRequiredModifications getRequiredModifications} to
     * indicate that recipients may be added. 
     */
    public static final int SMFIF_ADDRCPT = 0x0004;

    /**
     * Flag for {@link #getRequiredModifications getRequiredModifications} to
     * indicate that recipients may be deleted. 
     */
    public static final int SMFIF_DELRCPT = 0x0008;

    /**
     * Flag for {@link #getRequiredModifications getRequiredModifications} to
     * indicate that headers may be changed or deleted. 
     */
    public static final int SMFIF_CHGHDRS = 0x0010;

    /**
     * Called once at the start of each SMTP connection.
     * 
     * @param hostname The host name of the message sender, as determined by a reverse lookup on the host address.
     * @param hostaddr The host address, as determined by a <code>getpeername()</code> call on the SMTP socket.
     * @param properties Any properties (macros) received from the MTA.
     * @return <code>SMFIS_</code> return codes from {@link JilterStatus}. <b>NOTE:</b> The MTA will currently
     *         ignore any custom values (values other than <code>SMFIS_</code> values). Specifically, values
     *         created with {@link JilterStatus#makeCustomStatus JilterStatus.makeCustomStatus} will not be honored.
     */
    JilterStatus connect(String hostname, InetAddress hostaddr, Properties properties);

    /**
     * Handle the HELO/EHLO command. Called whenever the client sends a HELO/EHLO command.
     * It may therefore be called between zero and three times.
     * 
     * @param helohost Value passed to HELO/EHLO command, which should be the domain name of the sending host (but is,
     *                 in practice, anything the sending host wants to send).
     * @param properties Any properties (macros) received from the MTA.
     * @return <code>SMFIS_</code> return codes from {@link JilterStatus}.
     */
    JilterStatus helo(String helohost, Properties properties);

    /**
     * Handle the envelope FROM command. Called once at the beginning of each message,
     * before <code>envrcpt</code>.
     * 
     * @param argv An array of SMTP command arguments. <code>argv[0]</code> is guaranteed to be the sender address.
     *             Later arguments are the ESMTP arguments.
     * @param properties Any properties (macros) received from the MTA.
     * @return <code>SMFIS_</code> return codes from {@link JilterStatus}.
     */
    JilterStatus envfrom(String[] argv, Properties properties);

    /**
     * Handle the envelope RCPT command. Called once per recipient, hence one or more
     * times per message, immediately after {@link #envfrom envfrom}.
     * 
     * @param argv An array of SMTP command arguments. <code>argv[0]</code> is guaranteed to be the recipient address.
     *             Later arguments are the ESMTP arguments.
     * @param properties Any properties (macros) received from the MTA.
     * @return <code>SMFIS_</code> return codes from {@link JilterStatus}.
     */
    JilterStatus envrcpt(String[] argv, Properties properties);

    /**
     * Handle a message header. Called zero or more times between {@link #envrcpt envrcpt}
     * and {@link #eoh eoh}, once per message header.
     * 
     * @param headerf Header field name.
     * @param headerv Header field value. The content of the header may include folded white space (i.e. multiple lines
     *                with following white space). The trailing line terminator (CR/LF) is removed.
     * @return <code>SMFIS_</code> return codes from {@link JilterStatus}.
     */
    JilterStatus header(String headerf, String headerv);

    /**
     * Handle the end of message headers. Called once after all headers have been sent and
     * processed.
     * 
     * @return <code>SMFIS_</code> return codes from {@link JilterStatus}.
     */
    JilterStatus eoh();

    /**
     * Handle a piece of a message's body. Called zero or more times between
     * {@link #eoh eoh} and {@link #eom eom}.
     * 
     * @param bodyp This block of body data.
     * @return <code>SMFIS_</code> return codes from {@link JilterStatus}.
     */
    JilterStatus body(ByteBuffer bodyp);

    /**
     * End of a message. Called once after all calls to {@link #body body} for a given message.
     * 
     * @param eomActions Interface for effecting message changes.
     * @param properties Any properties (macros) received from the MTA.
     * @return <code>SMFIS_</code> return codes from {@link JilterStatus}.
     */
    JilterStatus eom(JilterEOMActions eomActions, Properties properties);

    /**
     * Handle the current message being aborted. Called at any time during message
     * processing (i.e. between some message-oriented routine and {@link #eom eom}).
     * 
     * @return <code>SMFIS_</code> return codes from {@link JilterStatus}.
     */
    JilterStatus abort();

    /**
     * The current connection is being closed. Always called at the end of each connection.
     * 
     * @return <code>SMFIS_</code> return codes from {@link JilterStatus}.
     */
    JilterStatus close();

    /**
     * Get the list of callbacks implemented by this handler.
     * 
     * @return a combination of values from the <code>PROCESS_</code> constants.
     */
    int getSupportedProcesses();

    /**
     * Get the list of required modifications needed by this handler.
     * 
     * @return a combination of values from the <code>SMFIF_</code> constants.
     */
    int getRequiredModifications();
}
