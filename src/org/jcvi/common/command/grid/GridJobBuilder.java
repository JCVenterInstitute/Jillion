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

import org.jcvi.common.command.grid.GridJob.MemoryUnit;

/**
 * @author dkatzel
 *
 *
 */
public interface GridJobBuilder<J extends GridJob> extends org.jcvi.common.core.util.Builder<J>{

    GridJobBuilder<J> preExecutionHook(PreExecutionHook preExecutionHook);

    GridJobBuilder<J> postExecutionHook(PostExecutionHook postExecutionHook);

    GridJobBuilder<J> setBinaryMode(boolean mode);

    GridJobBuilder<J> setProjectCode(String code);

    GridJobBuilder<J> setQueue(String queueName);

    GridJobBuilder<J> setMemory(Integer size, MemoryUnit unit);

    GridJobBuilder<J> setMinCPUs(Integer minimumCPUs);

    GridJobBuilder<J> setArchitecture(String arch);

    GridJobBuilder<J> addEmailRecipient(String emailAddr);

    GridJobBuilder<J> clearEmailRecipients();

    GridJobBuilder<J> setTimeout(Long seconds);

    GridJobBuilder<J> setTimeoutMinutes(long minutes);

    GridJobBuilder<J> copyCurrentEnvironment();

    GridJobBuilder<J> copyCurrentEnvironment(String var);

    GridJobBuilder<J> setEnvironment(String var, String val);

    GridJobBuilder<J> setNativeSpec(String value);

    GridJobBuilder<J> clearNativeSpec(String miscSpec);

    GridJobBuilder<J> setName(String jobName);

    GridJobBuilder<J> setWorkingDirectory(File workingDirectory);

    GridJobBuilder<J> setInputFile(File inputFile);

    GridJobBuilder<J> setOutputFile(File outputFile);

    GridJobBuilder<J> setErrorFile(File errorFile);

}