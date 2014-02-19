package org.jcvi.jillion.sam;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.jcvi.jillion.core.Range;
/**
 * {@code SamUtil} is a utility class
 * for working with Sam or Bam encoded data.
 * @author dkatzel
 *
 */
public final class SamUtil {
	/**
	 * The max bin number allowed in a BAM file.
	 * Should be 37449.
	 */
	private static final int MAX_BIN = (((1<<18)-1)/7);
	
	/**
	 * From BAM specification section 5.1.1:
	 * "In BAM, each bin may span 
	 * 2<sup>29</sup>, 2<sup>26</sup>, 2<sup>23</sup>,
	 *  2<sup>20</sup>, 2<sup>17</sup> or 2<sup>14</sup> bp. 
	 *  Bin 0 spans a 512Mbp region,
	 *  bins 1-8 span 64Mbp, 
	 *  9-72 8Mbp, 
	 *  73-584 1Mbp, 
	 *  585-4680 128Kbp 
	 *  and bins 4681-37449 span 16Kbp regions."
	 *  <p/>
	 *  Each of those specified bin ranges
	 *  is a smaller range of the alignment
	 *  and can be viewed as an R-tree.  Each level
	 *  in the tree is a more fine grained sub region
	 *  of the alignment.
	 *  <p/>
	 *  <table border = "1" style="text-align:center;">
	 *  <tbody>
	 *  <tr><td colspan = "64">0-512 Mbp<td></tr>
	 *  <tr><td  colspan = "8">0-64 Mbp</td><td  colspan = "8">64-128 Mbp</td><td  colspan = "8">128-192 Mbp</td><td  colspan = "8">192-256 Mbp</td><td  colspan = "8">256- 320 Mbp</td><td  colspan = "8">320-384 Mbp</td><td  colspan = "8">384-448 Mbp</td><td  colspan = "8">448-512 Mbp</td></tr>
	 *  <tr><td >0-8 Mbp</td ><td >8-16 Mbp</td><td >16-24 Mbp</td><td >24-32 Mbp</td><td >32-40 Mbp</td><td >40-48 Mbp</td><td >48-56 Mbp</td><td >56-64 Mbp</td><td >64-72 Mbp</td><td>...</td></tr>
		 
	 *  </tbody>
	 *  </table>
	 *  <br/>
	 *  And each 8 Mb block has the following levels:
	 *  <br/>
	 *  <table border = "1" style="text-align:center;">
	 *  <tbody>
		 * 	<tr><td colspan = "512">0-8 Mbp</td colspan = "512"><td colspan = "512">8-16 Mbp</td><td colspan = "512">16-24 Mbp</td><td colspan = "512">24-32 Mbp</td><td colspan = "512">32-40 Mbp</td><td colspan = "512">40-48 Mbp</td><td colspan = "512">48-56 Mbp</td><td colspan = "512">56-64 Mbp</td><td colspan = "512">64-72 Mbp</td><td>...</td></tr>
		 *  <tr><td colspan = "64">0-1 Mbp</td><td colspan = "64">1-2 Mbp</td><td colspan = "64">2-3 Mbp</td><td colspan = "64">3-4 Mbp</td><td colspan = "64">4-5 Mbp</td><td colspan = "64">5-6 Mbp</td><td colspan = "64">6-7 Mbp</td><td colspan = "64">7-8 Mbp</td><td colspan = "64">8-9 Mbp</td><td>...</td></tr>
		 *  <tr><td colspan = "8">0-128 Kbp</td><td colspan = "8">128-256 Kbp</td><td colspan = "8">256-384 Kbp</td><td colspan = "8">384-512 Kbp</td><td colspan = "8">512-640 Kbp</td><td colspan = "8">640-768 Kbp</td><td colspan = "8">768-896 Kbp</td><td colspan = "8">896-1024 Kbp</td><td colspan = "8">1024-1152 Kbp</td><td  colspan = "8">...</td></tr>
		 *  <tr><td>0-16 Kbp</td><td>16-32 Kbp</td><td>32-48 Kbp</td><td>48-64 Kbp</td><td>64-80 Kbp</td><td>80-96 Kbp</td><td>96-114 Kbp</td><td>114-128 Kbp</td><td>128 -144 Kbp</td><td>...</td></tr>
		 *  
		 *  </tbody>
	 *  </table>
	 *  <p/>
	 *  
	 *  To simplify which bin offsets are which levels in the tree
	 *  we can just store the initial bin for each level.
	 *  
	 */
	private static final int[] BIN_TREE_LEVEL_OFFSETS = new int[]{1, 9, 73, 585, 4681};
	
	private static final int[] BIN_TREE_LEVEL_SHIFTS = new int[]{26,23, 20,  17, 14};
	
