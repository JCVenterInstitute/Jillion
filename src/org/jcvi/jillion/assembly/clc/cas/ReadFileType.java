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
package org.jcvi.jillion.assembly.clc.cas;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.io.FileUtil;
import org.jcvi.jillion.internal.trace.chromat.ChromatogramUtil;

/**
 * {@code ReadFileType} says what kind of
 * the file type a set of reads is stored as.
 * @author dkatzel
 *
 *
 */
enum ReadFileType {
	/**
	 * The reads are stored in an SFF file.
	 */
    SFF,
    /**
	 * The reads are stored in a fastq
	 * formatted file.
	 */
    FASTQ,
    /**
	 * The reads are stored in a fasta
	 * formatted file.
	 */
    FASTA,
    /**
     * The read is stored as a sanger chromtogram.
     */
    SANGER;
    
    public static ReadFileType getTypeFromFile(File readFile) throws IOException{
    	String readFileName= readFile.getName();
    	String extension =FileUtil.getExtension(readFileName);
        if("fastq".equals(extension) || readFileName.matches("\\S*/?s_+\\d+_sequence\\.txt") || readFileName.endsWith(".fastq.untrimmed")){
           return FASTQ;
        }if("sff".equals(extension)){
            return SFF;
        }if(ChromatogramUtil.isChromatogram(readFile)){
        	return SANGER;
        }
        //there are so many different ways fasta files are named,
        //that just assume anything not matching something
        //above is a fasta file
        return FASTA;
    }
    
}
