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

package org.jcvi.common.core.seq.read.trace.sanger.chromat;

import java.io.IOException;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jcvi.common.core.seq.read.Peaks;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.Chromatogram;
import org.jcvi.common.core.symbol.RunLengthEncodedGlyphCodec;
import org.jcvi.common.core.symbol.qual.EncodedQualitySequence;
import org.jcvi.common.core.symbol.qual.PhredQuality;
import org.jcvi.common.core.symbol.residue.nuc.DefaultNucleotideSequence;
import org.jcvi.io.fileServer.ResourceFileServer;
import org.jcvi.trace.sanger.chromatogram.Chromatogram2fasta;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
public class TestChromatogram2Fasta {

	ResourceFileServer RESOURCES = new ResourceFileServer(TestChromatogram2Fasta.class);
	
	Chromatogram chromo;
	String basecalls = "ACGT";
	byte[] quals = new byte[]{20,20,30,15};
	short[] peaks = new short[]{12,24,36,48};
	String id = "id";
	@Before
	public void setup(){
		chromo = createMock(Chromatogram.class);
		expect(chromo.getBasecalls()).andStubReturn(new DefaultNucleotideSequence(basecalls));
		expect(chromo.getQualities()).andStubReturn(new EncodedQualitySequence(
				RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE, PhredQuality.valueOf(quals)));
		expect(chromo.getPeaks()).andStubReturn(new Peaks(peaks));
		replay(chromo);
	}
	@Test
	public void writeAll3Fastas() throws IOException{
		ByteArrayOutputStream seqOut = new ByteArrayOutputStream();
		ByteArrayOutputStream qualOut = new ByteArrayOutputStream();
		ByteArrayOutputStream posOut = new ByteArrayOutputStream();		
		
		Chromatogram2fasta sut = new Chromatogram2fasta(seqOut, qualOut, posOut);
		sut.writeChromatogram(id, chromo);
		assertEquals(">id\n"+basecalls+"\n", new String(seqOut.toByteArray()));
		assertEquals(">id\n20 20 30 15\n", new String(qualOut.toByteArray()));
		assertEquals(">id\n0012 0024 0036 0048\n", new String(posOut.toByteArray()));
	}
	@Test
	public void qualAndPosAreNullShouldWriteSeqOnly() throws IOException{
		ByteArrayOutputStream seqOut = new ByteArrayOutputStream();	
		
		Chromatogram2fasta sut = new Chromatogram2fasta(seqOut, null, null);
		sut.writeChromatogram(id, chromo);
		assertEquals(">id\n"+basecalls+"\n", new String(seqOut.toByteArray()));
	}
	@Test
	public void seqAndPosAreNullShouldWriteQualOnly() throws IOException{
		ByteArrayOutputStream qualOut = new ByteArrayOutputStream();
		
		Chromatogram2fasta sut = new Chromatogram2fasta(null, qualOut, null);
		sut.writeChromatogram(id, chromo);
		assertEquals(">id\n20 20 30 15\n", new String(qualOut.toByteArray()));
	}
	@Test
	public void seqAndQualAreNullShouldWritePosOnly() throws IOException{
		ByteArrayOutputStream posOut = new ByteArrayOutputStream();
		
		Chromatogram2fasta sut = new Chromatogram2fasta(null, null, posOut);
		sut.writeChromatogram(id, chromo);
		assertEquals(">id\n0012 0024 0036 0048\n", new String(posOut.toByteArray()));
	}
	@Test(expected = NullPointerException.class)
	public void allWritersAreNullShouldThrowNPE(){
		new Chromatogram2fasta(null, null, null);
	}
}
