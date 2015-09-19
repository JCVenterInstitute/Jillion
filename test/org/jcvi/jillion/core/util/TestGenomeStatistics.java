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
package org.jcvi.jillion.core.util;

import static org.easymock.EasyMock.createMock;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import org.jcvi.jillion.core.util.GenomeStatistics.GenomeStatisticsBuilder;
import org.junit.Test;

public class TestGenomeStatistics {

	@Test
	public void emptyCollector(){
		assertFalse(GenomeStatistics.n50(IntStream.empty()).isPresent());
	}
	
	@Test
	public void allLengthsNotEnoughToMeetPercentageValueShouldReturnEmpty(){
		
		OptionalInt value = Arrays.asList(1,2,3)
										.stream()
										.collect(GenomeStatistics.ng50Collector(100));
						
		assertFalse(value.isPresent());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void negativeGenomeSizeShouldThrowIllegalArgumentException(){
		GenomeStatistics.ng50Builder(-1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void negativePercentShouldThrowIllegalArgumentException(){
		GenomeStatistics.nXBuilder(-1);
	}
	@Test(expected = IllegalArgumentException.class)
	public void onehundredPercentShouldThrowIllegalArgumentException(){
		GenomeStatistics.nXBuilder(1);
	}
	@Test(expected = IllegalArgumentException.class)
	public void zeroLengthNGCollectorShouldThrowIllegalArgumentException(){
		GenomeStatistics.ngXCollector(0, .5D);
	}
	@Test(expected = IllegalArgumentException.class)
	public void negativePercentNGCollectorShouldThrowIllegalArgumentException(){
		GenomeStatistics.ngXCollector(1000, -1);
	}
	@Test(expected = IllegalArgumentException.class)
	public void onehundredPercentNGCollectorShouldThrowIllegalArgumentException(){
		GenomeStatistics.ngXCollector(1000, 1);
	}
	
	
	@Test(expected = IllegalArgumentException.class)
	public void zeroLengthNGShouldThrowIllegalArgumentException(){
		GenomeStatistics.ngXBuilder(0, .5D);
	}
	@Test(expected = IllegalArgumentException.class)
	public void negativePercentNGShouldThrowIllegalArgumentException(){
		GenomeStatistics.ngXBuilder(1000, -1);
	}
	@Test(expected = IllegalArgumentException.class)
	public void onehundredPercentNGShouldThrowIllegalArgumentException(){
		GenomeStatistics.ngXBuilder(1000, 1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void negativeLengthAddedShouldThrowException(){
		GenomeStatistics.n50Builder()
						.add(-1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void lengthLargerThanIntMaxAddedShouldThrowException(){
		
		long value = Integer.MAX_VALUE + 1L;
		
		GenomeStatistics.n50Builder()
						.add(value);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void lengthSmallerThanIntMinAddedShouldThrowException(){
		
		long value = Integer.MIN_VALUE - 1L;
		
		GenomeStatistics.n50Builder()
						.add(value);
	}
	
	@Test(expected = IllegalStateException.class)
	public void mergingNonBuilderShouldThrowException(){
		GenomeStatisticsBuilder builder = GenomeStatistics.n50Builder();
		
		builder.merge(createMock(GenomeStatisticsBuilder.class));
	}
	
}
