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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.ggf.drmaa.Session;

/**
 * {@code GridJobExecutorService} is an {@link ExecutorService}
 * that submits and manages {@link AbstractGridJob}s.
 * @author dkatzel
 * @author jsitz@jcvi.org
 */
public class GridJobExecutorService extends ThreadPoolExecutor
{

    private final Session gridSession;
    private final String name;
    private final Map<GridJob,GridJobFuture> futures = new HashMap<GridJob,GridJobFuture>();

    /**
     * Creates a new <code>GridPoolExecutor</code>.
     */
    public GridJobExecutorService(String name, int maxConcurrency)
    {
       this(GridUtils.getGlobalSession(), name, maxConcurrency);
    }
    public GridJobExecutorService(Session session,String name, int maxConcurrency)
    {
        super(maxConcurrency, maxConcurrency, 2L, TimeUnit.MINUTES, new LinkedBlockingDeque<Runnable>());

        this.name = name;
        this.gridSession = session;
    }

    public Session getSession()
    {
        return this.gridSession;
    }

    public final String getName()
    {
        return this.name;
    }


    public int countActiveTasks()
    {
        int stillRunning = 0;
        for(Entry<GridJob,GridJobFuture> entry : futures.entrySet()){
            GridJobFuture future = entry.getValue();
            if( !future.isDone() && !future.isCancelled()){
                stillRunning++;
            }
        }
        return stillRunning;
    }
   

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.getClass().getSimpleName() + ":" + this.getName();
    }
    protected GridJobFuture newTaskFor(GridJob gridJob) {
        return new GridJobFuture(gridJob);
    }
    /**
     * Special implementation logic for handling new Tasks for 
     * instances of {@link GridJob}.  We need to keep track
     * of GridJobs in case we need to cancel one.
     */
    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        if(callable instanceof GridJob){
            GridJob gridJob = (GridJob)callable;
            final GridJobFuture gridJobTask = newTaskFor(gridJob);
            futures.put(gridJob,gridJobTask);
            return (RunnableFuture<T>)gridJobTask;
        }
        return super.newTaskFor(callable);
    }
    
    public GridJobFuture submit(GridJob gridJob) {
        return(GridJobFuture) submit((Callable<Integer>) gridJob);
    }
   
    /**
     * 
    * {@inheritDoc}
    * <p/>
    * Cancel's any currently running job using
    * {@link GridJobFuture#cancel(boolean)}
    * and stops any new queued jobs from ever running.
     */
    @Override
    public synchronized List<Runnable> shutdownNow() {
       
        //must force cancel all non-complete jobs
        //normal Executor shutdown uses
        //interrupt which relies on implementation
        //checking interrupt flag
        //and won't work if drmma is blocking on completion
        
        //copy of list to iterate over so we don't get 
        //concurrent modification errors when we remove items
       
        Map<GridJob,GridJobFuture> jobsToCancel = new HashMap<GridJob, GridJobFuture>(futures);

       for(Entry<GridJob,GridJobFuture> entry : jobsToCancel.entrySet()){
                GridJobFuture future = entry.getValue();
                future.cancel(true);
        }
        futures.clear();
        return  super.shutdownNow();
    }
   
  
    
    
}
