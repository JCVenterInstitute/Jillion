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

import org.jcvi.jillion.sam.header.ReferenceSequence;
import org.jcvi.jillion.sam.header.SamHeader;
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
		
		SamRecord first = createMock(SamRecord.class);
		SamRecord second = createMock(SamRecord.class);
		
		expect(first.getQueryName()).andStubReturn("first");
		expect(second.getQueryName()).andStubReturn("second");
		
		replay(first,second);
		assertCorrectlySorted(SortOrder.QUERY_NAME, first, second);
	}
	
	private SamHeader createHeader(){
		return createHeaderWithReferences();
	}
	private SamHeader createHeaderWithReferences(String...references){
		SamHeader.Builder builder = new SamHeader.Builder();
		for(String ref : references){
			builder.addReferenceSequence(new ReferenceSequence.Builder(ref, Integer.MAX_VALUE).build());
			
		}
		return builder.build();
	}
	@Test
	public void sortByCoordinateBothReadsUnmappedShouldOrderByQueryName(){
		SamRecord first = createMock(SamRecord.class);
		SamRecord second = createMock(SamRecord.class);
		
		SamHeader header = createHeader();
		
		expect(first.getQueryName()).andStubReturn("first");
		expect(second.getQueryName()).andStubReturn("second");
		
		expect(first.getReferenceName()).andStubReturn(SamRecord.UNAVAILABLE);
		expect(second.getReferenceName()).andStubReturn(SamRecord.UNAVAILABLE);
		expect(first.mapped()).andStubReturn(false);
		expect(second.mapped()).andStubReturn(false);
		
		replay(first,second);
		assertCorrectlySorted(SortOrder.COORDINATE, first, second, header);
	}
	@Test
	public void sortByCoordinateMappedAlwaysLessThanUnMapped(){
		SamRecord first = createMock(SamRecord.class);
		SamRecord second = createMock(SamRecord.class);
		
		SamHeader header = createHeaderWithReferences("mapped");
		
		expect(first.mapped()).andStubReturn(true);
		expect(second.mapped()).andStubReturn(false);
		
		expect(first.getReferenceName()).andStubReturn("mapped");
		expect(second.getReferenceName()).andStubReturn(SamRecord.UNAVAILABLE);
		
		
		replay(first,second);
		assertCorrectlySorted(SortOrder.COORDINATE, first, second, header, DONT_CHECK_SELVES);
	}
	@Test
	public void sortByCoordinateSameReferenceShouldSortByCoordinate(){
		SamRecord first = createMock(SamRecord.class);
		SamRecord second = createMock(SamRecord.class);
		
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
		SamRecord first = createMock(SamRecord.class);
		SamRecord second = createMock(SamRecord.class);
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
		SamRecord first = createMock(SamRecord.class);
		SamRecord second = createMock(SamRecord.class);
		
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
	
	private void assertCorrectlySorted(SortOrder order, SamRecord first, SamRecord second){
		assertCorrectlySorted(order, first, second, null);
	}

	private void assertCorrectlySorted(SortOrder order, SamRecord first, SamRecord second, SamHeader header){
		assertCorrectlySorted(order, first, second, header, true);
	}
	private void assertCorrectlySorted(SortOrder order, SamRecord first, SamRecord second, SamHeader header,boolean checkSelves){
		Comparator<SamRecord> comparator = order.createComparator(header);
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
