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

import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.JobInfo;
import org.jcvi.common.core.util.MapValueComparator;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestGridUtils extends EasyMockSupport{

    @Test
    public void getJobStatusNullJobInfoShouldReturnUnknown() throws DrmaaException{
        assertEquals(GridJob.Status.UNKNOWN, GridUtils.getJobStatus((JobInfo)null));
    }
    
    @Test
    public void abortedJobInfoShouldHaveAbortedStatus() throws DrmaaException{
        JobInfo aborted = createMock(JobInfo.class);
        expect(aborted.wasAborted()).andReturn(true);
        replayAll();
        assertEquals(GridJob.Status.ABORTED, GridUtils.getJobStatus(aborted));
        verifyAll();
    }
    @Test
    public void timedOutJobInfoShouldHaveTimedOutStatus() throws DrmaaException{
        JobInfo timedOut = new JobInfoTimeout("1234");
        assertEquals(GridJob.Status.TIMED_OUT, GridUtils.getJobStatus(timedOut));
    }
    
    @Test
    public void signaledJobInfoShouldHaveSignaledStatus() throws DrmaaException{
        JobInfo aborted = createSignaledJobInfo();
        replayAll();
        assertEquals(GridJob.Status.SIGNALLED, GridUtils.getJobStatus(aborted));
        verifyAll();
    }

    private JobInfo createSignaledJobInfo() throws DrmaaException {
        //have to manage these mocks separately
        //so EasyMockSupport doesn't replay them at
        //the wrong time.
        JobInfo aborted = EasyMock.createMock(JobInfo.class);
        expect(aborted.wasAborted()).andStubReturn(false);
        expect(aborted.hasSignaled()).andStubReturn(true);
        EasyMock.replay(aborted);
        return aborted;
    }
    @Test
    public void completedJobInfoShouldHaveCompletedStatus() throws DrmaaException{
        JobInfo complete = createCompleteJobInfo();
        replayAll();
        assertEquals(GridJob.Status.COMPLETED, GridUtils.getJobStatus(complete));
        verifyAll();
    }
    
    @Test
    public void getStatusAllJobInfosCompleteShouldReturnComplete() throws DrmaaException{
        GridJob gridJob = createMock(GridJob.class);
        Map<String, JobInfo> map = new HashMap<String, JobInfo>();
        map.put("1234", createCompleteJobInfo());
        map.put("999", createCompleteJobInfo());
        expect(gridJob.getJobInfoMap()).andReturn(map);
        replayAll();
        assertEquals(GridJob.Status.COMPLETED, GridUtils.getJobStatus(gridJob));
        verifyAll();
    }

    @Test
    public void getStatusJobIsNotCompleteJobInfosCompleteShouldReturnWorstStatus() throws DrmaaException{
        GridJob gridJob = createMock(GridJob.class);
        Map<String, JobInfo> map = new HashMap<String, JobInfo>();
        map.put("1234", createCompleteJobInfo());
        map.put("333",new JobInfoTimeout("333"));
        expect(gridJob.getJobInfoMap()).andReturn(map);
        replayAll();
        assertEquals(GridJob.Status.TIMED_OUT, GridUtils.getJobStatus(gridJob));
        verifyAll();
    }
    
    @Test
    public void getStatusJobMultipleNotCompleteJobInfosCompleteShouldReturnWorstStatus() throws DrmaaException{
        GridJob gridJob = createMock(GridJob.class);
        Map<String, JobInfo> map = new HashMap<String, JobInfo>();
        map.put("1234", createCompleteJobInfo());
        map.put("999", createSignaledJobInfo());
        map.put("333",new JobInfoTimeout("333"));
        
        expect(gridJob.getJobInfoMap()).andReturn(MapValueComparator.sortAscending(map, JobInfoStatusComparator.INSTANCE));
        replayAll();
        assertEquals(GridJob.Status.SIGNALLED, GridUtils.getJobStatus(gridJob));
        verifyAll();
    }
    
    
    private JobInfo createCompleteJobInfo() throws DrmaaException{
        JobInfo complete = EasyMock.createMock(JobInfo.class);
        expect(complete.wasAborted()).andStubReturn(false);
        expect(complete.hasSignaled()).andStubReturn(false);
        EasyMock.replay(complete);
        return complete;
    }
    
}
