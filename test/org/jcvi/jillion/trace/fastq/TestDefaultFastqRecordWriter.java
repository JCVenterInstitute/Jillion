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
package org.jcvi.jillion.trace.fastq;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.trace.fastq.DefaultFastqFileDataStore;
import org.jcvi.jillion.trace.fastq.FastqDataStore;
import org.jcvi.jillion.trace.fastq.FastqQualityCodec;
import org.jcvi.jillion.trace.fastq.FastqRecordWriter;
import org.jcvi.jillion.trace.fastq.FastqRecordWriterBuilder;
import org.junit.Test;

public class TestDefaultFastqRecordWriter {

	private final FastqDataStore datastore;
	
	public TestDefaultFastqRecordWriter() throws IOException{
		ResourceHelper RESOURCES = new ResourceHelper(TestDefaultFastqRecordWriter.class);
		datastore = DefaultFastqFileDataStore.create(RESOURCES.getFile("files/example.fastq"),FastqQualityCodec.ILLUMINA);
	}
	
	@Test
    public void illuminaEncoding() throws DataStoreException, IOException{
        String expected = "@SOLEXA1:4:1:12:1489#0/1\n"+
                "TATTTAAAATCTAATANGTCTTGATTTGAAATTGAAAGAGCAAAAATCTGATTGATTTTATTGAAGAATAATTTGATTTAATATATTCTTAAGTCTGTTT\n"+
                "+\n"+
                "abaab]_]aaa`bbabB`Wb__aa\\_]W]a`^[`\\T`aZa_aa`WXa``]_`[`^a^^[`^][a^Raaa\\V\\OQ]aYQ^aa^\\`GRTDP`^T^Lb^aR`S\n";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FastqRecordWriter sut = new FastqRecordWriterBuilder(out)
        						.qualityCodec(FastqQualityCodec.ILLUMINA)
        						.build();
        sut.write(datastore.get("SOLEXA1:4:1:12:1489#0/1"));
        sut.close();
        
        String actual = new String(out.toByteArray(), IOUtil.UTF_8);
        assertEquals(expected, actual);
    }
	
