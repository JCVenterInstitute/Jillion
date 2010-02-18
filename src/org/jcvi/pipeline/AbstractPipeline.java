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
