/*
 * Copyright (c) 2005 Sendmail, Inc. All Rights Reserved
 */

package com.sendmail.jilter;

import java.net.InetAddress;

import java.nio.ByteBuffer;

import java.util.Properties;

/**
 * An adapter to implement methods in JilterHander interface so subclasses
 * may override only those methods they desire.
 *
 * @author  Neil Aggarwal (neil@JAMMConsulting.com)
 */

public abstract class JilterHandlerAdapter implements JilterHandler
{
    public JilterStatus connect(String hostname, InetAddress hostaddr, Properties properties)
    {
        return JilterStatus.SMFIS_CONTINUE;
    }

    public JilterStatus helo(String helohost, Properties properties)
    {
        return JilterStatus.SMFIS_CONTINUE;
    }

    public JilterStatus envfrom(String[] argv, Properties properties)
    {
        return JilterStatus.SMFIS_CONTINUE;
    }

    public JilterStatus envrcpt(String[] argv, Properties properties)
    {
        return JilterStatus.SMFIS_CONTINUE;
    }

    public JilterStatus header(String headerf, String headerv)
    {
        return JilterStatus.SMFIS_CONTINUE;
    }

    public JilterStatus eoh()
    {
        return JilterStatus.SMFIS_CONTINUE;
    }

    public JilterStatus body(ByteBuffer bodyp)
    {
        return JilterStatus.SMFIS_CONTINUE;
    }

    public JilterStatus eom(JilterEOMActions eomActions, Properties properties)
    {
        return JilterStatus.SMFIS_CONTINUE;
    }

    public JilterStatus abort()
    {
        return JilterStatus.SMFIS_CONTINUE;
    }

    public JilterStatus close()
    {
        return JilterStatus.SMFIS_CONTINUE;
    }

    public int getRequiredModifications()
    {
        // By default, do not modify anything
        return SMFIF_NONE;
    }
}
