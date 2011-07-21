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

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.ggf.drmaa.DrmaaException;
/**
 * {@code GridJobFuture} is a {@link Future}
 * implementation specifically for {@link AbstractGridJob}s
 * so you can access GridJob specific fields
 * @author dkatzel
 *
 *
 */
public class GridJobFuture extends FutureTask<Integer>{

    private final GridJob job;
    public GridJobFuture(GridJob gridJob) {
        super(gridJob);
        this.job = gridJob;
    }
    public GridJob getJob() {
        return job;
    }
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
       
        try {
            job.terminate();
            return true;
        } catch (DrmaaException e) {
            return false;
        }finally{
            super.cancel(mayInterruptIfRunning);
        }
    }
    
    

}

