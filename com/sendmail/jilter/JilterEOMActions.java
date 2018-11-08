/*
 * Copyright (c) 2001-2004 Sendmail, Inc. All Rights Reserved
 */

package com.sendmail.jilter;

import java.io.IOException;

import java.nio.ByteBuffer;

/**
 * Contains the actions available during {@link JilterHandler#eom eom} processing.
 */

public interface JilterEOMActions
{
    /**
     * Add a header to the current message.
     * 
     * @param headerf the header name.
     * @param headerv the header value.
     */
    public void addheader(String headerf, String headerv)
        throws IOException;

    /**
     * Change or delete a message header.
     * 
     * @param headerf the header name.
     * @param hdridx header index value (1-based). A hdridx value of 1 will modify
     *               the first occurrence of a header named headerf. If hdridx is greater than the number
     *               of times headerf appears, a new copy of headerf will be added.
     * @param headerv the new value of the given header. headerv == <code>null</code> indicates
     *                that the header should be deleted. 
     */
    public void chgheader(String headerf, int hdridx, String headerv)
        throws IOException;

    /**
     * Add a recipient for the current message.
     * 
     * @param rcpt the new recipient's address.
     */
    public void addrcpt(String rcpt)
        throws IOException;

    /**
     * Removes the named recipient from the current message's envelope.
     * 
     * @param rcpt the recipient address to be removed.
     */
    public void delrcpt(String rcpt)
        throws IOException;

    /**
     * Replaces the body of the current message. If called more than once,
     * subsequent calls result in data being appended to the new body.
     * 
     * @param bodyp a buffer containing the new body data. Body data should be in CR/LF form. 
     */
    public void replacebody(ByteBuffer bodyp)
        throws IOException;

    /**
     * Notify the MTA that an operation is still in progress.
     */
    public void progress()
        throws IOException;
    
    /**
     * Set the resulting EOM status. Note: Calling the method essentially invalidates this object. The result of any subsequent
     * calls to methods on this object is undefined.
     * 
     * @param status the resulting status of EOM processing.
     */
    public void finish(JilterStatus status)
        throws IOException;
    
}
