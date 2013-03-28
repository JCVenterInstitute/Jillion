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

import org.jcvi.jillion.core.io.FileUtil;

/**
 * @author dkatzel
 *
 *
 */
public enum ReadFileType {

    SFF,
    FASTQ,
    FASTA,
    SANGER;
    
    public static ReadFileType getTypeFromFile(File readFile){
        return getTypeFromFile(readFile.getName());
    }
    public static ReadFileType getTypeFromFile(String readFileName){
        String extension =FileUtil.getExtension(readFileName);
        if("fastq".equals(extension) || readFileName.matches("\\S*/?s_+\\d+_sequence\\.txt") || readFileName.endsWith(".fastq.untrimmed")){
           return FASTQ;
        }if("sff".equals(extension)){
            return SFF;
        }
        if("fasta".equals(extension) || "fna".equals(extension) || "fa".equals(extension) || "seq".equals(extension) || readFileName.endsWith(".fasta.untrimmed")){
            return FASTA;
        }
        return SANGER;
    }
    
}
