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

package org.jcvi.common.core.assembly.clc.cas;

import java.io.File;

import org.jcvi.common.core.assembly.clc.cas.ReadFileType;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * @author dkatzel
 *
 *
 */
public class TestReadFileType {

    @Test
    public void sffFile(){
        assertEquals(ReadFileType.SFF, 
                ReadFileType.getTypeFromFile(new File("my.sff")));
    }
    @Test
    public void sff(){
        assertEquals(ReadFileType.SFF, 
                ReadFileType.getTypeFromFile("my.sff"));
    }
    
    @Test
    public void fasta(){
        assertEquals(ReadFileType.FASTA, 
                ReadFileType.getTypeFromFile("my.fasta"));
    }
    @Test
    public void fastaFile(){
        assertEquals(ReadFileType.FASTA, 
                ReadFileType.getTypeFromFile(new File("my.fasta")));
    }
    @Test
    public void fnaShouldBeFastaFile(){
        assertEquals(ReadFileType.FASTA, 
                ReadFileType.getTypeFromFile(new File("my.fna")));
    }
    @Test
    public void fnaShouldBeFasta(){
        assertEquals(ReadFileType.FASTA, 
                ReadFileType.getTypeFromFile("my.fna"));
    }
    
    @Test
    public void faShouldBeFastaFile(){
        assertEquals(ReadFileType.FASTA, 
                ReadFileType.getTypeFromFile(new File("my.fa")));
    }
    @Test
    public void faShouldBeFasta(){
        assertEquals(ReadFileType.FASTA, 
                ReadFileType.getTypeFromFile("my.fa"));
    }
    @Test
    public void seqShouldBeFastaFile(){
        assertEquals(ReadFileType.FASTA, 
                ReadFileType.getTypeFromFile(new File("my.seq")));
    }
    @Test
    public void seqShouldBeFasta(){
        assertEquals(ReadFileType.FASTA, 
                ReadFileType.getTypeFromFile("my.seq"));
    }
    
    @Test
    public void ztrShouldBeSangerFile(){
        assertEquals(ReadFileType.SANGER, 
                ReadFileType.getTypeFromFile(new File("my.ztr")));
    }
    @Test
    public void ztrShouldBeSanger(){
        assertEquals(ReadFileType.SANGER, 
                ReadFileType.getTypeFromFile("my.ztr"));
    }
    
    @Test
    public void scfShouldBeSangerFile(){
        assertEquals(ReadFileType.SANGER, 
                ReadFileType.getTypeFromFile(new File("my.scf")));
    }
    @Test
    public void scfShouldBeSanger(){
        assertEquals(ReadFileType.SANGER, 
                ReadFileType.getTypeFromFile("my.scf"));
    }
    @Test
    public void abiShouldBeSangerFile(){
        assertEquals(ReadFileType.SANGER, 
                ReadFileType.getTypeFromFile(new File("my.abi")));
    }
    @Test
    public void abiShouldBeSanger(){
        assertEquals(ReadFileType.SANGER, 
                ReadFileType.getTypeFromFile("my.abi"));
    }
    @Test
    public void noExtensionShouldBeSangerFile(){
        assertEquals(ReadFileType.SANGER, 
                ReadFileType.getTypeFromFile(new File("trace")));
    }
    @Test
    public void noExtensionShouldBeSanger(){
        assertEquals(ReadFileType.SANGER, 
                ReadFileType.getTypeFromFile("trace"));
    }
    
    @Test
    public void fastqShouldBeIlluminaFile(){
        assertEquals(ReadFileType.FASTQ, 
                ReadFileType.getTypeFromFile(new File("my.fastq")));
    }
    @Test
    public void fastqShouldBeIllumina(){
        assertEquals(ReadFileType.FASTQ, 
                ReadFileType.getTypeFromFile("my.fastq"));
    }
    
    @Test
    public void s_1_sequenceDotTxtShouldBeIlluminaFile(){
        assertEquals(ReadFileType.FASTQ, 
                ReadFileType.getTypeFromFile(new File("s_1_sequence.txt")));
    }
    @Test
    public void s_1_sequenceDotTxtShouldBeIllumina(){
        assertEquals(ReadFileType.FASTQ, 
                ReadFileType.getTypeFromFile("s_1_sequence.txt"));
    }
    
    @Test
    public void s_2_sequenceDotTxtShouldBeIlluminaFile(){
        assertEquals(ReadFileType.FASTQ, 
                ReadFileType.getTypeFromFile(new File("s_2_sequence.txt")));
        //s_2_sequence.txt
    }
    @Test
    public void s_2_sequenceDotTxtShouldBeIllumina(){
        assertEquals(ReadFileType.FASTQ, 
                ReadFileType.getTypeFromFile("s_2_sequence.txt"));
    }
    @Test
    public void fullPaths_2_sequenceDotTxtShouldBeIllumina(){
        assertEquals(ReadFileType.FASTQ, 
                ReadFileType.getTypeFromFile("/path/to/file/s_2_sequence.txt"));
    }
}
