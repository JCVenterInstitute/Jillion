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

import java.util.concurrent.CancellationException;

import org.easymock.EasyMockSupport;
import org.easymock.IAnswer;
import org.ggf.drmaa.DrmaaException;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestGridJobFuture extends EasyMockSupport{
    private static final DrmaaException EXPECTED_CANCELLATION_EXCEPTION = new DrmaaException("expected") {};
    
    /**
     * @author dkatzel
     *
     *
     */
    private final class GridJobBackgroundThread extends Thread {
        /**
         * 
         */
        volatile boolean isterminated=false;
        private final boolean throwExceptionOnTerminate;
        GridJobBackgroundThread(){
            this(false);
        }
        GridJobBackgroundThread(boolean throwExceptionOnTerminate){
            super();
            this.throwExceptionOnTerminate=throwExceptionOnTerminate;
        }
        @Override
        public void run() {
            try {
                mockGridJob.terminate();
                
                expectLastCall().andAnswer(new IAnswer<Void>(){
                    @Override
                    public Void answer() throws Throwable {
                        isterminated=true;
                        System.out.println("terminated");
                        if(throwExceptionOnTerminate){
                            throw EXPECTED_CANCELLATION_EXCEPTION;
                        }
                        return null;
                    }
                    
                });
                
                expect(mockGridJob.call()).andAnswer(new IAnswer<Integer>() {
   
                    @Override
                    public Integer answer() throws Throwable {                         
                        //this will infinite loop
                        //if we don't cancel
                        for(;;){
                            if(isterminated){                                    
                                break;
                            }
                        }
                        return Integer.MAX_VALUE;
                    }
                    
                });
                replayAll();
                sut.run();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        /**
         * This interrupt will be called when
         * we get the future gets canceled
         */
        @Override
        public void interrupt() {
            System.out.println("interrupted");
            isterminated=true;
            super.interrupt();
        }
    }
    GridJob mockGridJob;
    GridJobFuture sut;
    
    @Before
    public void setup(){
        mockGridJob = createMock(GridJob.class);
        sut = new GridJobFuture(mockGridJob);
    }
    
    @Test
    public void getGridJob(){
        assertSame(mockGridJob, sut.getGridJob());
    }
    @Test
    public void runToCompletion() throws Exception{
        expect(mockGridJob.call()).andReturn(0);
        replayAll();
        sut.run();
        assertEquals(Integer.valueOf(0),sut.get());
        verifyAll();
    }
    @Test
    public void cancelShouldTerminateGridJob() throws Exception{
        
        setupAndRunMockGridJobInBackgroundThread();        
        assertTrue(sut.cancel(true));
        try{
            sut.get();
            fail("should throw cancelation exception");
        }catch(CancellationException expected){
            
        }
        assertTrue(sut.isDone());
        assertTrue(sut.isCancelled());
        verifyAll();
    }
    
    @Test
    public void cancelGridJobAndCanNotTerminateShouldReturnFalse() throws Exception{
        
        setupAndRunUnTerminatableMockGridJobInBackgroundThread();        
        assertFalse(sut.cancel(true));
        try{
            sut.get();
            fail("should throw cancelation exception");
        }catch(CancellationException expected){
            
        }
        assertTrue(sut.isDone());
        assertFalse(sut.isCancelled());
        assertEquals(EXPECTED_CANCELLATION_EXCEPTION, sut.getCancellationException());
        verifyAll();
    }
    /**
     * Since we need a job that will run until
     * it is canceled, it is easier to do that in a background
     * thread that infinite loops until interrupted.
     * @throws InterruptedException 
     */
    private void setupAndRunMockGridJobInBackgroundThread() throws InterruptedException {
        new GridJobBackgroundThread().start();
        //wait for other thread to start  
        Thread.sleep(500);
    }
    
    /**
     * Since we need a job that will run until
     * it is canceled, it is easier to do that in a background
     * thread that infinite loops until interrupted.
     * @throws InterruptedException 
     */
    private void setupAndRunUnTerminatableMockGridJobInBackgroundThread() throws InterruptedException {
        new GridJobBackgroundThread(true).start();
        //wait for other thread to start  
        Thread.sleep(500);
    }
}
