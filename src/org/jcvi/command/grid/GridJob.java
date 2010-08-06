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
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.ExitTimeoutException;
import org.ggf.drmaa.JobInfo;
import org.ggf.drmaa.JobTemplate;
import org.ggf.drmaa.Session;
import org.jcvi.command.Command;

/**
 * A <code>GridJob</code> is an abstractions for a DRMAA-supported distributed execution
 * process.
 *
 * @author jsitz@jcvi.org
 * @author dkatzel
 */
public class GridJob implements Callable<Integer>
{
    private static final int SECONDS_PER_MINUTE = 60;

    private enum NativeSpec
    {
        BINARY_MODE,
        QUEUE,
        ARCHITECTURE,
        PROJECT_CODE,
        MEMORY,
        CPUS;
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

    private final Command command;
    private JobTemplate jobTemplate;
    private String jobid;
    private final Session gridSession;
    private JobInfo gridInfo;

    private final Map<NativeSpec, String> nativeSpecs;
    private final Set<String> otherNativeSpecs;
    private final Properties env;
    private final Set<String> emailRecipients;

    private long timeout;

    /**
     * Creates a new <code>GridJob</code>.
     */
    public GridJob(Session session, Command remoteCommand, String projectCode) throws DrmaaException
    {
        super();

        this.gridSession = session;
        this.jobTemplate = this.gridSession.createJobTemplate();
        this.command = remoteCommand;
        this.nativeSpecs = new EnumMap<NativeSpec, String>(NativeSpec.class);
        this.otherNativeSpecs = new HashSet<String>();
        this.env = new Properties();
        this.emailRecipients = new HashSet<String>();

        this.setProjectCode(projectCode);
        this.setBinaryMode(true);

        this.timeout = TimeUnit.HOURS.toMillis(4);

        this.initJobTemplate();
    }