	private static ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat> () {

		  @Override
		  public DateFormat get() {
		   return super.get();
		  }

		  @Override
		  protected DateFormat initialValue() {
		   return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		  }

		  @Override
		  public void remove() {
		   super.remove();
		  }

		  @Override
		  public void set(DateFormat value) {
		   super.set(value);
		  }

		 };
	private SamUtil(){
		//can not instantiate
	}
	/**
	 * Assert that the first key character is a valid letter
	 * [A-Za-z] and that the second key character is a valid letter or digit
	 * [A-Za-z0-9].
	 * @param key1 the first letter in the key.
	 * @param key2 the second letter in the key.
	 * @return {@code true} if valid;{@code false}
	 * otherwise.
	 */
	public static boolean isValidKey(char key1, char key2) {
		return assertValid1(key1)  && assertValid2(key2);
			
	}
	/**
	 * Assert that the first key character is a valid letter
	 * [A-Za-z].
	 * @param c
	 * @return {@code true} if valid;{@code false}
	 * otherwise.
	 */
	private static boolean assertValid1(char c) {
		//A-Zz-z
		if(c<65 || c>122){
			return false;
		}
		//check for special chars between Z-a
		//[91-96] invalid
		if(c > 90 && c<97){
			return false;
		}
		return true;
	}
	/**
	 * Assert that the second key character is a valid letter or digit
	 * [A-Za-z0-9].
	 * @param c
	 * @return {@code true} if valid;{@code false}
	 * otherwise.
	 */
	private static boolean assertValid2(char c) {
		if(c >=48 && c<=57){
			//digit is good
			return true;
		}
		return assertValid1(c);
		
	}
	
	public static Date toDate(String dateString) throws ParseException{
		return dateFormat.get().parse(dateString);
	}
	
	public static String formatIsoDate(Date date){
		return dateFormat.get().format(date);
	}
	/**
	 *  Calculate the BAM bin for a given Range
	 *  same as
	 *  {@code computeBinFor((int)range.getBegin(), (int)range.getEnd()+1); }
	 * @param range the alignment range, can not be null.
	 * @return the BAM bin.
	 * @see #computeBinFor(int,int)
	 * @throws NullPointerException if range is null.
	 */
	public static int computeBinFor(Range range){
		return computeBinFor((int)range.getBegin(), (int)range.getEnd()+1);
	}
	/**
	 * Calculate the BAM bin for a given alignment
	 * covering [begin, end) zero based.
	 * @param begin the begin coordinate zero-based
	 * inclusive.
	 * @param endExclusive the end coordinate
	 * zero based EXCLUSIVE.
	 * @return the BAM bin.
	 * @throws IllegalArgumentException if endExclusive <= begin
	 */
	public static int computeBinFor(int begin, int endExclusive){
		if(endExclusive <= begin){
			throw new IllegalArgumentException("end must be > begin");
		}
		int end = endExclusive-1;
		int beg = begin;
		//taken directly from C source example in SAMv1 file spec
		//with some slight re-formatting
		//and moving all magic numbers to either
		//constants or arrays with intent revealing
		//names and comments.
		//TODO this refactoring
		//may make the actual computation slower
		//than hardcoding magic numbers
		//if it becomes a bottleneck
		//replace with C code from spec.
		
		//iterate backwards to find the largest bin values
		//(and therefore the smallest lenghts) first		
		int numberOfLevels = BIN_TREE_LEVEL_SHIFTS.length;
		for(int level = numberOfLevels -1; level >=0; level--){
			final int shiftAmount = BIN_TREE_LEVEL_SHIFTS[level];
			if (beg>>shiftAmount == end>>shiftAmount){
				return ((1 << (3 * (level +1))) - 1)/7 + (beg>>shiftAmount);
			}
		}
		//if we've gotten this far
		//then we must be in bin 0
		return 0;
		
		
	}
	/**
	 * Get an array which contains
	 * all the potential BAM bins that the given alignment range
	 * overlaps.  The contents in the array is not necessarily 
	 * contigious values and the actual bins used may only be the first
	 * few elements in this array depending on the length of the reference.
	 * @param range the alignment range; can not be null.
	 * @return a new array will never be null and will
	 * always have at least one element [0] = 0 (which is bin 0).
	 * @throws NullPointerException if range is null.
	 */
	public static int[] getCandidateOverlappingBins(Range range){
		return getCandidateOverlappingBins((int)range.getBegin(), (int)range.getEnd() +1);
	}
	/**
	 * Get an array which contains
	 * all the potential BAM bins that the given alignment range
	 * overlaps.  The contents in the array is not necessarily 
	 * contigious values and the actual bins used may only be the first
	 * few elements in this array depending on the length of the reference.
	 * @param begin the begin coordinate zero-based
	 * inclusive.
	 * @param endExclusive the end coordinate
	 * zero based EXCLUSIVE.
	 * @return a new array will never be null and will
	 * always have at least one element [0] = 0 (which is bin 0).
	 */
	public static int[] getCandidateOverlappingBins(int begin, int endExclusive){
		if(endExclusive <= begin){
			throw new IllegalArgumentException("end must be > begin");
		}
		int end = endExclusive-1;
		int beg = begin;
		//taken directly from C source example in SAMv1 file spec
		//with some slight re-formatting 
		//and minor conversion to more Java friendly syntax	
		//and moving all magic numbers to either
		//constants or arrays with intent revealing
		//names and comments.
		//TODO this refactoring
		//may make the actual computation slower
		//than hardcoding magic numbers
		//if it becomes a bottleneck
		//replace with C code from spec.
		int[] list = new int[MAX_BIN];
		
		int i=0, k;
		//bin 0 is always a candidate since it covers everything
		list[i++]=0;
		
		for(int level = 0; level < BIN_TREE_LEVEL_OFFSETS.length; level++){
			int shiftAmount = BIN_TREE_LEVEL_SHIFTS[level];
			int initialK = BIN_TREE_LEVEL_OFFSETS[level];
			for (k = initialK + (beg>>shiftAmount); k <= initialK + (end>>shiftAmount); ++k){
				list[i++] = k;
			}
		}		
		//at this point 
		//i is the length of elements
		//we populated in the array
		return Arrays.copyOf(list, i);
	}
}
