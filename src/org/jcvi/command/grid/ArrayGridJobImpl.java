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
import org.ggf.drmaa.ExitTimeoutException;
import org.ggf.drmaa.JobInfo;
import org.ggf.drmaa.JobTemplate;
import org.ggf.drmaa.Session;
import org.jcvi.command.Command;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
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
public class ArrayGridJobImpl extends AbstractGridJob {

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
                            int bulkJobLoopIncrement,
                            PreExecutionHook preExecutionHook,
                            PostExecutionHook postExecutionHook) {
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
              timeout,
              preExecutionHook,
              postExecutionHook);
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
    protected int callPostExecutionHook() throws Exception {
        if(this.getPostExecutionHook()==null){
            return 0;
        }
        return this.getPostExecutionHook().execute(getJobInfoMap());
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
            this.jobInfoMap = Collections.synchronizedMap(new HashMap<String,JobInfo>());
        }
        finally
        {
            this.releaseJobTemplate();
        }

    }

    @Override
    public void waitForCompletion() throws DrmaaException {
        waiting = true;
        try {
            this.gridSession.synchronize(jobIDList, this.timeout, false);
        } catch (ExitTimeoutException e) {
            cancelGridJobs(true);
        } finally {
            updateGridJobStatusMap();
            waiting = false;
        }
    }

    public static class Builder extends AbstractGridJob.Builder<ArrayGridJobImpl>{

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

        public Builder setMaxRunningTasks(Integer maxRunningTasks) {
            if (maxRunningTasks == null) {
                this.clearNativeSpec(NativeSpec.MAX_RUNNING_TASKS);
            } else {
                this.setNativeSpec(NativeSpec.MAX_RUNNING_TASKS, "-tc " + maxRunningTasks);
            }
            return this;
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
                                        bulkJobLoopIncrement,
                                        preExecutionHook,
                                        postExecutionHook);
        }
    }
}