	@Test
    public void defaultsToSangerEncoding() throws DataStoreException, IOException{
        String expected = "@SOLEXA1:4:1:12:1489#0/1\n"+
                "TATTTAAAATCTAATANGTCTTGATTTGAAATTGAAAGAGCAAAAATCTGATTGATTTTATTGAAGAATAATTTGATTTAATATATTCTTAAGTCTGTTT\n"+
                "+\n"+
                "BCBBC>@>BBBACCBC#A8C@@BB=@>8>BA?<A=5AB;B@BBA89BAA>@A<A?B??<A?><B?3BBB=7=02>B:2?BB?=A(35%1A?5?-C?B3A4\n";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FastqRecordWriter sut = new FastqRecordWriterBuilder(out)
        						.build();
        sut.write(datastore.get("SOLEXA1:4:1:12:1489#0/1"));
        sut.close();
        
        String actual = new String(out.toByteArray(), IOUtil.UTF_8);
        assertEquals(expected, actual);
    }
	@Test
    public void sangerEncoding() throws DataStoreException, IOException{
        String expected = "@SOLEXA1:4:1:12:1489#0/1\n"+
                "TATTTAAAATCTAATANGTCTTGATTTGAAATTGAAAGAGCAAAAATCTGATTGATTTTATTGAAGAATAATTTGATTTAATATATTCTTAAGTCTGTTT\n"+
                "+\n"+
                "BCBBC>@>BBBACCBC#A8C@@BB=@>8>BA?<A=5AB;B@BBA89BAA>@A<A?B??<A?><B?3BBB=7=02>B:2?BB?=A(35%1A?5?-C?B3A4\n";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FastqRecordWriter sut = new FastqRecordWriterBuilder(out)
        						.qualityCodec(FastqQualityCodec.SANGER)
        						.build();
        sut.write(datastore.get("SOLEXA1:4:1:12:1489#0/1"));
        sut.close();
        
        String actual = new String(out.toByteArray(), IOUtil.UTF_8);
        assertEquals(expected, actual);
    }
	@Test
    public void multiline() throws DataStoreException, IOException{
        String expected = "@SOLEXA1:4:1:12:1489#0/1\n"+
                "TATTTAAAATCTAATANGTCTTGATTTGAA\n" +
                "ATTGAAAGAGCAAAAATCTGATTGATTTTA\n" +
                "TTGAAGAATAATTTGATTTAATATATTCTT\n" +
                "AAGTCTGTTT\n"+
                "+\n"+
                "BCBBC>@>BBBACCBC#A8C@@BB=@>8>B\n" +
                "A?<A=5AB;B@BBA89BAA>@A<A?B??<A\n" +
                "?><B?3BBB=7=02>B:2?BB?=A(35%1A\n" +
                "?5?-C?B3A4\n";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FastqRecordWriter sut = new FastqRecordWriterBuilder(out)
        						.qualityCodec(FastqQualityCodec.SANGER)
        						.basesPerLine(30)
        						.build();
        sut.write(datastore.get("SOLEXA1:4:1:12:1489#0/1"));
        sut.close();
        
        String actual = new String(out.toByteArray(), IOUtil.UTF_8);
        assertEquals(expected, actual);
    }
	@Test
    public void multilineLineEndsAtEdgeShouldNotAddExtraBlankLine() throws DataStoreException, IOException{
        String expected = "@SOLEXA1:4:1:12:1489#0/1\n"+
                "TATTTAAAATCTAATANGTCTTGATTTGAAATTGAAAGAGCAAAAATCTG\n" +
                "ATTGATTTTATTGAAGAATAATTTGATTTAATATATTCTTAAGTCTGTTT\n"+
                "+\n"+
                "BCBBC>@>BBBACCBC#A8C@@BB=@>8>BA?<A=5AB;B@BBA89BAA>\n" +
                "@A<A?B??<A?><B?3BBB=7=02>B:2?BB?=A(35%1A?5?-C?B3A4\n";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FastqRecordWriter sut = new FastqRecordWriterBuilder(out)
        						.qualityCodec(FastqQualityCodec.SANGER)
        						.basesPerLine(50)
        						.build();
        sut.write(datastore.get("SOLEXA1:4:1:12:1489#0/1"));
        sut.close();
        
        String actual = new String(out.toByteArray(), IOUtil.UTF_8);
        assertEquals(expected, actual);
    }
	@Test
    public void withQualityLineDuplicated() throws DataStoreException, IOException{
        String expected = "@SOLEXA1:4:1:12:1489#0/1\n"+
                "TATTTAAAATCTAATANGTCTTGATTTGAAATTGAAAGAGCAAAAATCTGATTGATTTTATTGAAGAATAATTTGATTTAATATATTCTTAAGTCTGTTT\n"+
                "+SOLEXA1:4:1:12:1489#0/1\n"+
                "abaab]_]aaa`bbabB`Wb__aa\\_]W]a`^[`\\T`aZa_aa`WXa``]_`[`^a^^[`^][a^Raaa\\V\\OQ]aYQ^aa^\\`GRTDP`^T^Lb^aR`S\n";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FastqRecordWriter sut = new FastqRecordWriterBuilder(out)
        						.duplicateIdOnQualityDefLine()
        						.qualityCodec(FastqQualityCodec.ILLUMINA)
        						.build();
        sut.write(datastore.get("SOLEXA1:4:1:12:1489#0/1"));
        sut.close();
        
        String actual = new String(out.toByteArray(), IOUtil.UTF_8);
        assertEquals(expected, actual);
    }
	
