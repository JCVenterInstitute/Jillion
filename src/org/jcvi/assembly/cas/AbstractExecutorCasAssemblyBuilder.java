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

package org.jcvi.assembly.cas;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.jcvi.command.Command;


/**
 * @author dkatzel
 *
 *
 */
public abstract class AbstractExecutorCasAssemblyBuilder<R> extends AbstractMultiThreadedCasAssemblyBuilder{

        private final ExecutorService executor;
        private final List<Callable<R>> submissions = new ArrayList<Callable<R>>();
        
        public AbstractExecutorCasAssemblyBuilder(File casFile,int numberOfContigsToConvertAtSameTime){
            super(casFile);
            this.executor = createExecutorService(numberOfContigsToConvertAtSameTime);
        }
        
        protected abstract ExecutorService createExecutorService(int numberOfContigsToConvertAtSameTime);
        protected abstract Callable<R> createSingleAssemblyCasConversionCallable(Command command);
        protected abstract void jobFinished(R returnedValue);
        
        /**
         * {@inheritDoc}
         */
         @Override
         protected void submitSingleCasAssemblyConversion(Command command)
                 throws IOException {
             submissions.add(createSingleAssemblyCasConversionCallable(command));
             
         }

         @Override
         protected void waitForAllAssembliesToFinish() throws Exception {
             List<Future<R>> futures = executor.invokeAll(submissions);
             for(Future<R> future: futures){
                 jobFinished(future.get());
             }
             submissions.clear();
             executor.shutdown();
         }
}
