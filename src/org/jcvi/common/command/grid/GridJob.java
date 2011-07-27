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
import org.ggf.drmaa.JobInfo;
import org.jcvi.common.command.Command;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by IntelliJ IDEA.
 * User: aresnick
 * Date: Aug 9, 2010
 * Time: 5:07:52 PM
 * To change this template use File | Settings | File Templates.
 */
public interface GridJob extends Callable<Integer> {

    public enum Status{
        UNKNOWN,        
        SIGNALLED,
        TIMED_OUT,
        ABORTED,
        COMPLETED,
    }

    public enum MemoryUnit{
        MB("M"),
        GB("G");

        private final String gridCode;
        private MemoryUnit(String code){
            this.gridCode = code;
        }
        public String getGridCode() {
            return gridCode;
        }


    }

    Command getCommand();

    List<String> getJobIDList();
    /**
     * Map is sorted by JobInfo status.
     * @return
     */
    Map<String,JobInfo> getJobInfoMap();

    void waitForCompletion() throws DrmaaException;

    void runGridCommand() throws DrmaaException;

    @Override
    Integer call() throws Exception;

    void terminate() throws DrmaaException;
}