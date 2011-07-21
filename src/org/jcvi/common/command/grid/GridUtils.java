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

package org.jcvi.common.command.grid;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.JobInfo;
import org.ggf.drmaa.Session;
import org.ggf.drmaa.SessionFactory;

/**
 * The <code>GridUtils</code> class provides a library of static methods for performing various
 * tasks with the grid.
 *
 * @author jsitz@jcvi.org
 * @author dkatzel
 */
public final class GridUtils
{
    /** The default {@link Session} initializtion contact to use if none is supplied. */
    private static final String DEFAULT_CONTACT = "";

    /** An appropriate {@link SessionFactory} for the environment. */
    private static final SessionFactory SESSION_FACTORY = SessionFactory.getFactory();

    /** The global DRMAA {@link Session}. */
    private static Session GLOBAL_SESSION;

    /** The lock which controls access to the global {@link Session}. */
    private static final Lock SESSION_LOCK = new ReentrantLock();

    /**
     * Fetch the global {@link Session}.  This is a singleton instance, so all calls to this
     * method are guaranteed to return the same reference.
     *
     * @return An initalized {@link Session} object.
     */
    public static Session getGlobalSession()
    {
        SESSION_LOCK.lock();
        try
        {
            if (GLOBAL_SESSION == null)
            {
                GLOBAL_SESSION = buildNewSession();
                Runtime.getRuntime().addShutdownHook(new Thread(){

                    @Override
                    public void run() {
                        try {
                            GLOBAL_SESSION.exit();
                        } catch (DrmaaException e) {
                        }
                    }
                    
                });
            }

            return GLOBAL_SESSION;
        }
        finally
        {
            SESSION_LOCK.unlock();
        }
    }

    /**
     * Builds a new DRMAA {@link Session}.
     *
     * @param contact An implementation-dependent string declaring which grid implementation is
     * intended to be used.  This may be set to <code>null</code> or the empty string to use the
     * default implementation.
     * @return A new {@link Session}.
     * @throws GridInitializationException If the <code>Session</code> could not be initialized.
     * @see Session#init(String)
     */
    public static Session buildNewSession(String contact) throws GridInitializationException
    {
        try
        {
            final Session session = SESSION_FACTORY.getSession();
            session.init(contact);

            return session;
        }
        catch (final DrmaaException e)
        {
            throw new GridInitializationException("Error while initializing session.", e);
        }
    }

    /**
     * Builds a new DRMAA {@link Session} with the default contact ({@value #DEFAULT_CONTACT}).
     *
     * @return A new {@link Session}.
     * @throws GridInitializationException If the <code>Session</code> could not be initialized.
     * @see #buildNewSession(String)
     */
    public static Session buildNewSession() throws GridInitializationException
    {
        return buildNewSession(DEFAULT_CONTACT);
    }

    public static GridJob.Status getJobStatus(GridJob job) throws DrmaaException {
        for ( String jobID : job.getJobIDList() ) {
            GridJob.Status status = GridUtils.getJobStatus(job.getJobInfoMap().get(jobID));
            if ( status != GridJob.Status.COMPLETED ) {
                return status;
            }
        }

        return GridJob.Status.COMPLETED;
    }

    public static GridJob.Status getJobStatus(JobInfo jobInfo) throws DrmaaException 
    {
        if ( jobInfo == null ) {
            return GridJob.Status.UNKNOWN;
        } else if ( jobInfo.wasAborted() ) {
            if ( jobInfo instanceof JobInfoTimeout ) {
                return GridJob.Status.TIMED_OUT;
            }
            return GridJob.Status.ABORTED;
        } else if ( jobInfo.hasSignaled() ) {
            return GridJob.Status.SIGNALLED;
        } else {
            return GridJob.Status.COMPLETED;
        }
    }

    public static String printJobInfo(JobInfo info) throws DrmaaException {
        if ( info == null ) {
            return "job info is null";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Job " + info.getJobId() + " job status is " + GridUtils.getJobStatus(info));
        builder.append("\nExit status value is: " + (info.hasExited() ? info.getExitStatus() : " not applicable"));

        // Output usage information
        builder.append("\nJob Usage:");

        // try to get the map
        Map<String,String> rmap = info.getResourceUsage();
        if (rmap != null && !rmap.isEmpty()) {
            for(Entry<String,String> entry : rmap.entrySet()){
                builder.append("\n  " + entry.getKey() + "=" + entry.getValue());
            }
        } else {
            builder.append("\n  not available");
        }

        return builder.toString();
    }

}

