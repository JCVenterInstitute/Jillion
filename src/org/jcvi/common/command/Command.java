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
  
    
    

    /** The {@link ProcessBuilder} to use when invoking the command locally. */
    private final ProcessBuilder procBuilder;
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
        this.procBuilder = new ProcessBuilder();
        this.executable = executable;
        this.opt = new LinkedHashMap<String, String>();
        this.flags = new ArrayList<String>();
        this.targets = new ArrayList<String>();

        this.workingDir = new File(System.getProperty("user.dir"));
    }


    /**
     * Constructs a new <code>Command</code>.
     *
     * @param executable The path to an executable, as a <code>String</code>.
     */
    public Command(String executable)
    {
        this(new File(executable));
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
    public void setWorkingDir(File dir)
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

    public void addFlag(String flag)
    {
        this.flags.add(flag);
    }

    public void addFlag(String ... newFlags)
    {
        for (final String flag : newFlags)
        {
            this.addFlag(flag);
        }
    }

    public Map<String, String> getOpt() {
        return opt;
    }


    public List<String> getFlags() {
        return flags;
    }


    public List<String> getTargets() {
        return targets;
    }


    public void removeFlag(String flag)
    {
        this.flags.remove(flag);
    }

    public void removeAllArguments()
    {
        this.flags.clear();
    }

    public void setOption(String flag, String value)
    {
        if (value == null)
        {
            this.clearOption(flag);
        }
        else
        {
            this.opt.put(flag, value);
        }
    }

    public void clearOption(String flag)
    {
        this.opt.remove(flag);
    }

    public void clearAllOptions()
    {
        this.opt.clear();
    }

    public void addTarget(String target)
    {
        this.targets.add(target);
    }

    public void removeTarget(String target)
    {
        this.targets.remove(target);
    }

    public void removeAllTargets()
    {
        this.targets.clear();
    }
    
    public void removeAllFlags()
    {
        this.flags.clear();
    }

    public void addTargets(String ... targetList)
    {
        for (final String target : targetList)
        {
            this.addTarget(target);
        }
    }

    public String getExecutablePath()
    {
        return this.executable.getAbsolutePath();
    }

    public List<String> getArguments()
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

    public List<String> getCommandLine()
    {
        final List<String> args = this.getArguments();
        final List<String> command = new ArrayList<String>(args.size() + 1);
        command.add(this.executable.getAbsolutePath());
        command.addAll(args);

        return command;
    }

    public Process execute() throws IOException
    {
        this.procBuilder.command(this.getCommandLine());
        this.procBuilder.directory(workingDir);
        return this.procBuilder.start();

        
    }

    public int executeAndWait() throws IOException
    {
        final Process proc = this.execute();
        return CommandUtils.waitFor(proc);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        final StringBuilder cmd = new StringBuilder();

        for (final String arg : this.getCommandLine())
        {
            if (cmd.length() > 0)
            {
                cmd.append(' ');
            }
            if (arg.indexOf(' ') > -1)
            {
                if (arg.indexOf('"') > -1)
                {
                    cmd.append('\'').append(arg).append('\'');
                }
                else
                {
                    cmd.append('"').append(arg).append('"');
                }
            }
            else
            {
                cmd.append(arg);
            }
        }

        return cmd.toString();
    }
}