    public GridJob(GridJob copy) throws DrmaaException
    {
        super();

        this.gridSession = copy.gridSession;
        this.jobTemplate = this.gridSession.createJobTemplate();
        this.command = copy.command;
        this.nativeSpecs = copy.nativeSpecs;
        this.otherNativeSpecs = copy.otherNativeSpecs;
        this.env = copy.env;
        this.emailRecipients = copy.emailRecipients;

        this.initJobTemplate();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable
    {
        this.releaseJobTemplate();
        super.finalize();
    }

    public void setName(String jobName) throws DrmaaException
    {
        this.jobTemplate.setJobName(jobName);
    }

    /**
     * Select binary or script mode for this job. In binary mode, the command is
     * run as-is from the grid host.  In non-binary (script) mode, the command
     * path is copied first to the grid host and run from a local directory.
     * This is slightly faster, but if the command has path dependencies it may
     * cause the job to fail.
     *
     * @param mode <code>true</code> if binary mode is desired, <code>false</code>
     * if script mode is desired.
     */
    public void setBinaryMode(boolean mode)
    {
        this.setNativeSpec(NativeSpec.BINARY_MODE, (mode) ? "-b y" : "-b n");
    }

    public void setProjectCode(String code)
    {
        if (code == null)
        {
            this.clearNativeSpec(NativeSpec.PROJECT_CODE);
        }
        this.setNativeSpec(NativeSpec.PROJECT_CODE, "-P " + code);
    }

    public void setQueue(String queueName)
    {
        if (queueName == null)
        {
            this.clearNativeSpec(NativeSpec.QUEUE);
        }
        else
        {
            this.setNativeSpec(NativeSpec.QUEUE, "-l " + queueName);
        }
    }
    public void setMemory(Integer size, MemoryUnit unit)
    {
        if (size == null)
        {
            this.clearNativeSpec(NativeSpec.MEMORY);
        }
        else
        {
            if(unit ==null){
                throw new NullPointerException("unit can not be null");
            }
            if(size.intValue()<1){
                
            }
            this.setNativeSpec(NativeSpec.MEMORY, "-l memory='" + size + unit.getGridCode()+"'");
        }
    }
    public void setMinCPUs(Integer minimumCPUs)
    {
        if (minimumCPUs == null)
        {
            this.clearNativeSpec(NativeSpec.CPUS);
        }
        else
        {
            this.setNativeSpec(NativeSpec.CPUS, "-pe threaded " + minimumCPUs );
        }
    }
    public void setArchitecture(String arch)
    {
        if (arch == null)
        {
            this.clearNativeSpec(NativeSpec.ARCHITECTURE);
        }
        else
        {
            this.setNativeSpec(NativeSpec.ARCHITECTURE, "-l arch='" + arch + "'");
        }
    }

    public void setWorkingDirectory(File dir) throws DrmaaException
    {
        this.jobTemplate.setWorkingDirectory(dir.getAbsolutePath());
    }

    public void setOutputFile(File output) throws DrmaaException
    {
        this.jobTemplate.setOutputPath(":" + output.getAbsolutePath());
    }

    public void setErrorFile(File error) throws DrmaaException
    {
        this.jobTemplate.setErrorPath(":" + error.getAbsolutePath());
    }

    public void addEmailRecipient(String emailAddr)
    {
        this.emailRecipients.add(emailAddr);
    }

    public void clearEmailRecipients()
    {
        this.emailRecipients.clear();
    }

    public void setTimeout(long seconds)
    {
        this.timeout = seconds;
    }

    public void setTimeoutMinutes(long minutes)
    {
        this.setTimeout(minutes * GridJob.SECONDS_PER_MINUTE);
    }

    public void copyCurrentEnvironment()
    {
        this.env.putAll(System.getenv());
    }

    public void copyCurrentEnvironment(String var)
    {
        this.setEnvironment(var, System.getenv(var));
    }

    public void setEnvironment(String var, String val)
    {
        if (val == null)
        {
            this.env.remove(var);
        }
        else
        {
            this.env.setProperty(var, val);
        }
    }

    /**
     * @return A <code>Command</code>.
     */
    public Command getCommand()
    {
        return this.command;
    }

    public int preExecution()
    {
        // Do nothing by default.
        return 0;
    }

    public int postScheduling()
    {
        // Do nothing by default
        return 0;
    }

    public int postExecution(JobInfo runInfo, int jobStatus) throws DrmaaException
    {
        // Do nothing by default.
        return 0;
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public Integer call() throws Exception
    {
        int result = 0;

        /*
         * Call the pre-execution hook
         */
        result = this.preExecution();
        if (result != 0) return Integer.valueOf(result);

        /*
         * Run the grid command and wait for it to complete.
         */
        this.runGridCommand();

        /*
         * Call the postScheduling hook
         */
        result = this.postScheduling();
        if (result != 0) return Integer.valueOf(result);

        try
        {
            this.waitForCompletion();
        }
        catch (final ExitTimeoutException e)
        {
            // Terminate the job.
            this.gridSession.control(this.getJobID(), Session.TERMINATE);

            // Throw a better exception
            throw new GridException("The grid job (" + this.getJobID() + ") failed to complete in the scheduled time.");
        }

        /*
         * Call the post-execution hook
         */
        final int jobProgramStatus;
      /*  try{
        jobProgramStatus = this.gridSession.getJobProgramStatus(jobid);
        }catch (Throwable t){
            t.printStackTrace();
            throw new RuntimeException(t);
        }
        */
        result = this.postExecution(this.gridInfo, 0);

        return Integer.valueOf(result);
    }

    /**
     * Performs initial setup on the grid job template.  This should be called
     * during object construction.
     *
     * @throws DrmaaException If there is an error while setting up the {@link JobTemplate}.
     */
    protected void initJobTemplate() throws DrmaaException
    {
        this.jobTemplate.setRemoteCommand(this.command.getExecutablePath());
        this.jobTemplate.setArgs(this.command.getArguments());

        this.copyCurrentEnvironment("PATH");
        this.copyCurrentEnvironment("ELVIRA_ETC");
        this.copyCurrentEnvironment("LD_LIBRARY_PATH");
    }

    /**
     * Finishes setting up the the grid job template based on various fields and
     * user-selected options.  This includes building the native spec and setting
     * the job mode.
     *
     * @throws DrmaaException If there is an error while setting up the {@link JobTemplate}.
     */
    protected void finishJobTemplate() throws DrmaaException
    {
        this.jobTemplate.setNativeSpecification(this.getNativeSpec());
        this.jobTemplate.setJobEnvironment(this.env);

        if (!this.emailRecipients.isEmpty())
        {
            this.jobTemplate.setEmail(this.emailRecipients);
        }
    }

    /**
     * Set one of the keyed native job specs.  This will set and overwrite any
     * existing value in the given keyed spec.  This is used for setting things
     * such as the binary mode of the job, queue restrictions and the project code.
     *
     * @param spec The keyed spec to set.
     * @param value The value to set it to.
     */
    protected void setNativeSpec(NativeSpec spec, String value)
    {
        if (value == null)
        {
            this.nativeSpecs.remove(spec);
        }
        else
        {
            this.nativeSpecs.put(spec, value);
        }
    }

    /**
     * Set a non-keyed job spec.  This will add the value to a free-form list
     * of specs which will be added to the job when it is started.
     *
     * @param value
     */
    protected void setNativeSpec(String value)
    {
        if (value == null) return;
        this.otherNativeSpecs.add(value);
    }

    protected void clearNativeSpec(NativeSpec spec)
    {
        this.nativeSpecs.remove(spec);
    }

    protected void clearNativeSpec(String miscSpec)
    {
        this.otherNativeSpecs.remove(miscSpec);
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

    private void runGridCommand() throws DrmaaException
    {
        try
        {
            this.finishJobTemplate();

            this.jobid = this.gridSession.runJob(this.jobTemplate);
        }
        finally
        {
            this.releaseJobTemplate();
        }
    }



    public String getJobID()
    {
        return this.jobid;
    }

    public JobInfo getJobInfo()
    {
        return this.gridInfo;
    }

    public void waitForCompletion() throws DrmaaException
    {
        this.gridInfo = this.gridSession.wait(this.jobid, this.timeout);
        System.out.println("completed has exited= "+ gridInfo.hasExited());
       
    }

    private void releaseJobTemplate() throws DrmaaException
    {
        if (this.jobTemplate != null)
        {
            this.gridSession.deleteJobTemplate(this.jobTemplate);
            this.jobTemplate = null;
        }
    }
    /**
     * Terminate this job.
     * @throws DrmaaException
     */
    public void terminate() throws DrmaaException{
        this.gridSession.control(jobid, Session.TERMINATE);
    }
}

