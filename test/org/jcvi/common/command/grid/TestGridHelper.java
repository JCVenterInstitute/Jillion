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

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.ggf.drmaa.DrmaaException;

/**
 * @author dkatzel
 *
 *
 */
public final class TestGridHelper {

    public static final DrmaaException EXPECTED_CANCELLATION_EXCEPTION = new DrmaaException("expected") {};
    
    public static final class LongRunningGridJobHelper extends Thread{
        private final GridJob mockGridJob;
        private final GridJobFuture sut;
        private final long jobLengthInMillis;
        private final int returnValue;

        LongRunningGridJobHelper(int returnValue, long jobLengthInMillis){
            this.mockGridJob = EasyMock.createMock(GridJob.class);
            this.sut = new GridJobFuture(mockGridJob);
            this.jobLengthInMillis = jobLengthInMillis;
            this.returnValue = returnValue;
        }
       
        
        
        /**
         * @return the mockGridJob
         */
        public GridJob getMockGridJob() {
            return mockGridJob;
        }
        /**
         * @return the sut
         */
        public GridJobFuture getSut() {
            return sut;
        }
        @Override
        public void run() {
            try {
                
                
                expect(mockGridJob.call()).andAnswer(new IAnswer<Integer>() {
   
                    @Override
                    public Integer answer() throws Throwable {                         
                        Thread.sleep(jobLengthInMillis);
                        return returnValue;
                    }
                    
                });
                EasyMock.replay(mockGridJob);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    /**
     * @author dkatzel
     *
     *
     */
    public static final class CancellableGridJobHelper extends Thread {

        volatile boolean isterminated=false;
        private final boolean throwExceptionOnTerminate;
        private final GridJob mockGridJob;
        private final GridJobFuture sut;
        private final boolean runGridJobFuture;

        CancellableGridJobHelper(){
            this(false);
        }
        CancellableGridJobHelper(boolean throwExceptionOnTerminate){
            this(throwExceptionOnTerminate,true);
        }
        CancellableGridJobHelper(boolean throwExceptionOnTerminate,boolean runGridJobFuture){
            this.mockGridJob = EasyMock.createMock(GridJob.class);
            this.sut = new GridJobFuture(mockGridJob);
      
            this.throwExceptionOnTerminate=throwExceptionOnTerminate;
            this.runGridJobFuture = runGridJobFuture;
        }
        
        /**
         * @return the mockGridJob
         */
        public GridJob getMockGridJob() {
            return mockGridJob;
        }
        /**
         * @return the sut
         */
        public GridJobFuture getSut() {
            return sut;
        }
        @Override
        public void run() {
            try {
                mockGridJob.terminate();
                expectLastCall().andAnswer(new IAnswer<Void>(){
                    @Override
                    public Void answer() throws Throwable {
                        isterminated=true;
                        if(throwExceptionOnTerminate){
                            throw EXPECTED_CANCELLATION_EXCEPTION;
                        }
                        return null;
                    }
                    
                });
                final CountDownLatch latch = new CountDownLatch(1);
                
                expect(mockGridJob.call()).andAnswer(new IAnswer<Integer>() {
   
                    @Override
                    public Integer answer() throws Throwable {                          
                        //this will infinite loop
                        //if we don't cancel
                        latch.await();
                        return null;
                    }
                    
                });
                EasyMock.replay(mockGridJob);
                if(runGridJobFuture){
                    sut.run();
                }
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
            isterminated=true;
            super.interrupt();
        }
    }
    public static void assertIsCancelledWithException(GridJobFuture future) throws InterruptedException, ExecutionException{
        assertIsCancelled(future);
        assertEquals(TestGridHelper.EXPECTED_CANCELLATION_EXCEPTION, future.getCancellationException());
        
    }
    public static void assertIsCancelled(GridJobFuture future) throws InterruptedException, ExecutionException{
        try{
            future.get();
            fail("should throw cancelation exception");
        }catch(CancellationException expected){
            
        }
        assertTrue(future.isDone());
        assertTrue(future.isCancelled());
       
    }
}
