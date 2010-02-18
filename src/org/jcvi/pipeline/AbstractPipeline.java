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
/*
 * Created on Feb 10, 2009
 *
 * @author dkatzel
 */
package org.jcvi.pipeline;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPipeline implements Pipeline{

    private List<Process> preProcesses;
    private List<Process> postProcesses;
    private List<Process> processes;
    
    protected AbstractPipeline(){
        preProcesses = new ArrayList<Process>();
        processes = new ArrayList<Process>();
        postProcesses = new ArrayList<Process>();
    }
    @Override
    public void run() {
        setup();
        runProcesses(preProcesses);
        runProcesses(processes);
        runProcesses(postProcesses);
    }

    private void runProcesses(List<Process> processList) {
        for(Process process : processList){
            run(process);
        }
    }
    private void run(Process process) {
        //TODO error handling? 
        process.run();        
    }
    @Override
    public void setup() {
        preProcesses.addAll(createPreProcesses());
        processes.addAll(createProcesses());
        postProcesses.addAll(createPostProcesses());
    }

    protected abstract List<Process> createProcesses();

    protected abstract List<Process> createPreProcesses();
    protected abstract List<Process> createPostProcesses();

}
