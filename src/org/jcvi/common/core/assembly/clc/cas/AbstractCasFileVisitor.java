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
/*
 * Created on Oct 30, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.clc.cas;

import org.jcvi.common.core.assembly.contig.cas.align.score.CasScoringScheme;
/**
 * {@code AbstractCasFileVisitor} is an abstract implementation
 * of {@link CasFileVisitor} with all the methods of the interface
 * implemented as no-ops.  This simplifies implementing the interface
 * since subclasses will only have to override methods they care about.
 * @author dkatzel
 *
 *
 */
public abstract class AbstractCasFileVisitor implements CasFileVisitor {
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void visitAssemblyProgramInfo(String name, String version,
            String parameters) {

    }
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void visitReferenceDescription(CasReferenceDescription description) {}
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void visitReferenceFileInfo(CasFileInfo contigFileInfo) {}
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void visitContigPair(CasContigPair contigPair) {}
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void visitMatch(CasMatch match) {}
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void visitMetaData(long numberOfContigSequences, long numberOfReads) {}
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void visitNumberOfReferenceFiles(long numberOfContigFiles) {}
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void visitNumberOfReadFiles(long numberOfReadFiles) {}
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void visitReadFileInfo(CasFileInfo readFileInfo) {}
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void visitScoringScheme(CasScoringScheme scheme) {}
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void visitEndOfFile() {}
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void visitFile() {}

}
