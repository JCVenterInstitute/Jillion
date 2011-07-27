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

import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.JobInfo;

import java.util.Collections;
import java.util.Map;


/**
 * {@code TimeoutJobInfo} is a specific implementation
 * of a {@link JobInfo} when a Job gets timed out.
 * This makes it easier to see that a job has timed out
 * opposed to stoped or errored out for another reason.
 * @author dkatzel
 * @author aresnick
 */
public final class TimeoutJobInfo implements JobInfo {

    private final String jobId;

    public TimeoutJobInfo(String jobId) {
        if(jobId==null){
            throw new NullPointerException("job id can not be null");
        }
        this.jobId = jobId;
    }

    @Override
    public String getJobId() throws DrmaaException {
        return jobId;
    }

    @Override
    public Map getResourceUsage() throws DrmaaException {
        return Collections.emptyMap();
    }

    @Override
    public boolean hasExited() throws DrmaaException {
        return false;
    }

    @Override
    public int getExitStatus() throws DrmaaException {
        throw new IllegalStateException("job did not run to completion");
    }

    @Override
    public boolean hasSignaled() throws DrmaaException {
        return false;
    }

    @Override
    public String getTerminatingSignal() throws DrmaaException {
        throw new IllegalStateException("job not terminated due to termination signal");
    }

    @Override
    public boolean hasCoreDump() throws DrmaaException {
       return false;
    }

    @Override
    public boolean wasAborted() throws DrmaaException {
        return true;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + jobId.hashCode();
        return result;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof TimeoutJobInfo)){
            return false;
        }
        TimeoutJobInfo other = (TimeoutJobInfo) obj;
        return jobId.equals(other.jobId);
    }
    
    
}
