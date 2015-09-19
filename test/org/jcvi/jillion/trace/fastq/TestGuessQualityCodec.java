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
package org.jcvi.jillion.trace.fastq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
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
	
	
	@Test
	public void qvsAllSanger30(){
		//sanger qv 30 = "?" = 30 +33 = 63 which is less than illumina offset

		String quals = "??????????";
		assertEquals(FastqQualityCodec.SANGER, FastqUtil.guessQualityCodecUsed(quals));
	}
}
