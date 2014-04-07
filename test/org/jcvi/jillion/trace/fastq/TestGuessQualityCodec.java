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

import org.jcvi.jillion.trace.fastq.FastqQualityCodec;
import org.jcvi.jillion.trace.fastq.FastqUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestGuessQualityCodec {

	@Test
	public void sanger(){
		String quals = "@;7C9;A)565A;4..9;2;45,?@###########################################################################";
		assertEquals(FastqQualityCodec.SANGER, FastqUtil.guessQualityCodecUsed(quals));
	}
	
	@Test
	public void illumina(){
		String quals = "`a\\a`^\\a^ZZa[]^WB_aaaa^^a`]^a`^`aaa`]``aXaaS^a^YaZaTW]a_aPY\\_UVY[P_ZHQY_NLZUR[^UZ\\TZWT_[_VWMWaRFW]BB";
		assertEquals(FastqQualityCodec.ILLUMINA, FastqUtil.guessQualityCodecUsed(quals));
	}
	
	@Test
	public void solexa(){
		//added negative values to end of string
		String quals = "`a\\a`^\\a^ZZa[]^WB_aaaa^^a`]^a`^`aaa`]``aXaaS^a^YaZaTW]a_aPY\\_UVY[P_ZHQY_NLZUR[^UZ\\TZWT_[_VWMWaRFW]BBAQ?>=";
		assertEquals(FastqQualityCodec.SOLEXA, FastqUtil.guessQualityCodecUsed(quals));

	}
	
	@Test
	public void emptyStringReturnsNull(){
		assertNull(FastqUtil.guessQualityCodecUsed(""));
	}
}
