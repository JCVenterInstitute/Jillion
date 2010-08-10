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

package org.jcvi.command.grid;

import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.JobInfo;
import org.ggf.drmaa.Session;
import org.ggf.drmaa.JobTemplate;
import org.jcvi.command.Command;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: aresnick
 * Date: Aug 6, 2010
 * Time: 3:51:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArrayGridJobImpl extends GridJobImpl {

    protected int bulkJobStartLoopIndex;
    protected int bulkJobEndLoopIndex;
    protected int bulkJobLoopIncrement;

    public ArrayGridJobImpl(Session gridSession,
                            Command command,
                            Map<NativeSpec, String> nativeSpecs,
                            Set<String> otherNativeSpecs,
                            Properties env,
                            Set<String> emailRecipients,
                            String jobName,
                            File workingDirectory,
                            File inputFile,
                            File outputFile,
                            File errorFile,
                            long timeout,
                            int bulkJobStartLoopIndex,
                            int bulkJobEndLoopIndex,
                            int bulkJobLoopIncrement) {
        super(gridSession,
              command,
              nativeSpecs,
              otherNativeSpecs,
              env,
              emailRecipients,
              jobName,
              workingDirectory,
              inputFile,
              outputFile,
              errorFile,
              timeout);
        this.bulkJobStartLoopIndex = bulkJobStartLoopIndex;
        this.bulkJobEndLoopIndex = bulkJobEndLoopIndex;
        this.bulkJobLoopIncrement = bulkJobLoopIncrement;
    }

    public ArrayGridJobImpl(ArrayGridJobImpl copy) throws DrmaaException {
        super(copy);
        this.bulkJobStartLoopIndex = copy.bulkJobStartLoopIndex;
        this.bulkJobEndLoopIndex = copy.bulkJobEndLoopIndex;
        this.bulkJobLoopIncrement = copy.bulkJobLoopIncrement;
    }

    /*
        BatchGridJob interface methods
     */    
    @Override
    protected int postExecution() throws DrmaaException {
        for ( String jobID : jobIDList ) {
            JobInfo info = jobInfoMap.get(jobID);
            if ( info == null ) {
                return Session.FAILED;
            } else if ( info.getExitStatus() != Session.DONE ) {
                return info.getExitStatus();
            } else {
                // evaluate next job result
            }
        }

        return Session.DONE;
    }

    @Override
    protected void buildJobTemplate() throws DrmaaException {
        super.buildJobTemplate();
        this.jobTemplate.setInputPath(this.jobTemplate.getInputPath()+"."+ JobTemplate.PARAMETRIC_INDEX);
        this.jobTemplate.setOutputPath(this.jobTemplate.getOutputPath()+"."+ JobTemplate.PARAMETRIC_INDEX);
        this.jobTemplate.setErrorPath(this.jobTemplate.getErrorPath()+"."+ JobTemplate.PARAMETRIC_INDEX);
    }

    @Override
    public void runGridCommand() throws DrmaaException {
        try
        {
            this.buildJobTemplate();
            this.jobIDList = this.gridSession.runBulkJobs(this.jobTemplate,
                                                          this.bulkJobStartLoopIndex,
                                                          this.bulkJobEndLoopIndex,
                                                          this.bulkJobLoopIncrement);
            this.jobInfoMap = new Hashtable<String,JobInfo>();
        }
        finally
        {
            this.releaseJobTemplate();
        }

    }

    @Override
    public void waitForCompletion() throws DrmaaException {
        this.gridSession.synchronize(jobIDList, this.timeout, false);
        for ( String jobID : jobIDList ) {
            jobInfoMap.put(jobID,this.gridSession.wait(jobID,Session.TIMEOUT_NO_WAIT));
        }

        System.out.println("completed has exited");
    }

    @Override
    public void terminate() throws DrmaaException{
        for ( String jobID : jobIDList ) {
            if ( !jobInfoMap.containsKey(jobID) ) {
                this.gridSession.control(jobID, Session.TERMINATE);
                jobInfoMap.put(jobID,new StatusJobInfo(jobID, Session.TERMINATE));
            }
        }
    }

    @Override
    public GridException getGridTimeoutException() {
        return new GridException("One or more of this array grid job's grid jobs "
            + "failed to complete in the scheduled time.");
    }

    public static class Builder extends GridJobImpl.Builder implements org.jcvi.Builder<GridJob>{

        private int bulkJobStartLoopIndex = 1;
        private int bulkJobEndLoopIndex = 1;
        private int bulkJobLoopIncrement = 1;

        public Builder(Session gridSession, Command command, String projectCode) {
            super(gridSession, command, projectCode);
        }

        public void setBulkJobStartLoopIndex(int bulkJobStartLoopIndex) {
            this.bulkJobStartLoopIndex = bulkJobStartLoopIndex;
        }

        public void setBulkJobEndLoopIndex(int bulkJobEndLoopIndex) {
            this.bulkJobEndLoopIndex = bulkJobEndLoopIndex;
        }

        public void setBulkJobLoopIncrement(int bulkJobLoopIncrement) {
            this.bulkJobLoopIncrement = bulkJobLoopIncrement;
        }

        @Override
        public ArrayGridJobImpl build() {
            return new ArrayGridJobImpl(gridSession,
                                        command,
                                        nativeSpecs,
                                        otherNativeSpecs,
                                        env,
                                        emailRecipients,
                                        jobName,
                                        workingDirectory,
                                        inputFile,
                                        outputFile,
                                        errorFile,
                                        timeout,
                                        bulkJobStartLoopIndex,
                                        bulkJobEndLoopIndex,
                                        bulkJobLoopIncrement);
        }
    }
}
