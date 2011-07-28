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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.easymock.EasyMockSupport;
import org.ggf.drmaa.Session;
import org.jcvi.common.command.grid.TestGridHelper.CancellableGridJobHelper;
import org.jcvi.common.command.grid.TestGridHelper.LongRunningGridJobHelper;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestGridJobExecutorService extends EasyMockSupport{

    private Session mockSession;
    private GridJobExecutorService sut;
    private final String name = "serviceName";
    @Before
    public void setup(){
        mockSession = createMock(Session.class);
        sut = new GridJobExecutorService(mockSession, name, 2);
    }
    
    @Test
    public void getName(){
        assertEquals(name, sut.getName());
    }
    @Test
    public void testToString(){
        assertEquals("GridJobExecutorService:"+name, sut.toString());
    }
    @Test
    public void getSession(){
        assertEquals(mockSession, sut.getSession());
    }
    
    @Test
    public void submit() throws Exception{
        GridJob mockGridJob = createMock(GridJob.class);
        expect(mockGridJob.call()).andReturn(123);
        replayAll();
        GridJobFuture actualFuture =sut.submit(mockGridJob);
        assertEquals(Integer.valueOf(123), actualFuture.get());
        assertEquals(0, sut.countActiveTasks());
    }
    
    @Test
    public void submitLongRunningJobs() throws InterruptedException, ExecutionException{
       
        LongRunningGridJobHelper helper1 = new TestGridHelper.LongRunningGridJobHelper(1234,3000);
        GridJob job1 = helper1.getMockGridJob();
        helper1.start();
        LongRunningGridJobHelper helper2 = new TestGridHelper.LongRunningGridJobHelper(999,5000);
        GridJob job2 = helper2.getMockGridJob();
        helper2.start();
       
        Thread.sleep(500);
        GridJobFuture future1 =sut.submit(job1);
        GridJobFuture future2 =sut.submit(job2);
        assertEquals(2, sut.countActiveTasks());
        assertEquals(Integer.valueOf(1234), future1.get());
        assertEquals(Integer.valueOf(999), future2.get());
    }
    private class MySubmitThread extends Thread{
        
        private final GridJobExecutorService sut;
        private final GridJob [] jobs;
        private final List<GridJobFuture> futures = new ArrayList<GridJobFuture>();;
        MySubmitThread(GridJobExecutorService sut,GridJob... jobs) {
            this.sut = sut;
            this.jobs = jobs;
        }


        @Override
        public void run() {
            for(GridJob job : jobs){
                futures.add(sut.submit(job));
            }
        }


        /**
        * {@inheritDoc}
        */
        @Override
        public void interrupt() {
            System.out.println("mySubmit interrupted");
            super.interrupt();
        }

        
    }
    @Test
    public void shutdownNow() throws InterruptedException, ExecutionException{
        CancellableGridJobHelper helper1 = new TestGridHelper.CancellableGridJobHelper(false,false);
        final GridJob job1 = helper1.getMockGridJob();
        helper1.start();
        CancellableGridJobHelper helper2 = new TestGridHelper.CancellableGridJobHelper(false,false);
        final GridJob job2 = helper2.getMockGridJob();
        helper2.start();
        MySubmitThread submitThread =new MySubmitThread(sut, job1,job2);
        submitThread.start();
        Thread.sleep(1000);
        assertEquals(2, sut.countActiveTasks());
        sut.shutdownNow();
      
        for(GridJobFuture future : submitThread.futures){
            TestGridHelper.assertIsCancelled(future);
        }
        assertEquals(0, sut.countActiveTasks());
    }
    
    
    
}
