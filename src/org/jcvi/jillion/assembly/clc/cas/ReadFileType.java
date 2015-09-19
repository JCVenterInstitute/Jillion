/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
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
public enum ReadFileType {
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
        if("fastq".equals(extension) || readFileName.matches(".+\\d+_sequence\\.txt") || readFileName.endsWith(".fastq.untrimmed")){
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
