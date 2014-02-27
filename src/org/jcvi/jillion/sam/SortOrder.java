package org.jcvi.jillion.sam;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jcvi.jillion.sam.header.ReferenceSequence;
import org.jcvi.jillion.sam.header.SamHeader;

/**
 * Sorting Order of alignments in a SAM/BAM file.
 * @author dkatzel
 *
 */
public enum SortOrder{
	/**
	 * Sort order is unknown.
	 * This is the default if no sort order is specified.
	 */
	UNKNOWN,
	UNSORTED,
	/**
	 * Order by query name (read name).
	 */
	QUERY_NAME{

		@Override
		public Comparator<SamRecord> createComparator(SamHeader header) {
			return QUERY_NAME_COMPARATOR;
		}
		
	},
	/**
	 * The major sort key is the Reference Name,
	 * with the order defined by the order of the
	 * References defined in the sequence dictionary. 
	 * And the minor sort key is the {@link org.jcvi.jillion.sam.SamRecord#getStartOffset()}
	 * field.
	 * Alignments with the same reference and start offset,
	 * the order is arbitrary.
	 * All alignments that do not map to a reference follow
	 * alignments with some other value but otherwise are in arbitrary order.
	 */
	COORDINATE{

		@Override
		public Comparator<SamRecord> createComparator(SamHeader header) {
			return new CoordinateComparator(header);
		}
		
	};
	/**
	 * The Mapping of String names to our
	 * {@link SortOrder} objects used when parsing 
	 * SAM files.  Have to use a map because
	 * Jillion enum names don't match 100% to names used 
	 * in SAM format.
	 */
	private static final Map<String, SortOrder> NAME_MAP;
	
	/**
	 * {@link Comparator} used by {@link SortOrder#QUERY_NAME}.
	 */
	private static final Comparator<SamRecord> QUERY_NAME_COMPARATOR = new Comparator<SamRecord>(){

		@Override
		public int compare(SamRecord o1, SamRecord o2) {
			//nulls always go last
			if (o1 == null) {
                return 1;
            }
            if (o2 == null) {
                return -1;
            }
			return o1.getQueryName().compareTo(o2.getQueryName());
		}
		
	};
	
	static{
		NAME_MAP = new HashMap<String, SortOrder>();
		
		for(SortOrder s : values()){
			NAME_MAP.put(s.getEncodedName(), s);
		}
	}
	private final String encodedName;
	
	private SortOrder(){
		encodedName = name().replaceAll("_", "").toLowerCase(Locale.US);
	}
	
	/**
	 * Get the name of this SortOrder
	 * that is encoded in SAM and BAM header text.
	 * @return the name as a String,
	 * and may be different than {@link #name()}.
	 */
	public String getEncodedName() {
		return encodedName;
	}


	/**
	 * Parse the {@link SortOrder} from the given sort order name. current
	 * accepted values are:
	 * <ul>
	 * <li>unknown</li>
	 * <li>unsorted</li>
	 * <li>queryname</li>
	 * <li>coordinate</li>
	 * </ul>
	 * All other values will return null.
	 * @param sortOrder the sortOrder name to check.
	 * @return the {@link SortOrder} type if the sortOrder
	 * String EXACTLY matches the value list described above;
	 * otherwise {@code null}.
	 */
	public static SortOrder parseSortOrder(String sortOrder){
		return NAME_MAP.get(sortOrder);
	}
	/**
	 * Get the {@link Comparator} for this {@link SortOrder}
	 * which may be null if SortOrder is {@link SortOrder#UNKNOWN}
	 * or {@link SortOrder#UNSORTED}.
	 * Depending on the type, the returned Comparator may be
	 * a new object or a pointer to a pre-existing object.
	 * @return a {@link Comparator} or {@code null}
	 * if no comparator specified.
	 */
	public Comparator<SamRecord> createComparator(SamHeader header){
		//by default return null
		//let types override to return actual
		//comparator implementation.
		return null;
	}
	/**
	 * {@code CoordinateComparator}
	 * is a class that implements the sort rules
	 * required for {@link SortOrder#COORDINATE}.
	 * <p>
	 * Algorithm: Sort by reference order specified in
	 * {@link SamHeader#getReferenceSequences()}
	 * then by start position.  Unmapped records
	 * sort after mapped records.
	 * 
	 * The SAM file spec says that records that either have the same
	 * reference and start position or records that are unmapped are
	 * in an arbitrary order, but we will try to sort them by 
	 * query name.
	 * </p>
	 * @author dkatzel
	 *
	 */
	private static final class CoordinateComparator implements Comparator<SamRecord>, Serializable{

		private static final long serialVersionUID = -4315866144598924346L;
		
		private final List<String> referenceNames;
		
		public CoordinateComparator(SamHeader header){
			Collection<ReferenceSequence> refs = header.getReferenceSequences();
			referenceNames = new ArrayList<String>(refs.size());
			for(ReferenceSequence ref : refs){
				referenceNames.add(ref.getName());
			}
		}
		@Override
		public int compare(SamRecord o1, SamRecord o2) {
			//nulls always go last
			if (o1 == null) {
                return 1;
            }
            if (o2 == null) {
                return -1;
            }
			//sort by reference then by start position
			//if the read didn't map, then the alignments
			//go last
			//according to the SAM spec and those reads
			//are in "arbitrary order"
			//which means we can do whatever we want
			//therefore will sort by qname
			String ref1 =o1.getReferenceName();
			String ref2 =o2.getReferenceName();
			
			boolean ref1DidNotMap = SamRecord.UNAVAILABLE.equals(ref1);
			boolean ref2DidNotMap = SamRecord.UNAVAILABLE.equals(ref2);
			
			if(ref1DidNotMap && ref2DidNotMap){
				//according to the SAM spec and those reads
				//that don't map are sorted in
				//"arbitrary order"
				//which means we can do whatever we want
				//therefore will sort by qname
				return o1.getQueryName().compareTo(o2.getQueryName());
			}
			//unmapped reads go last
			if(ref1DidNotMap){
				//read 1 therefore will always be 
				//after read 2
				return 1;
			}
			if(ref2DidNotMap){
				//read 2 therefore will always be 
				//after read 1
				return -1;
			}
			//if we get this far,
			//then both reads mapped somewhere
			if(!ref1.equals(ref2)){
				int index1 =referenceNames.indexOf(ref1);
				int index2 =referenceNames.indexOf(ref2);
				if(index1 < index2){
					return -1;
				}
				return 1;
			}
			//same reference order by start position
			int startComp = compare(o1.getStartPosition(), o2.getStartPosition());
			if(startComp != 0){
				return startComp;
			}
			//according to the SAM spec and those reads
			//that don't map are sorted in
			//"arbitrary order"
			//which means we can do whatever we want
			//therefore will sort by qname
			return o1.getQueryName().compareTo(o2.getQueryName());
		}
			
		private static int  compare(int x, int y) {
			//taken from Java 7's compare since
			//it's not in Java 6
			return (x < y) ? -1 : ((x == y) ? 0 : 1);
		}
	}
}
