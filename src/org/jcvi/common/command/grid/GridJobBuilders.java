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

import java.io.File;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.ExitTimeoutException;
import org.ggf.drmaa.JobInfo;
import org.ggf.drmaa.JobTemplate;
import org.ggf.drmaa.Session;
import org.jcvi.common.command.Command;
import org.jcvi.common.core.util.MapValueComparator;

/**
 * @author dkatzel
 *
 *
 */
public final class GridJobBuilders {

    public static GridJobBuilder<SimpleGridJob> createSimpleGridJobBuilder(Session gridSession, Command command, String projectCode){
        return new SimpleGridJobImpl.SimpleGridJobBuilder(gridSession, command, projectCode);
    }
    
    public static ArrayGridJobBuilder createArrayGridJobBuilder(Session gridSession, Command command, String projectCode){
        return new ArrayGridJobImpl.ArrayGridJobImplBuilder(gridSession, command, projectCode);
    }
    
    /**
     * A <code>GridJobImpl</code> is an abstractions for a DRMAA-supported distributed execution
     * process.
     *
     * @author jsitz@jcvi.org
     * @author dkatzel
     */
     private static abstract class AbstractGridJob implements GridJob {

        protected enum NativeSpec
        {
            BINARY_MODE,
            QUEUE,
            ARCHITECTURE,
            PROJECT_CODE,
            MEMORY,
            CPUS,
            MAX_RUNNING_TASKS
        }

        protected Session gridSession;
        protected Command command;

        protected Map<NativeSpec, String> nativeSpecs;
        protected Set<String> otherNativeSpecs;
        protected Properties env;
        protected Set<String> emailRecipients;
        protected String jobName;
        protected File workingDirectory;
        protected File inputFile;
        protected File outputFile;
        protected File errorFile;

        protected long timeout;

        protected JobTemplate jobTemplate;

        protected List<String> jobIDList=Collections.emptyList();
        protected Map<String,JobInfo> jobInfoMap = new ConcurrentHashMap<String, JobInfo>();

        private final PostExecutionHook postExecutionHook;
        private final PreExecutionHook preExecutionHook;
        
        protected boolean waiting = false;

        protected AbstractGridJob(Session gridSession,
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
                              PreExecutionHook preExecutionHook,
                              PostExecutionHook postExecutionHook) {
            super();

            this.gridSession = gridSession;
            this.command = command;
            this.nativeSpecs = nativeSpecs;
            this.otherNativeSpecs = otherNativeSpecs;
            this.env = env;
            this.emailRecipients = emailRecipients;
            this.jobName = jobName;
            this.workingDirectory = workingDirectory;
            this.inputFile = inputFile;
            this.outputFile = outputFile;
            this.errorFile = errorFile;
            this.timeout = timeout;
            this.preExecutionHook = preExecutionHook;
            this.postExecutionHook = postExecutionHook;
        }

