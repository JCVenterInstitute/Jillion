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
import java.util.concurrent.ExecutionException;

import org.easymock.EasyMockSupport;
import org.easymock.IAnswer;
import org.ggf.drmaa.Session;
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
        final GridJob mockGridJob = createMock(GridJob.class);
        final GridJob mockGridJob2 = createMock(GridJob.class);
        new Thread(){

            @Override
            public void run() {
                
                 try {
                     
                    expect(mockGridJob.call()).andAnswer(new IAnswer<Integer>() {
                         @Override
                         public Integer answer(){
                             try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                         return Integer.valueOf(3000);
                         }
                    });
                    
                    
                    expect(mockGridJob2.call()).andAnswer(new IAnswer<Integer>() {
                         @Override
                         public Integer answer(){
                             try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                         return Integer.valueOf(5000);
                         }
                    });
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                replayAll();
            }
        }.start();
        Thread.sleep(1000);
        GridJobFuture future1 =sut.submit(mockGridJob);
        GridJobFuture future2 =sut.submit(mockGridJob2);
        assertEquals(2, sut.countActiveTasks());
        assertEquals(Integer.valueOf(3000), future1.get());
        assertEquals(Integer.valueOf(5000), future2.get());
    }
    
}
