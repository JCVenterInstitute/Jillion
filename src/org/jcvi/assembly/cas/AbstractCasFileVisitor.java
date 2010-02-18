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
package org.jcvi.assembly.cas;

import org.jcvi.assembly.cas.alignment.score.CasScoringScheme;

public abstract class AbstractCasFileVisitor implements CasFileVisitor {

    @Override
    public void visitAssemblyProgramInfo(String name, String version,
            String parameters) {

    }

    @Override
    public void visitContigDescription(CasContigDescription description) {}

    @Override
    public void visitContigFileInfo(CasFileInfo contigFileInfo) {}

    @Override
    public void visitContigPair(CasContigPair contigPair) {}

    @Override
    public void visitMatch(CasMatch match) {}

    @Override
    public void visitMetaData(long numberOfContigSequences, long numberOfReads) {}

    @Override
    public void visitNumberOfContigFiles(long numberOfContigFiles) {}

    @Override
    public void visitNumberOfReadFiles(long numberOfReadFiles) {}

    @Override
    public void visitReadFileInfo(CasFileInfo readFileInfo) {}

    @Override
    public void visitScoringScheme(CasScoringScheme scheme) {}

    @Override
    public void visitEndOfFile() {}

    @Override
    public void visitFile() {}

}
