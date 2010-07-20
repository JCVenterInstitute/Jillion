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

package org.jcvi.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * {@code ExceptionIntolerantFixedSizedThreadPoolExecutor} 
 * is a {@link ThreadPoolExecutor} that has a fixed size number of
 * threads and will shutdown immediately and try to cancel
 * all currently running threads if any of the completed threads
 * threw a Throwable (errors out).
 * @author dkatzel
 *
 *
 */
public class ExceptionIntolerantFixedSizedThreadPoolExecutor extends ThreadPoolExecutor{
    public  ExceptionIntolerantFixedSizedThreadPoolExecutor(int numberOfThreads){
        super(numberOfThreads,numberOfThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
      
    }
    @Override
    protected synchronized void afterExecute(Runnable r, Throwable t) {
        if(t !=null){
            t.printStackTrace();
            this.shutdownNow();
            throw new RuntimeException(t);
        }
      
    }
}