        public AbstractGridJob(AbstractGridJob copy) throws DrmaaException
        {
            super();

            this.gridSession = copy.gridSession;
            this.jobTemplate = this.gridSession.createJobTemplate();
            this.command = copy.command;
            this.nativeSpecs = copy.nativeSpecs;
            this.otherNativeSpecs = copy.otherNativeSpecs;
            this.env = copy.env;
            this.emailRecipients = copy.emailRecipients;
            this.jobName = copy.jobName;
            this.workingDirectory = copy.workingDirectory;
            this.inputFile = copy.inputFile;
            this.outputFile = copy.outputFile;
            this.errorFile = copy.errorFile;
            this.timeout = copy.timeout;
            this.postExecutionHook = copy.postExecutionHook;
            this.preExecutionHook = copy.preExecutionHook;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#finalize()
         */
        @Override
        protected void finalize() throws Throwable
        {
            //force job template to be released
            //incase it hasn't already
            //this is to try to avoid a memory leak of the underlying
            //C drmma library and properly cleanup itself.
            this.releaseJobTemplate();
            super.finalize();
        }

        /**
         * @return A <code>Command</code>.
         */
        @Override
        public Command getCommand()
        {
            return this.command;
        }


        protected int preExecution()
        {
            // Do nothing by default.
            return 0;
        }

        protected int postScheduling()
        {
            // Do nothing by default
            return 0;
        }

        protected Status postExecution() throws DrmaaException {
            for ( String jobID : jobIDList ) {
                Status status = GridUtils.getJobStatus(jobInfoMap.get(jobID));
                if ( status != Status.COMPLETED ) {
                    return status;
                }
            }

            return Status.COMPLETED;
        }
        /* (non-Javadoc)
         * @see java.util.concurrent.Callable#call()
         */
        @Override
        public Integer call() throws Exception
        {
            /*
             * Call the pre-execution hook
             */
           
            int result = this.preExecution();

            if (result != 0){
                return result;
            }

            /*
             * Run the grid command and wait for it to complete.
             */
            this.runGridCommand();

            /*
             * Call the postScheduling hook
             */
            result = this.postScheduling();

            if (result != 0){
                return result;
            }
            
            this.waitForCompletion();

            /*
             * Call the post-execution hook
             */
            if(postExecutionHook ==null){
                return 0;
            }
            return callPostExecutionHook();
        }
        
        protected PostExecutionHook getPostExecutionHook() {
            return postExecutionHook;
        }

        protected PreExecutionHook getPreExecutionHook() {
            return preExecutionHook;
        }

        protected abstract int callPostExecutionHook() throws Exception;
        
        protected void cancelGridJobs(boolean jobTimeout) throws DrmaaException {
            for ( String jobID : jobIDList ) {
                int jobProgramStatus = this.gridSession.getJobProgramStatus(jobID);
                if ( jobProgramStatus != Session.DONE && jobProgramStatus != Session.FAILED ) {
                    this.gridSession.control(jobID, Session.TERMINATE);
                    if ( jobTimeout ) {
                        jobInfoMap.put(jobID,new JobInfoTimeout(jobID));
                    }
                }
            }
        }

        protected void updateGridJobStatusMap() throws DrmaaException {
            for ( String jobID : jobIDList ) {
                if ( !jobInfoMap.containsKey(jobID) ) {
                    jobInfoMap.put(jobID,this.gridSession.wait(jobID,1));
                }
            }
        }


        /**
         * builds the grid job template based on various fields and
         * user-selected options.  This includes building the native spec and setting
         * the job mode.
         *
         * @throws DrmaaException If there is an error while setting up the {@link JobTemplate}.
         */
        protected void buildJobTemplate() throws DrmaaException {
            this.jobTemplate = this.gridSession.createJobTemplate();
            this.jobTemplate.setRemoteCommand(this.command.getExecutablePath());
            this.jobTemplate.setArgs(this.command.getArguments());
            this.jobTemplate.setNativeSpecification(this.getNativeSpec());
            this.jobTemplate.setJobEnvironment(this.env);

            if (!this.emailRecipients.isEmpty()) {
                this.jobTemplate.setEmail(this.emailRecipients);
            }

            if ( jobName != null ) {
                jobTemplate.setJobName(jobName);
            }

            if ( workingDirectory != null ) {
                jobTemplate.setWorkingDirectory(workingDirectory.getAbsolutePath());
            }

            if ( inputFile != null ) {
                jobTemplate.setInputPath(getFileLocation(inputFile));
            }

            if ( outputFile != null ) {
                jobTemplate.setOutputPath(getFileLocation(outputFile));
            }

            if ( errorFile != null ) {
                jobTemplate.setErrorPath(getFileLocation(errorFile));
            }
        }

        protected String getNativeSpec()
        {
            final StringBuilder nativeSpec = new StringBuilder();

            for (final String spec : this.nativeSpecs.values())
            {
                if (nativeSpec.length() > 0)
                {
                    nativeSpec.append(' ');
                }
                nativeSpec.append(spec);
            }

            for (final String spec : this.otherNativeSpecs)
            {
                if (nativeSpec.length() > 0)
                {
                    nativeSpec.append(' ');
                }
                nativeSpec.append(spec);
            }

            return nativeSpec.toString();
        }

        private String getFileLocation(File file) {
            return ":" + file.getAbsolutePath();
        }

        @Override
        public abstract void runGridCommand() throws DrmaaException;

        @Override
        public List<String> getJobIDList() {
            return jobIDList;
        }

        @Override
        public Map<String, JobInfo> getJobInfoMap() {
            return MapValueComparator.sortAscending(jobInfoMap,JobInfoStatusComparator.INSTANCE );
        }

        public abstract void waitForCompletion() throws DrmaaException;

        protected void releaseJobTemplate() throws DrmaaException
        {
            if (this.jobTemplate != null)
            {
                this.gridSession.deleteJobTemplate(this.jobTemplate);
                this.jobTemplate = null;
            }
        }

        @Override
        public void terminate() throws DrmaaException{
            cancelGridJobs(false);
            if ( !waiting ) {
                updateGridJobStatusMap();
            }
        }

        static abstract class AbstractBuilder<J extends GridJob> implements GridJobBuilder<J>{
            private static final int SECONDS_PER_MINUTE = 60;

            protected Session gridSession;
            protected Command command;

            protected Map<NativeSpec, String> nativeSpecs;
            protected Set<String> otherNativeSpecs;
            protected Properties env;
            protected Set<String> emailRecipients;
            protected long timeout = Session.TIMEOUT_WAIT_FOREVER;

            protected String jobName;
            protected File workingDirectory;
            protected File inputFile;
            protected File outputFile;
            protected File errorFile;
            protected PostExecutionHook postExecutionHook;
            protected PreExecutionHook preExecutionHook;


            public  AbstractBuilder(Session gridSession, Command command, String projectCode) {
                this.gridSession = gridSession;
                this.command = command;

                this.nativeSpecs = new EnumMap<NativeSpec, String>(NativeSpec.class);
                this.otherNativeSpecs = new HashSet<String>();
                this.env = new Properties();
                this.emailRecipients = new HashSet<String>();

                this.setProjectCode(projectCode);
                this.setBinaryMode(true);
                this.setWorkingDirectory(new File(System.getProperty("user.dir")));
            }

            /**
            * {@inheritDoc}
            */
            @Override
            public GridJobBuilder<J> preExecutionHook(PreExecutionHook preExecutionHook){
                this.preExecutionHook = preExecutionHook;
                return this;
            }
            /**
            * {@inheritDoc}
            */
            @Override
            public GridJobBuilder<J> postExecutionHook(PostExecutionHook postExecutionHook){
                this.postExecutionHook = postExecutionHook;
                return this;
            }
            /**
            * {@inheritDoc}
            */
            @Override
            public GridJobBuilder<J> setBinaryMode(boolean mode) {
                this.setNativeSpec(NativeSpec.BINARY_MODE, (mode) ? "-b y" : "-b n");
                return this;
            }

            /**
            * {@inheritDoc}
            */
            @Override
            public GridJobBuilder<J> setProjectCode(String code) {
                if (code == null) {
                    this.clearNativeSpec(NativeSpec.PROJECT_CODE);
                } else {
                    this.setNativeSpec(NativeSpec.PROJECT_CODE, "-P " + code);
                }
                return this;
            }

            /**
            * {@inheritDoc}
            */
            @Override
            public GridJobBuilder<J> setQueue(String queueName) {
                if (queueName == null) {
                    this.clearNativeSpec(NativeSpec.QUEUE);
                } else {
                    this.setNativeSpec(NativeSpec.QUEUE, "-l " + queueName);
                }
                return this;
            }

            /**
            * {@inheritDoc}
            */
            @Override
            public GridJobBuilder<J> setMemory(Integer size, MemoryUnit unit) {
                if (size == null || size<1 ) {
                    this.clearNativeSpec(NativeSpec.MEMORY);
                } else {
                    if(unit ==null){
                        throw new NullPointerException("unit can not be null");
                    }
                    this.setNativeSpec(NativeSpec.MEMORY, "-l memory='" + size + unit.getGridCode()+"'");
                }
                return this;
            }

            /**
            * {@inheritDoc}
            */
            @Override
            public GridJobBuilder<J> setMinCPUs(Integer minimumCPUs) {
                if (minimumCPUs == null) {
                    this.clearNativeSpec(NativeSpec.CPUS);
                } else {
                    this.setNativeSpec(NativeSpec.CPUS, "-pe threaded " + minimumCPUs );
                }
                return this;
            }

            /**
            * {@inheritDoc}
            */
            @Override
            public GridJobBuilder<J> setArchitecture(String arch) {
                if (arch == null) {
                    this.clearNativeSpec(NativeSpec.ARCHITECTURE);
                } else {
                    this.setNativeSpec(NativeSpec.ARCHITECTURE, "-l arch='" + arch + "'");
                }
                return this;
            }

            /**
            * {@inheritDoc}
            */
            @Override
            public GridJobBuilder<J> addEmailRecipient(String emailAddr) {
                this.emailRecipients.add(emailAddr);
                return this;
            }

            /**
            * {@inheritDoc}
            */
            @Override
            public GridJobBuilder<J> clearEmailRecipients() {
                this.emailRecipients.clear();
                return this;
            }

            /**
            * {@inheritDoc}
            */
            @Override
            public GridJobBuilder<J> setTimeout(Long seconds) {
                if(seconds ==null){
                    this.timeout = Session.TIMEOUT_WAIT_FOREVER;
                }else if(seconds <0){
                   throw new IllegalArgumentException("timeout cannot be negative");
                }else{
                    this.timeout = seconds;
                }
                return this;
            }

            /**
            * {@inheritDoc}
            */
            @Override
            public GridJobBuilder<J> setTimeoutMinutes(long minutes) {
                this.setTimeout(minutes * SECONDS_PER_MINUTE);
                return this;
            }

            /**
            * {@inheritDoc}
            */
            @Override
            public GridJobBuilder<J> copyCurrentEnvironment() {
                this.env.putAll(System.getenv());
                return this;
            }

            /**
            * {@inheritDoc}
            */
            @Override
            public GridJobBuilder<J> copyCurrentEnvironment(String var) {
                this.setEnvironment(var, System.getenv(var));
                return this;
            }

            /**
            * {@inheritDoc}
            */
            @Override
            public GridJobBuilder<J> setEnvironment(String var, String val) {
                if (val == null) {
                    this.env.remove(var);
                }
                else {
                    this.env.setProperty(var, val);
                }
                return this;
            }

            /**
            * {@inheritDoc}
            */
            @Override
            public GridJobBuilder<J> setNativeSpec(String value)
            {
                if (value != null){
                    this.otherNativeSpecs.add(value);
                }
                return this;
            }

            /**
            * {@inheritDoc}
            */
            @Override
            public GridJobBuilder<J> clearNativeSpec(String miscSpec)
            {
                this.otherNativeSpecs.remove(miscSpec);
                return this;
            }

            /**
            * {@inheritDoc}
            */
            @Override
            public GridJobBuilder<J> setName(String jobName)
            {
                this.jobName = jobName;
                return this;
            }

            /**
            * {@inheritDoc}
            */
            @Override
            public GridJobBuilder<J> setWorkingDirectory(File workingDirectory)
            {
                this.workingDirectory = workingDirectory;
                return this;
            }

            /**
            * {@inheritDoc}
            */
            @Override
            public GridJobBuilder<J> setInputFile(File inputFile)
            {
                this.inputFile = inputFile;
                return this;
            }

            /**
            * {@inheritDoc}
            */
            @Override
            public GridJobBuilder<J> setOutputFile(File outputFile)
            {
                this.outputFile = outputFile;
                return this;
            }

            /**
            * {@inheritDoc}
            */
            @Override
            public GridJobBuilder<J> setErrorFile(File errorFile)
            {
                this.errorFile = errorFile;
                return this;
            }

            protected GridJobBuilder<J> setNativeSpec(NativeSpec spec, String value) {
                this.nativeSpecs.put(spec,value);
                return this;
            }

            protected GridJobBuilder<J> clearNativeSpec(NativeSpec spec) {
                this.nativeSpecs.remove(spec);
                return this;
            }
        }
     }
     
