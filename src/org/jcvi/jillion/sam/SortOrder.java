package org.jcvi.jillion.sam;

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
		public Comparator<SamRecord> getComparator(SamHeader header) {
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
		public Comparator<SamRecord> getComparator(SamHeader header) {
			return new CoordinateComparator(header);
		}
		
	};
	private static final Map<String, SortOrder> NAME_MAP;
	
	
	private static final Comparator<SamRecord> QUERY_NAME_COMPARATOR = new Comparator<SamRecord>(){

		@Override
		public int compare(SamRecord o1, SamRecord o2) {
			return o1.getQueryName().compareTo(o2.getQueryName());
		}
		
	};
	
	static{
		NAME_MAP = new HashMap<String, SortOrder>();
		
		for(SortOrder s : values()){
			NAME_MAP.put(s.name().replaceAll("_", "").toLowerCase(Locale.US), s);
		}
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
	 * @return a {@link Comparator} or {@code null}
	 * if no comparator specified.
	 */
	public Comparator<SamRecord> getComparator(SamHeader header){
		return null;
	}
	
	private static final class CoordinateComparator implements Comparator<SamRecord>{

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
			int startComp = Integer.compare(o1.getStartPosition(), o2.getStartPosition());
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
			
		
	}
}
