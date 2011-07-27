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
import org.jcvi.common.core.testUtil.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestTimeOutJobInfo {

    String jobId = "12345";
    TimeoutJobInfo sut = new TimeoutJobInfo(jobId);
    
    @Test(expected = NullPointerException.class)
    public void nullJobIdShouldThrowNPE(){
        new TimeoutJobInfo(null);
    }
    @Test
    public void getJobId() throws DrmaaException{
        assertEquals(jobId,sut.getJobId());
    }
    @Test
    public void hasNotExited() throws DrmaaException{
        assertFalse(sut.hasExited());
    }
    
    @Test
    public void didNotSignal() throws DrmaaException{
        assertFalse(sut.hasSignaled());
    }
    @Test
    public void emptyResourceUsage() throws DrmaaException{
        assertTrue(sut.getResourceUsage().isEmpty());
    }
    @Test
    public void doesNotHaveCoreDump() throws DrmaaException{
        assertFalse(sut.hasCoreDump());
    }
    
    @Test
    public void wasAborted() throws DrmaaException{
        assertTrue(sut.wasAborted());
    }
    
    @Test(expected = IllegalStateException.class)
    public void getTerminatingSignalShouldThrowIllegalStateException() throws DrmaaException{
        sut.getTerminatingSignal();
    }
    
    @Test(expected = IllegalStateException.class)
    public void getExitStatusShouldThrowIllegalStateException() throws DrmaaException{
        sut.getExitStatus();
    }
    
    @Test
    public void sameJobIdShouldBeEqual(){
        TimeoutJobInfo same = new TimeoutJobInfo(jobId);
        TestUtil.assertEqualAndHashcodeSame(sut, same);
    }
    @Test
    public void sameRefBeEqual(){
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    
    @Test
    public void differentIdShouldNotBeEqual(){
        TimeoutJobInfo different = new TimeoutJobInfo("different"+jobId);
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, different);
    }
}