     /**
      * A <code>GridJobImpl</code> is an abstractions for a DRMAA-supported distributed execution
      * process.
      *
      * @author jsitz@jcvi.org
      * @author dkatzel
      */
     private static class SimpleGridJobImpl extends AbstractGridJob implements SimpleGridJob {

         protected SimpleGridJobImpl(Session gridSession,
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
         }

         /**
          * Creates a new <code>GridJobImpl</code>.
          */
         public SimpleGridJobImpl(SimpleGridJobImpl copy) throws DrmaaException
         {
             super(copy);
         }

         /*
             BatchGridJob interface methods
          */
         @Override
         public String getJobID()
         {
             String jobID = null;
             if (jobIDList!=null && !jobIDList.isEmpty() ) {
                 jobID = this.jobIDList.get(0);
             }
             return jobID;
         }

         @Override
         public String toString() {
             return "BatchGridJobImpl [getJobID()=" + getJobID() + "]";
         }

         @Override
         public JobInfo getJobInfo()
         {
             JobInfo jobInfo = null;
             if ( jobInfoMap != null && !jobInfoMap.isEmpty()) {
                 jobInfo = jobInfoMap.get(getJobID());
             }
             return jobInfo;
         }

         @Override
         protected int callPostExecutionHook() throws Exception {
             JobInfo jobInfo = getJobInfo();
             if(this.getPostExecutionHook()==null){
                 return jobInfo.getExitStatus();
             }
             return this.getPostExecutionHook().execute(getJobInfoMap());
         }

