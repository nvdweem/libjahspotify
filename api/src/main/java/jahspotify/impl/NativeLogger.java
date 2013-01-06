package jahspotify.impl;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *        or more contributor license agreements.  See the NOTICE file
 *        distributed with this work for additional information
 *        regarding copyright ownership.  The ASF licenses this file
 *        to you under the Apache License, Version 2.0 (the
 *        "License"); you may not use this file except in compliance
 *        with the License.  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *        Unless required by applicable law or agreed to in writing,
 *        software distributed under the License is distributed on an
 *        "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *        KIND, either express or implied.  See the License for the
 *        specific language governing permissions and limitations
 *        under the License.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Johan Lindquist
 */
public class NativeLogger
{
    private static Log _log = LogFactory.getLog(NativeLogger.class);
    public static final boolean SYSOUT = false;

    public static void trace(final String component, final String subComponent, final String message)
    {
        if (_log.isTraceEnabled())
        {
            _log.trace(formatMessage(component, subComponent, message));
        }
        if (SYSOUT)
        	System.out.println(formatMessage(component, subComponent, message));
    }

    private static Object formatMessage(final String component, final String subComponent, final String message)
    {
        return String.format("[%s::%s] %s",component, subComponent, message);
    }

    public static void debug(final String component, final String subComponent, final String message)
    {
        if (_log.isDebugEnabled())
        {
            _log.debug(formatMessage(component, subComponent, message));
        }
        if (SYSOUT)
        	System.out.println(formatMessage(component, subComponent, message));
    }

    public static void info(final String component, final String subComponent, final String message)
    {
        if (_log.isInfoEnabled())
        {
            _log.info(formatMessage(component, subComponent, message));
        }
        if (SYSOUT)
        	System.out.println(formatMessage(component, subComponent, message));
    }

    public static void warn(final String component, final String subComponent, final String message)
    {
        if (_log.isWarnEnabled())
        {
            _log.warn(formatMessage(component, subComponent, message));
        }
        if (SYSOUT)
        	System.out.println(formatMessage(component, subComponent, message));
    }

    public static void error(final String component, final String subComponent, final String message)
    {
        if (_log.isErrorEnabled())
        {
            _log.error(formatMessage(component, subComponent, message));
        }
        if (SYSOUT)
        	System.out.println(formatMessage(component, subComponent, message));
    }

    public static void fatal(final String component, final String subComponent, final String message)
    {
        if (_log.isFatalEnabled())
        {
            _log.fatal(formatMessage(component, subComponent, message));
        }
        if (SYSOUT)
        	System.out.println(formatMessage(component, subComponent, message));
    }

    public static void d(final Object o) {
    	if (o != null)
    		System.out.println("d: " + o.getClass());
    	else
    		System.out.println("d: null");
    }
}
