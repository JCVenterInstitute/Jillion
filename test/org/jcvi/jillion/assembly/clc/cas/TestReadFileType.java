/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.clc.cas;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replayAll;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.testUtil.EasyMockUtil;
import org.jcvi.jillion.internal.trace.chromat.ChromatogramUtil;
import org.jcvi.jillion.internal.trace.chromat.abi.AbiUtil;
import org.jcvi.jillion.internal.trace.chromat.scf.SCFUtils;
import org.jcvi.jillion.internal.trace.chromat.ztr.ZTRUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
/**
 * @author dkatzel
 *
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( ChromatogramUtil.class )
public class TestReadFileType {

    @Test
    public void sffFile() throws FileNotFoundException, IOException{
        assertEquals(ReadFileType.SFF, 
                ReadFileType.getTypeFromFile(new File("my.sff")));
    }
   
   
    @Test
    public void fastaFile() throws Exception{
        assertEquals(ReadFileType.FASTA, 
                ReadFileType.getTypeFromFile(createFastaFile("my.fasta")));
    }
    @Test
    public void fnaShouldBeFastaFile() throws Exception{
        assertEquals(ReadFileType.FASTA, 
                ReadFileType.getTypeFromFile(createFastaFile("my.fna")));
    }
   
    @Test
    public void faShouldBeFastaFile() throws Exception{
        assertEquals(ReadFileType.FASTA, 
                ReadFileType.getTypeFromFile(createFastaFile("my.fa")));
    }
  
    @Test
    public void seqShouldBeFastaFile()throws Exception{
        assertEquals(ReadFileType.FASTA, 
                ReadFileType.getTypeFromFile(createFastaFile("my.seq")));
    }
   
    @Test
    public void contigsFileShouldBeFastaFile()throws Exception{
        assertEquals(ReadFileType.FASTA, 
                ReadFileType.getTypeFromFile(createFastaFile("my.100.contigs")));
    }
    @Test
    public void ztrShouldBeSangerFile()throws Exception{
        assertEquals(ReadFileType.SANGER, 
                ReadFileType.getTypeFromFile(createZtrFile("my.ztr")));
    }
 
   


	@Test
    public void scfShouldBeSangerFile()throws Exception{
        assertEquals(ReadFileType.SANGER, 
                ReadFileType.getTypeFromFile(createScfFile("my.scf")));
    }
   
    @Test
    public void abiShouldBeSangerFile()throws Exception{
        assertEquals(ReadFileType.SANGER, 
                ReadFileType.getTypeFromFile(createAb1File("my.abi")));
    }
  
    @Test
    public void noExtensionSangerFileShouldBeSangerFile()throws Exception{
        assertEquals(ReadFileType.SANGER, 
                ReadFileType.getTypeFromFile(createScfFile("trace")));
    }
    
    @Test
    public void noExtensionFastaFileShouldBeFastaFile()throws Exception{
        assertEquals(ReadFileType.FASTA, 
                ReadFileType.getTypeFromFile(createFastaFile("trace")));
    }
  
    
    @Test
    public void fastqShouldBeIlluminaFile()throws IOException{
        assertEquals(ReadFileType.FASTQ, 
                ReadFileType.getTypeFromFile(new File("my.fastq")));
    }
 
    
    @Test
    public void s_1_sequenceDotTxtShouldBeIlluminaFile()throws IOException{
        assertEquals(ReadFileType.FASTQ, 
                ReadFileType.getTypeFromFile(new File("s_1_sequence.txt")));
    }
  
    @Test
    public void s_2_sequenceDotTxtShouldBeIlluminaFile()throws IOException{
        assertEquals(ReadFileType.FASTQ, 
                ReadFileType.getTypeFromFile(new File("s_2_sequence.txt")));
        //s_2_sequence.txt
    }
 
    @Test
    public void fullPaths_2_sequenceDotTxtShouldBeIllumina() throws IOException{
        assertEquals(ReadFileType.FASTQ, 
                ReadFileType.getTypeFromFile(new File("/path/to/file/s_2_sequence.txt")));
    }
    
    private File createFastaFile(String name) throws Exception{
    	return createFileWithMagicNumber(name, ">ID1");
    }


	protected File createFileWithMagicNumber(String name, String magicNumber)throws Exception {
		return createFileWithMagicNumber(name, magicNumber.getBytes(IOUtil.UTF_8));
	}
	/**
	 * Use Powermock to intercept call to new FileInputStream( fakeFile)
	 * to return a mock inputStream so we can control
	 * the bytes returned when read back to determine the file type.
	 * 
	 * @param name name of the file to create.
	 * @param magicNumber the first few bytes of the file 
	 * that will be read from the mock inputstream.
	 * @return a new File instance with the path of 'name'
	 * @throws Exception
	 */
	protected File createFileWithMagicNumber(String name, byte[] magicNumber)throws Exception {
		FileInputStream mockStream = createMock(FileInputStream.class);
    	File file = new File(name);
    	expectNew(FileInputStream.class, file).andReturn(mockStream);
    	
    	
		expect(mockStream.read(isA(byte[].class), eq(0), eq(8192))).andAnswer(EasyMockUtil.writeArrayToInputStream(magicNumber));
    	replayAll();
    	return file;
	}
    
    private File createZtrFile(String name) throws Exception {
    	return createFileWithMagicNumber(name, ZTRUtil.getMagicNumber());
	}
    private File createScfFile(String name) throws  Exception {
    	return createFileWithMagicNumber(name, SCFUtils.getMagicNumber());
	}
    private File createAb1File(String name) throws Exception {
    	return createFileWithMagicNumber(name, AbiUtil.getMagicNumber());
	}
}
