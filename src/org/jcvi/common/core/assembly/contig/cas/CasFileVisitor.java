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
 * Created on Oct 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.assembly.contig.cas;

import org.jcvi.common.core.assembly.contig.cas.align.score.CasScoringScheme;
import org.jcvi.io.FileVisitor;
/**
 * {@code CasFileVisitor} is a {@link FileVisitor} implementation
 * for visiting CLC Bio's .cas assembly files.
 * @author dkatzel
 *
 *
 */
public interface CasFileVisitor extends FileVisitor{
    /**
     * Visit a {@link CasMatch}.
     * @param match the CasMatch object being visited (never null)
     */
    void visitMatch(CasMatch match);
    /**
     * Visit the invocation of the clc assembler that was
     * used to create this .cas file.
     * @param name name of the assembler used.
     * @param version version of the assembler used.
     * @param parameters assembler parameters invoked to create .cas file.
     */
    void visitAssemblyProgramInfo(String name, String version, String parameters);
    /**
     * Visit meta data for this .cas file.
     * @param numberOfContigSequences total number of contig sequences
     * in this .cas file.
     * @param numberOfReads total number of reads contained in this
     * .cas file.
     */
    void visitMetaData(long numberOfContigSequences, long numberOfReads);
    /**
     * Visit the number of files
     * that contain all the read data in this .cas assembly.
     * @param numberOfReadFiles number of files containing read data should
     * always be {@code >=0}.
     */
    void visitNumberOfReadFiles(long numberOfReadFiles);
    /**
     * Visit the number of files
     * that contain all the reference data in this .cas assembly.
     * @param numberOfContigFiles number of files containing read data should
     * always be {@code >=0}.
     */
    void visitNumberOfContigFiles(long numberOfContigFiles);
    /**
     * Visit the {@link CasFileInfo} for all the reference files
     * used in this .cas assembly.
     * @param contigFileInfo a CasFileInfo containing all data
     * about the reference files used; never null.
     */
    void visitContigFileInfo(CasFileInfo contigFileInfo);
    /**
     * Visit the {@link CasFileInfo} for all the read files
     * used in this .cas assembly.
     * @param readFileInfo a CasFileInfo containing all data
     * about the read files used; never null.
     */
    void visitReadFileInfo(CasFileInfo readFileInfo);
    
    void visitScoringScheme(CasScoringScheme scheme);
    
    void visitContigDescription(CasContigDescription description);
    
    void visitContigPair(CasContigPair contigPair);
    
}
