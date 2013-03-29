/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
/*
 * Created on Oct 27, 2009
 *
 * @author dkatzel
 */
package org.jcvi.jillion.assembly.clc.cas;

import org.jcvi.jillion.core.io.FileVisitor;
import org.jcvi.jillion.fasta.FastaFileParser;
import org.jcvi.jillion.trace.fastq.FastqVisitor;
/**
 * {@code CasFileVisitor} is a {@link FileVisitor} implementation
 * for visiting CLC Bio's .cas assembly files.
 * @author dkatzel
 *
 *
 */
public interface CasFileVisitor{
    
    public interface CasVisitorCallback {
    	/**
    	 * {@code FastaVisitorMemento} is a marker
    	 * interface that {@link CasFileParser}
    	 * instances can use to "rewind" back
    	 * to the position in its cas file
    	 * in order to revisit portions of the cas file. 
    	 * {@link CasVisitorMemento} should only be used
    	 * by the {@link CasFileParser} instance that
    	 * generated it.
    	 * @author dkatzel
    	 *
    	 */
    	interface CasVisitorMemento{
    		
    	}
    	/**
    	 * Is this callback capable of
    	 * creating {@link CasVisitorMemento}s
    	 * via {@link #createMemento()}.
    	 * @return {@code true} if this callback
    	 * can create mementos; {@code false} otherwise.
    	 */
    	boolean canCreateMemento();
    	/**
    	 * Create a {@link CasVisitorMemento}
    	 * 
    	 * @return a {@link CasVisitorMemento}; never null.
    	 * @see #canCreateMemento()
    	 * @throws UnsupportedOperationException if {@link #canCreateMemento()}
    	 * returns {@code false}.
    	 */
    	CasVisitorMemento createMemento();
    	/**
    	 * Tell the {@link FastaFileParser} to stop parsing
    	 * the fasta file.  {@link FastqVisitor#visitEnd()}
    	 * will still be called.
    	 */
    	void haltParsing();
	}

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
     * @param numberOfReferenceSequences total number of reference sequences
     * in this .cas file.  <strong>NOTE:</strong> It is possible that not all the references
     * had reads aligned to them.
     * @param numberOfReads total number of reads contained in this
     * .cas file.
     */
    void visitMetaData(long numberOfReferenceSequences, long numberOfReads);
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
     * @param numberOfReferenceFiles number of files containing read data should
     * always be {@code >=0}.
     */
    void visitNumberOfReferenceFiles(long numberOfReferenceFiles);
    /**
     * Visit the {@link CasFileInfo} for all the reference files
     * used in this .cas assembly.
     * @param referenceFileInfo a CasFileInfo containing all data
     * about the reference files used; never null.
     */
    void visitReferenceFileInfo(CasFileInfo referenceFileInfo);
    /**
     * Visit the {@link CasFileInfo} for a single read file 
     * or read mate pair.  This method will be called
     * n times where n is the sum of the number of fragment read input files
     * and the number of mated pairs of read input files
     * used in this .cas assembly.
     * @param readFileInfo a CasFileInfo containing all data
     * about the read files used; never null.
     */
    void visitReadFileInfo(CasFileInfo readFileInfo);
    
    void visitScoringScheme(CasScoringScheme scheme);
    /**
     * Visit a {@link CasReferenceDescription} of one of the references
     * used in this assembly.  This method will be called
     * n times where n is the number of references used in this 
     * cas file.
     * @param description the {@link CasReferenceDescription};
     * will never be null.
     */
    void visitReferenceDescription(CasReferenceDescription description);
    /**
     * Contig pairs are currently not used in CLC reference
     * assembly so this method will not get called
     * however it is a place holder for when it is eventually
     * included.
     * @param contigPair the {@link CasContigPair}; will never be null.
     */
    void visitContigPair(CasContigPair contigPair);
    
    void visitEnd();
    
    void halted();
    
    CasMatchVisitor visitMatches(CasVisitorCallback callback);
    
}
