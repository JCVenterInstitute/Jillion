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
package org.jcvi.jillion.sam;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.Comparator;

import org.jcvi.jillion.sam.header.SamHeader;
import org.jcvi.jillion.sam.header.SamHeaderBuilder;
import org.jcvi.jillion.sam.header.SamReferenceSequenceBuilder;
import org.junit.Test;
public class TestSortOrder {

	
	private static final boolean DONT_CHECK_SELVES = false;
	@Test
	public void unsortedShouldReturnNullComparator(){
		assertNull(SortOrder.UNSORTED.createComparator(null));
	}
	
	@Test
	public void unknownShouldReturnNullComparator(){
		assertNull(SortOrder.UNKNOWN.createComparator(null));
	}
	
	@Test
	public void sortByQueryName(){
		
		SamRecordI first = createMock(SamRecordI.class);
		SamRecordI second = createMock(SamRecordI.class);
		
		expect(first.getQueryName()).andStubReturn("first");
		expect(second.getQueryName()).andStubReturn("second");
		
		replay(first,second);
		assertCorrectlySorted(SortOrder.QUERY_NAME, first, second);
	}
	
	private SamHeader createHeader(){
		return createHeaderWithReferences();
	}
	private SamHeader createHeaderWithReferences(String...references){
		SamHeaderBuilder builder = new SamHeaderBuilder();
		for(String ref : references){
			builder.addReferenceSequence(new SamReferenceSequenceBuilder(ref, Integer.MAX_VALUE).build());
			
		}
		return builder.build();
	}
	@Test
	public void sortByCoordinateBothReadsUnmappedShouldOrderByQueryName(){
		SamRecordI first = createMock(SamRecordI.class);
		SamRecordI second = createMock(SamRecordI.class);
		
		SamHeader header = createHeader();
		
		expect(first.getQueryName()).andStubReturn("first");
		expect(second.getQueryName()).andStubReturn("second");
		
		expect(first.getReferenceName()).andStubReturn(SamRecordI.UNAVAILABLE);
		expect(second.getReferenceName()).andStubReturn(SamRecordI.UNAVAILABLE);
		expect(first.mapped()).andStubReturn(false);
		expect(second.mapped()).andStubReturn(false);
		
		replay(first,second);
		assertCorrectlySorted(SortOrder.COORDINATE, first, second, header);
	}
	@Test
	public void sortByCoordinateMappedAlwaysLessThanUnMapped(){
		SamRecordI first = createMock(SamRecordI.class);
		SamRecordI second = createMock(SamRecordI.class);
		
		SamHeader header = createHeaderWithReferences("mapped");
		
		expect(first.mapped()).andStubReturn(true);
		expect(second.mapped()).andStubReturn(false);
		
		expect(first.getReferenceName()).andStubReturn("mapped");
		expect(second.getReferenceName()).andStubReturn(SamRecordI.UNAVAILABLE);
		
		
		replay(first,second);
		assertCorrectlySorted(SortOrder.COORDINATE, first, second, header, DONT_CHECK_SELVES);
	}
	@Test
	public void sortByCoordinateSameReferenceShouldSortByCoordinate(){
		SamRecordI first = createMock(SamRecordI.class);
		SamRecordI second = createMock(SamRecordI.class);
		
		SamHeader header = createHeaderWithReferences("sameRef");
		
		expect(first.getReferenceName()).andStubReturn("sameRef");
		expect(second.getReferenceName()).andStubReturn("sameRef");
		
		expect(first.mapped()).andStubReturn(true);
		expect(second.mapped()).andStubReturn(true);
		
		expect(first.getStartPosition()).andStubReturn(1);
		expect(second.getStartPosition()).andStubReturn(9999);
		
		
		replay(first,second);
		assertCorrectlySorted(SortOrder.COORDINATE, first, second,header, DONT_CHECK_SELVES);
	}
	
	@Test
	public void sortByCoordinateDifferentReferenceShouldSortByReferenceOrderInHeader(){
		SamRecordI first = createMock(SamRecordI.class);
		SamRecordI second = createMock(SamRecordI.class);
		//picked alphabetically backwards ref name
		//to make sure we are sorting in header order
		//not alphabetically
		SamHeader header = createHeaderWithReferences("zzzz", "aaaa");
		
		expect(first.getReferenceName()).andStubReturn("zzzzz");
		expect(second.getReferenceName()).andStubReturn("aaaa");
	
		expect(first.mapped()).andStubReturn(true);
		expect(second.mapped()).andStubReturn(true);
		
		replay(first,second);
		assertCorrectlySorted(SortOrder.COORDINATE, first, second,header, DONT_CHECK_SELVES);
	}
	@Test
	public void sortByCoordinateSameReferenceSamePositionShouldSortByQueryName(){
		SamRecordI first = createMock(SamRecordI.class);
		SamRecordI second = createMock(SamRecordI.class);
		
		SamHeader header = createHeaderWithReferences("sameRef");
		
		expect(first.getReferenceName()).andStubReturn("sameRef");
		expect(second.getReferenceName()).andStubReturn("sameRef");
		
		expect(first.getStartPosition()).andStubReturn(1234);
		expect(second.getStartPosition()).andStubReturn(1234);
		
		expect(first.getQueryName()).andStubReturn("first");
		expect(second.getQueryName()).andStubReturn("second");
		
		expect(first.mapped()).andStubReturn(true);
		expect(second.mapped()).andStubReturn(true);
		
		replay(first,second);
		assertCorrectlySorted(SortOrder.COORDINATE, first, second,header);
	}
	
	private void assertCorrectlySorted(SortOrder order, SamRecordI first, SamRecordI second){
		assertCorrectlySorted(order, first, second, null);
	}

	private void assertCorrectlySorted(SortOrder order, SamRecordI first, SamRecordI second, SamHeader header){
		assertCorrectlySorted(order, first, second, header, true);
	}
	private void assertCorrectlySorted(SortOrder order, SamRecordI first, SamRecordI second, SamHeader header,boolean checkSelves){
		Comparator<SamRecordI> comparator = order.createComparator(header);
		if(checkSelves){
			assertEquals("same record should sort equal to itself", 0, comparator.compare(first, first));
			assertEquals("same record should sort equal to itself", 0, comparator.compare(second, second));
		}
		assertThat(comparator.compare(first, second), is(lessThan(0)));
		assertThat(comparator.compare(second, first), is(greaterThan(0)));

		
	}
	
	
	@Test
	public void parseSortOrder(){
		assertNull(SortOrder.parseSortOrder("invalid name"));
		assertThat(SortOrder.parseSortOrder("unknown"), is(SortOrder.UNKNOWN));
		assertThat(SortOrder.parseSortOrder("unsorted"), is(SortOrder.UNSORTED));
		assertThat(SortOrder.parseSortOrder("queryname"), is(SortOrder.QUERY_NAME));
		assertThat(SortOrder.parseSortOrder("coordinate"), is(SortOrder.COORDINATE));
	}
	
	@Test
	public void getEncodedName(){		
		assertThat("unknown", is(SortOrder.UNKNOWN.getEncodedName()));
		assertThat("unsorted", is(SortOrder.UNSORTED.getEncodedName()));
		assertThat("queryname", is(SortOrder.QUERY_NAME.getEncodedName()));
		assertThat("coordinate", is(SortOrder.COORDINATE.getEncodedName()));
		
	}
}