	@Test
	public void multipleRecords() throws IOException, DataStoreException{
		 String expected = "@SOLEXA1:4:1:12:1489#0/1\n"+
	                "TATTTAAAATCTAATANGTCTTGATTTGAAATTGAAAGAGCAAAAATCTGATTGATTTTATTGAAGAATAATTTGATTTAATATATTCTTAAGTCTGTTT\n"+
	                "+\n"+
	                "abaab]_]aaa`bbabB`Wb__aa\\_]W]a`^[`\\T`aZa_aa`WXa``]_`[`^a^^[`^][a^Raaa\\V\\OQ]aYQ^aa^\\`GRTDP`^T^Lb^aR`S\n"+
	                "@SOLEXA1:4:1:12:1692#0/1 example comment\n"+
	                "ACGCCTGCGTTATGGTNTAACAGGCATTCCGCCCCAGACAAACTCCCCCCCTAACCATGTCTTTCGCAAAAATCAGTCAATAAATGACCTTAACTTTAGA\n"+
	                "+\n"+
	                "`a\\a`^\\a^ZZa[]^WB_aaaa^^a`]^a`^`aaa`]``aXaaS^a^YaZaTW]a_aPY\\_UVY[P_ZHQY_NLZUR[^UZ\\TZWT_[_VWMWaRFW]BB\n";

	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        FastqRecordWriter sut = new FastqRecordWriterBuilder(out)
	        						.qualityCodec(FastqQualityCodec.ILLUMINA)
	        						.build();
	        sut.write(datastore.get("SOLEXA1:4:1:12:1489#0/1"));
	        sut.write(datastore.get("SOLEXA1:4:1:12:1692#0/1"));
	        sut.close();
	        
	        String actual = new String(out.toByteArray(), IOUtil.UTF_8);
	        assertEquals(expected, actual);
	}
	@Test
	public void differentCharset() throws IOException, DataStoreException{
		 String expected = "@SOLEXA1:4:1:12:1489#0/1\n"+
	                "TATTTAAAATCTAATANGTCTTGATTTGAAATTGAAAGAGCAAAAATCTGATTGATTTTATTGAAGAATAATTTGATTTAATATATTCTTAAGTCTGTTT\n"+
	                "+\n"+
	                "abaab]_]aaa`bbabB`Wb__aa\\_]W]a`^[`\\T`aZa_aa`WXa``]_`[`^a^^[`^][a^Raaa\\V\\OQ]aYQ^aa^\\`GRTDP`^T^Lb^aR`S\n"+
	                "@SOLEXA1:4:1:12:1692#0/1 example comment\n"+
	                "ACGCCTGCGTTATGGTNTAACAGGCATTCCGCCCCAGACAAACTCCCCCCCTAACCATGTCTTTCGCAAAAATCAGTCAATAAATGACCTTAACTTTAGA\n"+
	                "+\n"+
	                "`a\\a`^\\a^ZZa[]^WB_aaaa^^a`]^a`^`aaa`]``aXaaS^a^YaZaTW]a_aPY\\_UVY[P_ZHQY_NLZUR[^UZ\\TZWT_[_VWMWaRFW]BB\n";

	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        Charset charset = Charset.forName("UTF-16");
	        FastqRecordWriter sut = new FastqRecordWriterBuilder(out)
	        						.qualityCodec(FastqQualityCodec.ILLUMINA)
	        						.charset(charset)
	        						.build();
	        sut.write(datastore.get("SOLEXA1:4:1:12:1489#0/1"));
	        sut.write(datastore.get("SOLEXA1:4:1:12:1692#0/1"));
	        sut.close();
	        
	        String actual = new String(out.toByteArray(), charset);
	        assertEquals(expected, actual);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void differentNumberOfNucleotidesAndQualitiesShouldThrowIllegalArgumentException() throws IOException{
		 ByteArrayOutputStream out = new ByteArrayOutputStream();
		 FastqRecordWriter sut = new FastqRecordWriterBuilder(out).build();
		 sut.write("id", new NucleotideSequenceBuilder("ACGT").build(),
				 new QualitySequenceBuilder(new byte[]{10,11,12,13,14}).build());
		 
	}
	@Test(expected = NullPointerException.class)
	public void nullIdShouldThrowNullPointerException() throws IOException{
		 ByteArrayOutputStream out = new ByteArrayOutputStream();
		 FastqRecordWriter sut = new FastqRecordWriterBuilder(out).build();
		 sut.write(null, new NucleotideSequenceBuilder("ACGT").build(),
				 new QualitySequenceBuilder(new byte[]{10,11,12,13}).build());
		 
	}
	@Test(expected = NullPointerException.class)
	public void nullNucleotidesShouldThrowNullPointerException() throws IOException{
		 ByteArrayOutputStream out = new ByteArrayOutputStream();
		 FastqRecordWriter sut = new FastqRecordWriterBuilder(out).build();
		 sut.write("id", null,
				 new QualitySequenceBuilder(new byte[]{10,11,12,13}).build());
		 
	}
	@Test(expected = NullPointerException.class)
	public void nullQualitiesShouldThrowNullPointerException() throws IOException{
		 ByteArrayOutputStream out = new ByteArrayOutputStream();
		 FastqRecordWriter sut = new FastqRecordWriterBuilder(out).build();
		 sut.write("id", new NucleotideSequenceBuilder("ACGT").build(),
				 null);
		 
	}
}
