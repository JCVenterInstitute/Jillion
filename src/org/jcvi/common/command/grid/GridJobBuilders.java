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
import java.util.LinkedHashSet;
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
 * {@code GridJobBuilders} is a factory class
 * that create {@link GridJobBuilder} instances for various
 * types of {@link GridJob}s.
 * @author dkatzel
 */
public final class GridJobBuilders {
    /**
     * Create a new GridJobBuilder instance that will make a {@link SimpleGridJob}.
     * @param gridSession the Session to run this job with.
     * @param command the Command to be run on the grid
     * @param projectCode the project code that this job will be charged to.
     * @return a new GridJobBuilder instance, never null.
     */
    public static GridJobBuilder<SimpleGridJob> createSimpleGridJobBuilder(Session gridSession, Command command, String projectCode){
        return new SimpleGridJobImpl.SimpleGridJobBuilder(gridSession, command, projectCode);
    }
    /**
     * Create a new ArrayGridJobBuilder instance that will make a {@link SimpleGridJob}.
     * @param gridSession the Session to run this job with.
     * @param command the Command to be run on the grid
     * @param projectCode the project code that this job will be charged to.
     * @return a new GridJobBuilder instance, never null.
     */
    public static ArrayGridJobBuilder createArrayGridJobBuilder(Session gridSession, Command command, String projectCode){
        return new ArrayGridJobImpl.ArrayGridJobImplBuilder(gridSession, command, projectCode);
    }
    
    /**
     * {@code AbstractGridJob} is an abstract implementation of GridJob
     * that handles all the common functionality of setting up 
     * a Job to run on the grid.
     *
     * 
     * @author dkatzel
     * @author jsitz@jcvi.org
     */
     private static abstract class AbstractGridJob implements GridJob {
         /**
          * Native Spec options to potentially
          * alter which machines on the grid
          * can run this job.
          * @author dkatzel
          *
          *
          */
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

        private final Session gridSession;
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

        private List<String> jobIDList=Collections.emptyList();
        private Map<String,JobInfo> jobInfoMap = new ConcurrentHashMap<String, JobInfo>();

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

        public AbstractGridJob(AbstractGridJob copy)
        {
            super();

            this.gridSession = copy.gridSession;
            this.jobTemplate = copy.jobTemplate;
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

        /**
         * 
        * Forces job template to be released (if it 
        * hasn't already).
        * This is to try to avoid a memory leak of the underlying
        * C drmma library and properly cleanup itself.
         */
        @Override
        protected void finalize() throws Throwable
        {
           
            this.releaseJobTemplate(this.jobTemplate);
            super.finalize();
        }

        /**
         * @return the gridSession
         */
        public final Session getGridSession() {
            return gridSession;
        }

        /**
         * @return A <code>Command</code>.
         */
        @Override
        public final Command getCommand()
        {
            return this.command;
        }


        protected int preExecution()
        {
            // Do nothing by default.
            return 0;
        }

        @SuppressWarnings("unused")
        protected int postScheduling()
        {
            // Do nothing by default
            return 0;
        }

        @Override
        public final Integer call() throws Exception
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
            
            this.waitForCompletion();

            if(postExecutionHook ==null){
                return 0;
            }
            return callPostExecutionHook(postExecutionHook);
        }
        /**
         * Call the given {@link PostExecutionHook} after a Job has 
         * completed successfully.
         * @param postExecutionHook the hook to call, will never be null.
         * @return the exit code from the postExecution hook.
         * @throws Exception if there is any Exception thrown by the callback.
         */
        protected abstract int callPostExecutionHook(PostExecutionHook postExecutionHook) throws Exception;
        
        private final synchronized void cancelGridJobs(boolean jobTimeout) throws DrmaaException {
            for ( String jobID : jobIDList ) {
                int jobProgramStatus = this.gridSession.getJobProgramStatus(jobID);
                if ( jobProgramStatus != Session.DONE && jobProgramStatus != Session.FAILED ) {
                    this.gridSession.control(jobID, Session.TERMINATE);
                    if ( jobTimeout ) {
                        jobInfoMap.put(jobID,new TimeoutJobInfo(jobID));
                    }
                }
            }
        }
        /**
         * Terminate all jobs currently submitted
         * to the grid that haven't yet completed or failed.
         * @throws DrmaaException
         */
        protected final void terminateAllGridJobs() throws DrmaaException{
            cancelGridJobs(false);
        }
        /**
         * Terminate all jobs currently submitted
         * to the grid that haven't yet completed or failed
         * and mark them as timed out.
         * @throws DrmaaException
         */
        protected final void timeOutAllGridJobs() throws DrmaaException{
            cancelGridJobs(true);
        }

