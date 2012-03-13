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

/**
 * ProcessRunner.java
 *
 * Created: Apr 15, 2008 - 2:59:36 PM (jsitz)
 *
 * Copyright 2008 J. Craig Venter Institute
 */
package org.jcvi.common.command;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.common.core.util.JoinedStringBuilder;

/**
 * A <code>Command</code> is a utility object which encapsulates all of the data necessary to
 * invoke an executable.  This does not require or assume that the executable will be run by
 * the JVM or even on the same host as the JVM, though those actions are available.
 *
 * @author jsitz@jcvi.org
 * @author dkatzel
 */
public class Command
{
  
    /** The {@link File} representing the executable. */
    private final File executable;
    /** The base directory to use. */
    private File workingDir=null;
    /** A {@link Map} of command line options. */
    private final Map<String, String> opt;
    /** A {@link List} of command line flags. */
    private final List<String> flags;
    /** A {@link List} of command targets. */
    private final List<String> targets;

    /**
     * Constructs a new <code>Command</code>.
     *
     * @param executable The executable handling the command.
     */
    public Command(File executable)
    {
        if(!executable.canExecute()){
            throw new IllegalArgumentException(
                    String.format("%s can not be executed",executable.getAbsolutePath()));
        }
        this.executable = executable;
        this.opt = new LinkedHashMap<String, String>();
        this.flags = new ArrayList<String>();
        this.targets = new ArrayList<String>();
    }

    /**
     * Fetches the executable used by this <code>Command</code>.  Though this is represented by
     * a {@link File}, there is no requirement that this file exist on the local host.  This
     * requirement only holds when attempting to use the {@link #execute()} family of methods.
     *
     * @return The executable path, as a <code>File</code>.
     */
    public final File getExecutable()
    {
        return this.executable;
    }

    /**
     * Sets the working directory used by this command.
     *
     * @param dir The directory to use, as a {@link File}.
     */
    public final void setWorkingDir(File dir)
    {
        this.workingDir = dir;
    }

    /**
     * Fetches the working directory used by this command. This directory is used as the default
     * location to resolve relative paths from.
     *
     * @return The directory to use, as a {@link File}.
     */
    public final File getWorkingDir()
    {
        return this.workingDir;
    }

    public final void addFlag(String flag)
    {
        this.flags.add(flag);
    }

    public final void addFlag(String ... newFlags)
    {
        for (final String flag : newFlags)
        {
            this.addFlag(flag);
        }
    }

    public final void removeFlag(String flag)
    {
        this.flags.remove(flag);
    }

    public final void removeAllArguments()
    {
        this.flags.clear();
    }

    public final void setOption(String flag, String value)
    {
        if (value == null)
        {
            throw new NullPointerException("value can not be null");
        }
        this.opt.put(flag, value);
    }

    public final void clearOption(String flag)
    {
        this.opt.remove(flag);
    }

    public final void clearAllOptions()
    {
        this.opt.clear();
    }

    public final void addTarget(String target)
    {
        this.targets.add(target);
    }

    public final void removeTarget(String target)
    {
        this.targets.remove(target);
    }

    public final void removeAllTargets()
    {
        this.targets.clear();
    }
    
    public final void removeAllFlags()
    {
        this.flags.clear();
    }

    public final void addTargets(String ... targetList)
    {
        for (final String target : targetList)
        {
            this.addTarget(target);
        }
    }

    public final String getExecutablePath()
    {
        return this.executable.getAbsolutePath();
    }

    public final List<String> getArguments()
    {
        final int argSize = this.opt.size() + this.flags.size() + this.targets.size();
        final List<String> command = new ArrayList<String>(argSize);

        for(final String arg : this.opt.keySet())
        {
            final String val = this.opt.get(arg);
            command.add(arg);
            if (val != null)
            {
                command.add(val);
            }
        }

        for(final String flag : this.flags)
        {
            command.add(flag);
        }

        for(final String target : this.targets)
        {
            command.add(target);
        }

        return command;
    }

    public final List<String> getCommandLine()
    {
        final List<String> args = this.getArguments();
        final List<String> command = new ArrayList<String>(args.size() + 1);
        command.add(this.executable.getAbsolutePath());
        command.addAll(args);

        return command;
    }

    public final Process execute() throws IOException
    {
        ProcessBuilderWrapper procBuilder = createNewProcessBuilderWrapper();
        procBuilder.command(this.getCommandLine());
        procBuilder.directory(workingDir);
        return procBuilder.start();
    }
    
    /**
     * Create a new {@link ProcessBuilderWrapper}
     * instance which will be populated with the parameters 
     * specified by this Command and then started.
     * This method maybe overridden if a custom
     * implementation is required.
     * @return a new ProcessBuilderWrapper should never be null.
     */
    protected ProcessBuilderWrapper createNewProcessBuilderWrapper(){
        return new DefaultProcessBuilderWrapper();
    }

    public final int executeAndWait() throws IOException
    {
        final Process proc = this.execute();
        return CommandUtils.waitFor(proc);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString()
    {
        return new JoinedStringBuilder(this.getCommandLine())
                    .glue(" ")
                    .build();
    }
    /**
     * Wraps a ProcessBuilder so we can mock it/ replace
     * it with our own implementations.
     * @author dkatzel
     */
    protected interface ProcessBuilderWrapper {
        ProcessBuilderWrapper command(List<String> command);
        ProcessBuilderWrapper directory(File workDir);
        Process start() throws IOException;
    }
    /**
     * Default implementation of ProcessBuilderWrapper that just
     * delegates all calls to a {@link ProcessBuilder}.
     */
    private static final class DefaultProcessBuilderWrapper implements ProcessBuilderWrapper{
        private final ProcessBuilder builder = new ProcessBuilder();

        @Override
        public ProcessBuilderWrapper command(List<String> command) {
            builder.command(command);
            return this;
        }

        /**
        * {@inheritDoc}
        */
        @Override
        public ProcessBuilderWrapper directory(File workDir) {
            builder.directory(workDir);
            return this;
        }
        
        @Override
        public Process start() throws IOException{
            return builder.start();
        }
        
        
    }
}