         @Override
         public void runGridCommand() throws DrmaaException
         {
             try
             {
                 this.buildJobTemplate();
                 this.jobIDList = Collections.singletonList(this.gridSession.runJob(this.jobTemplate));
             }
             finally
             {
                 this.releaseJobTemplate();
             }
         }

         @Override
         public void waitForCompletion() throws DrmaaException
         {
             waiting = true;
             try {
                 JobInfo jobInfo = this.gridSession.wait(getJobID(), this.timeout);
                 jobInfoMap.put(getJobID(),jobInfo);
             } catch (ExitTimeoutException e) {
                 cancelGridJobs(true);
             } finally {
                 updateGridJobStatusMap();
                 waiting = false;
             }
         }

         private static class SimpleGridJobBuilder extends AbstractBuilder<SimpleGridJob>{
             public SimpleGridJobBuilder(Session gridSession, Command command, String projectCode) {
                 super(gridSession, command, projectCode);
             }

             @Override
             public SimpleGridJob build() {
                 return new SimpleGridJobImpl(gridSession,
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
                                             postExecutionHook
                                             );
             }
         }
     }
     
     static class ArrayGridJobImpl extends AbstractGridJob {

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

         static class ArrayGridJobImplBuilder extends AbstractBuilder<GridJob> implements ArrayGridJobBuilder{

             private int bulkJobStartLoopIndex = 1;
             private int bulkJobEndLoopIndex = 1;
             private int bulkJobLoopIncrement = 1;

             public ArrayGridJobImplBuilder(Session gridSession, Command command, String projectCode) {
                 super(gridSession, command, projectCode);
             }
             @Override
             public void setBulkJobStartLoopIndex(int bulkJobStartLoopIndex) {
                 this.bulkJobStartLoopIndex = bulkJobStartLoopIndex;
             }
             @Override
             public void setBulkJobEndLoopIndex(int bulkJobEndLoopIndex) {
                 this.bulkJobEndLoopIndex = bulkJobEndLoopIndex;
             }
             @Override
             public void setBulkJobLoopIncrement(int bulkJobLoopIncrement) {
                 this.bulkJobLoopIncrement = bulkJobLoopIncrement;
             }
             @Override
             public ArrayGridJobImplBuilder setMaxRunningTasks(Integer maxRunningTasks) {
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
}