        protected final void updateGridJobStatusMap() throws DrmaaException {
            for ( String jobID : jobIDList ) {
                if ( !jobInfoMap.containsKey(jobID) ) {
                   // gridSession.getJobProgramStatus(jobId)
                    jobInfoMap.put(jobID,this.gridSession.wait(jobID,1));
                    
                }
            }
        }
        /**
         * builds the grid job template based on various fields and
         * user-selected options.  This includes building the native spec and setting
         * the job mode.  Before returning, this method will delegate
         * to {@link #includeImplementationSpecificSettings(JobTemplate)}
         * to any implementation specific settings (if any)
         *
         * @throws DrmaaException If there is an error while setting up the {@link JobTemplate}.
         */
        private final JobTemplate createJobTemplate() throws DrmaaException{
            JobTemplate jobTemplate = this.gridSession.createJobTemplate();
            jobTemplate.setRemoteCommand(this.command.getExecutablePath());
            jobTemplate.setArgs(this.command.getArguments());
            jobTemplate.setNativeSpecification(this.getNativeSpec());
            jobTemplate.setJobEnvironment(this.env);

            if (!this.emailRecipients.isEmpty()) {
                jobTemplate.setEmail(this.emailRecipients);
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
            
            return includeImplementationSpecificSettings(jobTemplate);
        }
        /**
         * Some implementations might require
         * additional settings made to the JobTemplate.  Add those
         * additional settings to the given template then return it back.
         * @param template the current JobTemplate which may 
         * need implementation specific settings set.
         * @return a JobTemplate (possibly the same as the instance
         * passed in) never null.
         * @throws DrmaaException if there are any problems.
         */
        protected JobTemplate includeImplementationSpecificSettings(JobTemplate template) throws DrmaaException{
            return template;
        }
        
        /**
         * Convert map of NativeSpec objects
         * into a String of arguments.
         * @return a String representation of the native spec
         * into extra command parameters which the grid can interpret.
         */
        private String getNativeSpec(){
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
        public final void runGridCommand() throws DrmaaException{            
            try{
                this.jobTemplate =createJobTemplate();
                this.jobIDList = runTemplate(this.jobTemplate);
            }finally{
                this.releaseJobTemplate(this.jobTemplate);
            }
        }
        
        public abstract List<String> runTemplate(JobTemplate template) throws DrmaaException;

        @Override
        public final List<String> getJobIDList() {
            return jobIDList;
        }

        @Override
        public Map<String, JobInfo> getJobInfoMap() {
            return MapValueComparator.sortAscending(jobInfoMap,JobInfoStatusComparator.INSTANCE );
        }

        public abstract void waitForCompletion() throws DrmaaException;

        private void releaseJobTemplate(JobTemplate template) throws DrmaaException{
            if (template != null){
                this.gridSession.deleteJobTemplate(template);                
            }
        }

        @Override
        public final void terminate() throws DrmaaException{
            terminateAllGridJobs();
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
                this.otherNativeSpecs = new LinkedHashSet<String>();
                this.env = new Properties();
                this.emailRecipients = new LinkedHashSet<String>();

                this.setProjectCode(projectCode);
                this.setBinaryMode(true);
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
         public SimpleGridJobImpl(SimpleGridJobImpl copy)
         {
             super(copy);
         }

   
         @Override
         public String getJobID(){
             List<String> list = getJobIDList();
             if (list!=null && !list.isEmpty() ) {
                 return list.get(0);
             }
             return null;
         }

         @Override
         public String toString() {
             return "SimpleGridJob [getJobID()=" + getJobID() + "]";
         }

         @Override
         public JobInfo getJobInfo()
         {
             String jobId = getJobID();
             if(jobId ==null){
                 return null;
             }
             Map<String,JobInfo> map =getJobInfoMap();
             if ( map != null && !map.containsKey(jobId)) {
                 return map.get(jobId);
             }
             return null;
         }

         @Override
         protected int callPostExecutionHook(PostExecutionHook postExecutionHook) throws Exception {
             return postExecutionHook.execute(getJobInfoMap());
         }
         /**
        * {@inheritDoc}
        */
        @Override
        public List<String> runTemplate(JobTemplate template)
                throws DrmaaException {
            return Collections.singletonList(getGridSession().runJob(template));
        }

        @Override
         public void waitForCompletion() throws DrmaaException
         {
             waiting = true;
             try {
                 getGridSession().synchronize(Collections.singletonList(getJobID()), this.timeout,false);
                // JobInfo jobInfo = ;
                // getJobInfoMap().put(getJobID(),jobInfo);
             } catch (ExitTimeoutException e) {
                 timeOutAllGridJobs();
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
     /**
      * {@code ArrayGridJobImpl} is a GridJob that
      * runs multiple times with slightly different parameters 
      * as if it were in a loop.
      * @author dkatzel
      *
      *
      */
     private static class ArrayGridJobImpl extends AbstractGridJob {

         private final int bulkJobStartLoopIndex;
         private final int bulkJobEndLoopIndex;
         private final int bulkJobLoopIncrement;

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

         public ArrayGridJobImpl(ArrayGridJobImpl copy){
             super(copy);
             this.bulkJobStartLoopIndex = copy.bulkJobStartLoopIndex;
             this.bulkJobEndLoopIndex = copy.bulkJobEndLoopIndex;
             this.bulkJobLoopIncrement = copy.bulkJobLoopIncrement;
         }

         /*
             BatchGridJob interface methods
          */
         
         @Override
         protected int callPostExecutionHook(PostExecutionHook postExecutionHook) throws Exception {
             
             return postExecutionHook.execute(getJobInfoMap());
         }

         /**
        * Adds Array parameter index to any file paths that have been set.
         * @throws DrmaaException 
        */
        @Override
        protected JobTemplate includeImplementationSpecificSettings(
                JobTemplate template) throws DrmaaException {
            if(template.getInputPath() !=null){
                template.setInputPath(template.getInputPath()+"."+ JobTemplate.PARAMETRIC_INDEX);                
            }
            if(template.getOutputPath() !=null){
                template.setOutputPath(template.getOutputPath()+"."+ JobTemplate.PARAMETRIC_INDEX);                
            }
            if(template.getOutputPath() !=null){
                template.setErrorPath(template.getErrorPath()+"."+ JobTemplate.PARAMETRIC_INDEX);                
            }
           
            return template;
        }  

         /**
        * {@inheritDoc}
        */
        @Override
        public List<String> runTemplate(JobTemplate template) throws DrmaaException {
            return getGridSession().runBulkJobs(template,
                    this.bulkJobStartLoopIndex,
                    this.bulkJobEndLoopIndex,
                    this.bulkJobLoopIncrement);
            
        }

        @Override
         public void waitForCompletion() throws DrmaaException {
             waiting = true;
             try {
                 //dispose flag set to false so we can get
                 //jobinfo in updateGridJobStatusMap()
                 getGridSession().synchronize(getJobIDList(), this.timeout, false);
             } catch (ExitTimeoutException e) {
                 timeOutAllGridJobs();
             } finally {
                 updateGridJobStatusMap();
                 waiting = false;
             }
         }

         private static class ArrayGridJobImplBuilder extends AbstractBuilder<GridJob> implements ArrayGridJobBuilder{

             private int bulkJobStartLoopIndex = 1;
             private int bulkJobEndLoopIndex = 1;
             private int bulkJobLoopIncrement = 1;

             public ArrayGridJobImplBuilder(Session gridSession, Command command, String projectCode) {
                 super(gridSession, command, projectCode);
             }
             @Override
             public void setBulkJobLoop(int start, int end){
                 setBulkJobLoop(start, end, 1);
             }
             @Override
             public void setBulkJobLoop(int start, int end, int increment){
                 this.bulkJobStartLoopIndex = start;
                 this.bulkJobEndLoopIndex = end;
                 this.bulkJobLoopIncrement = increment;
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
