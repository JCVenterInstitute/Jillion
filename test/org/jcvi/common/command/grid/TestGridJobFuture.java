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
    /**
     * Since we need a job that will run until
     * it is cancelled, it is easier to do that in a background
     * thread that infinite loops until interrupted.
     * @throws InterruptedException 
     */
    private void setupAndRunMockGridJobInBackgroundThread() throws InterruptedException {
        new Thread(){
            volatile boolean isterminated=false;
            @Override
            public void run() {
                try {
                    mockGridJob.terminate();
                    
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
             * we get the future gets cancelled
             */
            @Override
            public void interrupt() {
                isterminated=true;
                super.interrupt();
            }
        }.start();
        //wait for other thread to start  
        Thread.sleep(500);
    }
}
