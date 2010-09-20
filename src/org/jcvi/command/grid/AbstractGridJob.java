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


import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.ggf.drmaa.*;
import org.jcvi.command.Command;

/**
 * A <code>GridJobImpl</code> is an abstractions for a DRMAA-supported distributed execution
 * process.
 *
 * @author jsitz@jcvi.org
 * @author dkatzel
 */
 public abstract class AbstractGridJob implements GridJob {

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
    protected Map<String,JobInfo> jobInfoMap = Collections.emptyMap();

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

        if (result != 0) return result;

        /*
         * Run the grid command and wait for it to complete.
         */
        this.runGridCommand();

        /*
         * Call the postScheduling hook
         */
        result = this.postScheduling();

        if (result != 0) return result;
        
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

    abstract protected int callPostExecutionHook() throws Exception;
    
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
    abstract public void runGridCommand() throws DrmaaException;

    @Override
    public List<String> getJobIDList() {
        return jobIDList;
    }

    @Override
    public Map<String, JobInfo> getJobInfoMap() {
        return jobInfoMap;
    }

    abstract public void waitForCompletion() throws DrmaaException;

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

    public static abstract class Builder<J extends GridJob> implements org.jcvi.Builder<J>{
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


        public  Builder(Session gridSession, Command command, String projectCode) {
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

        public Builder<J> preExecutionHook(PreExecutionHook preExecutionHook){
            this.preExecutionHook = preExecutionHook;
            return this;
        }
        public Builder<J> postExecutionHook(PostExecutionHook postExecutionHook){
            this.postExecutionHook = postExecutionHook;
            return this;
        }
        public Builder setBinaryMode(boolean mode) {
            this.setNativeSpec(NativeSpec.BINARY_MODE, (mode) ? "-b y" : "-b n");
            return this;
        }

        public Builder<J> setProjectCode(String code) {
            if (code == null) {
                this.clearNativeSpec(NativeSpec.PROJECT_CODE);
            } else {
                this.setNativeSpec(NativeSpec.PROJECT_CODE, "-P " + code);
            }
            return this;
        }

        public Builder<J> setQueue(String queueName) {
            if (queueName == null) {
                this.clearNativeSpec(NativeSpec.QUEUE);
            } else {
                this.setNativeSpec(NativeSpec.QUEUE, "-l " + queueName);
            }
            return this;
        }

        public Builder<J> setMemory(Integer size, MemoryUnit unit) {
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

        public Builder<J> setMinCPUs(Integer minimumCPUs) {
            if (minimumCPUs == null) {
                this.clearNativeSpec(NativeSpec.CPUS);
            } else {
                this.setNativeSpec(NativeSpec.CPUS, "-pe threaded " + minimumCPUs );
            }
            return this;
        }

        public Builder<J> setArchitecture(String arch) {
            if (arch == null) {
                this.clearNativeSpec(NativeSpec.ARCHITECTURE);
            } else {
                this.setNativeSpec(NativeSpec.ARCHITECTURE, "-l arch='" + arch + "'");
            }
            return this;
        }

        public Builder<J> addEmailRecipient(String emailAddr) {
            this.emailRecipients.add(emailAddr);
            return this;
        }

        public Builder<J> clearEmailRecipients() {
            this.emailRecipients.clear();
            return this;
        }

        public Builder<J> setTimeout(Long seconds) {
            if(seconds ==null){
                this.timeout = Session.TIMEOUT_WAIT_FOREVER;
            }else if(seconds <0){
               throw new IllegalArgumentException("timeout cannot be negative");
            }else{
                this.timeout = seconds;
            }
            return this;
        }

        public Builder<J> setTimeoutMinutes(long minutes) {
            this.setTimeout(minutes * SECONDS_PER_MINUTE);
            return this;
        }

        public Builder<J> copyCurrentEnvironment() {
            this.env.putAll(System.getenv());
            return this;
        }

        public Builder<J> copyCurrentEnvironment(String var) {
            this.setEnvironment(var, System.getenv(var));
            return this;
        }

        public Builder<J> setEnvironment(String var, String val) {
            if (val == null) {
                this.env.remove(var);
            }
            else {
                this.env.setProperty(var, val);
            }
            return this;
        }

        public Builder<J> setNativeSpec(String value)
        {
            if (value != null){
                this.otherNativeSpecs.add(value);
            }
            return this;
        }

        public Builder<J> clearNativeSpec(String miscSpec)
        {
            this.otherNativeSpecs.remove(miscSpec);
            return this;
        }

        public Builder<J> setName(String jobName)
        {
            this.jobName = jobName;
            return this;
        }

        public Builder<J> setWorkingDirectory(File workingDirectory)
        {
            this.workingDirectory = workingDirectory;
            return this;
        }

        public Builder<J> setInputFile(File inputFile)
        {
            this.inputFile = inputFile;
            return this;
        }

        public Builder<J> setOutputFile(File outputFile)
        {
            this.outputFile = outputFile;
            return this;
        }

        public Builder<J> setErrorFile(File errorFile)
        {
            this.errorFile = errorFile;
            return this;
        }

        protected Builder<J> setNativeSpec(NativeSpec spec, String value) {
            this.nativeSpecs.put(spec,value);
            return this;
        }

        protected Builder<J> clearNativeSpec(NativeSpec spec) {
            this.nativeSpecs.remove(spec);
            return this;
        }
    }
}

