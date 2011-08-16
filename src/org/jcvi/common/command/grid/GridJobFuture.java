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
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.ggf.drmaa.DrmaaException;
/**
 * {@code GridJobFuture} is a {@link Future}
 * implementation specifically for {@link GridJob}s
 * so you can access GridJob specific fields
 * @author dkatzel
 *
 *
 */
public class GridJobFuture extends FutureTask<Integer>{

    private final GridJob job;
    private volatile boolean wasCancelled=false;
    private Exception cancellationException = null;
    private Integer returnCode=null;
    private ExecutionException returnedException;
    
    public GridJobFuture(GridJob gridJob) {
        super(gridJob);
        this.job = gridJob;
    }
    public GridJob getGridJob() {
        return job;
    }
    /**
     * If while attempting to cancel the grid 
     * job on the grid, there is an exception thrown,
     * this method will return false and the exception
     * can be retrieved by calling {@link #getCancellationException()}.
     * @return {@code true} if the grid job was canceled; {@code false} otherwise.
     */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        super.cancel(mayInterruptIfRunning);
        try {
            job.terminate();       
            wasCancelled= true;
        } catch (DrmaaException e) {
            wasCancelled= false;
            cancellationException=e;
        }
        return wasCancelled;
    }
    @Override
    public boolean isCancelled() {
        return wasCancelled;
    }
    /**
     * @return the cancellationException
     */
    public Exception getCancellationException() {
        return cancellationException;
    }
    /**
    * {@inheritDoc}
    */
    @Override
    public synchronized Integer get() throws InterruptedException, ExecutionException {
        //we can only get the results from the grid once
        //so let's cache the return code.
        if(returnCode ==null && returnedException==null){
            try{
                returnCode=super.get();
            }catch(ExecutionException e){
                returnedException = e;
            }
        }
        if(returnedException!=null){
            throw returnedException;
        }
        return returnCode;
    }
    
    

}

