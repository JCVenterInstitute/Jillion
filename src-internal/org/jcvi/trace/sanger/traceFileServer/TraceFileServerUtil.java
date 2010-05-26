/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.jcvi.trace.sanger.traceFileServer;

/**
 * Utility class for TraceFileServer.
 * @author dkatzel
 *
 *
 */
public final class TraceFileServerUtil {
    /**
     * Base URL path for requesting data from the Trace File server using TIGR
     * Project Database creds.
     */
    public static final String TIGR_URL = "https://tracefileserver/TraceFileServer-tigr/TraceFileServer";
    
    /**
     * Base URL path for requesting data from the Trace File server using the 
     * Standard JCVI LDAP creds.
     */
    public static final String JCVI_FETCH_URL = "https://tracefileserver/TraceFileServer/TraceFileServer-secure";
    /**
     * List of comma separated URLS for writing data INTO the production TraceFileServer.
     */
    public static final String JCVI_WRITE_URLS = "jnp://tracefileserver-proda:1099,jnp://tracefileserver-prodb:1099";
    /**
     * Path to the HTTPS cacert file on unix.
     */
    public static final String TRACE_FILE_SERVER_TRUSTSTORE_UNIX_PATH = "/usr/local/devel/JTC/prod/dataDelivery/lib/security/cacerts";
    /**
     * Key System property used by the Chromatogram Archiver jar to set
     * the trace file server URL used to write chromatograms into JLIMS.
     */
    public static final String TRACE_FILE_SERVER_WRITER_URL_BASE_KEY= "JTCChromatogramArchiver.PROVIDER_URL";
    
    /**
     * private constructor.
     */
    private TraceFileServerUtil(){}
}
