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

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@code IlluminaUtil} is a utility class for working with Illumina data.
 * @author dkatzel
 *
 *
 */
public final class IlluminaUtil {

	private static final Pattern NEW_NAME_PATTERN = Pattern.compile(
			"^([A-Za-z0-9_]+):(\\d+):([A-Za-z0-9]+):(\\d+):(\\d+):(\\d+):(\\d+)((:[ACGTN]+\\+[ACGTN]+)? (\\d):([Y|N]):(\\d+):([ACGTN0-9]+))?$");

	private static final Pattern NAME_PATTERN = Pattern.compile(
            "^(SOLEXA\\d+).*:(\\d+):(\\d+):(\\d+):(\\d+)#(\\D+)?(\\d+)?\\/(\\d+)$");
    /**
     * Name pattern for Casava 1.8 which has different format
     * and space before mate info.
     */
    private static final Pattern CASAVA_1_8_PATTERN = Pattern.compile(
    "^(\\S+):(\\d+):(\\S+):(\\d+):(\\d+)#(\\D+)?(\\d+)?\\/(\\d+)$");
    
    
    private IlluminaUtil(){
    	//can not instantiate
    }
    private static IlluminaMatcher createMatcherOrError(String readId) {
    	IlluminaMatcher matcher = createMatcher(readId);
    	if(matcher ==null) {
    		throwNotValidReadId(readId);
    	}
    	return matcher;
    }
    private static IlluminaMatcher createMatcher(String readId) {
    	Objects.requireNonNull(readId);
    	Matcher m = NEW_NAME_PATTERN.matcher(readId);
    	if(m.matches()) {
    		return new NewMatcher(m);
    	}
    	m = NAME_PATTERN.matcher(readId);
    	if(m.matches()) {
    		return new LegacyMatcher(m);
    	}
    	m = CASAVA_1_8_PATTERN.matcher(readId);
    	if(m.matches()) {
    		return new CasavaMatcher(m);
    	}
    	
    	
    	return null;
    }
    public static interface IlluminaMatcher{
    	String getFlowcellId();
    	String getInstrumentName();
    	int getMultiplexIndex();
    	int getPairNumber();
    	String getRunId();
    	int getTileNumber();
    	int getXClusterCoordinate();
    	int getYClusterCoordinate();
    }
    private static class NewMatcher extends CasavaMatcher{

		public NewMatcher(Matcher matcher) {
			super(matcher);
		}
		@Override
		public String getFlowcellId() {
			return matcher.group(3);
		}

		@Override
		public String getInstrumentName() {
			 return matcher.group(1);
		}

		@Override
		public int getMultiplexIndex() {
			return Integer.parseInt(matcher.group(7));
		}

		@Override
		public int getPairNumber() {
			return Integer.parseInt(matcher.group(8));
		}

		@Override
		public String getRunId() {
			return matcher.group(2);
		}

		@Override
		public int getTileNumber() {
			return Integer.parseInt(matcher.group(5));
		}

		@Override
		public int getXClusterCoordinate() {
			return Integer.parseInt(matcher.group(6));
		}

		@Override
		public int getYClusterCoordinate() {
			return Integer.parseInt(matcher.group(7));
		}
    	
    	
    }
    private static class CasavaMatcher extends LegacyMatcher{

		public CasavaMatcher(Matcher matcher) {
			super(matcher);
		}
		@Override
		public String getFlowcellId() {
			return matcher.group(3);
		}
    	
    }
    private static class LegacyMatcher implements IlluminaMatcher{
    	protected final Matcher matcher;
    	

		public LegacyMatcher(Matcher matcher) {
			this.matcher = matcher;
		}

		@Override
		public String getFlowcellId() {
			return matcher.group(2);
		}

		@Override
		public String getInstrumentName() {
			 return matcher.group(1);
		}

		@Override
		public int getMultiplexIndex() {
			return Integer.parseInt(matcher.group(7));
		}

		@Override
		public int getPairNumber() {
			return Integer.parseInt(matcher.group(8));
		}

		@Override
		public String getRunId() {
			return matcher.group(2);
		}

		@Override
		public int getTileNumber() {
			return Integer.parseInt(matcher.group(3));
		}

		@Override
		public int getXClusterCoordinate() {
			return Integer.parseInt(matcher.group(4));
		}

		@Override
		public int getYClusterCoordinate() {
			return Integer.parseInt(matcher.group(5));
		}
    	
    }
    
    /**
     * Tests to see if the given read id matches the 
     * illumina naming patterns.
     * @param readId the read id to check.
     * @return {@code true} if the read id is matches
     * a valid illumina read pattern;
     * {@code false} otherwise.
     */
    public static boolean isIlluminaRead(String readId){
    	IlluminaMatcher matcher = createMatcher(readId);
    	return matcher !=null;
        
    }
	public static boolean isCasava18Read(String readId) {
		return CASAVA_1_8_PATTERN.matcher(readId).matches();
	}
    /**
     * Gets the run id from the given read id.
     * @param illuminaReadId the illumina read id to parse.
     * @return the run id as a String.
     * @throws IllegalArgumentException if the given read id is not a valid
     * Illumina Read ID.
     * @throws NullPointerException if the given id is null.
     */
    public static String getRunId(String illuminaReadId){
    	return createMatcherOrError(illuminaReadId)
    			.getRunId();
    }
    /**
     * Throw an unchecked exception that the given read is is not
     * a valid illumina id.
     * @param readId
     * @return This method will never return since it will
     * always throw an exception, however we just tell the compiler
     * that it will return a string so this method can be used
     * in various places as the last line in a method that needs a return value.
     */
	private static String throwNotValidReadId(String readId) {
		throw new IllegalArgumentException("is not an illumina read id "+readId);
	}
    /**
     * Gets the unique instrument name from the given read id.
     * @param illuminaReadId the illumina read id to parse.
     * @return the instrument name as a String.
     * @throws IllegalArgumentException if the given read id is not a valid
     * Illumina Read ID.
     * @throws NullPointerException if the given id is null.
     */
    public static final String getInstrumentName(String illuminaReadId){
    	return createMatcherOrError(illuminaReadId)
    			.getInstrumentName();
    }
    /**
     * Gets the flowcell lane from the given read id.
     * @param illuminaReadId the illumina read id to parse.
     * @return the flowcell lane as an int.
     * @throws IllegalArgumentException if the given read id is not a valid
     * Illumina Read ID.
     * @throws NullPointerException if the given id is null.
     */
    public static final String getFlowcellId(String illuminaReadId){
    	return createMatcherOrError(illuminaReadId)
    			.getFlowcellId();

        
    }
    
    /**
     * @param matcher
     * @return
     */
    private static String getCasava18FlowCellId(Matcher matcher) {
        return matcher.group(3);
    }

    private static String getLegacyFlowCellId(Matcher legacyMatcher){
        return legacyMatcher.group(2);
    }
    
    /**
     * Gets the tile number within the flowcell lane from the given read id.
     * @param illuminaReadId the illumina read id to parse.
     * @return the tile number within the flowcell lane as an int.
     * @throws IllegalArgumentException if the given read id is not a valid
     * Illumina Read ID.
     * @throws NullPointerException if the given id is null.
     */
    public static final int getTileNumber(String illuminaReadId){
    	return createMatcherOrError(illuminaReadId)
    			.getTileNumber();
    }
    /**
     * Gets the x-coordinate of the cluster within the tile from the given read id.
     * @param illuminaReadId the illumina read id to parse.
     * @return the x-coordinate of the cluster within the tile as an int.
     * @throws IllegalArgumentException if the given read id is not a valid
     * Illumina Read ID.
     * @throws NullPointerException if the given id is null.
     */
    public static final int getXClusterCoordinate(String illuminaReadId){
    	return createMatcherOrError(illuminaReadId)
    			.getXClusterCoordinate();
    }
    
    /**
     * Gets the y-coordinate of the cluster within the tile from the given read id.
     * @param illuminaReadId the illumina read id to parse.
     * @return the y-coordinate of the cluster within the tile as an int.
     * @throws IllegalArgumentException if the given read id is not a valid
     * Illumina Read ID.
     * @throws NullPointerException if the given id is null.
     */
    public static final int getYClusterCoordinate(String illuminaReadId){
    	return createMatcherOrError(illuminaReadId)
    			.getYClusterCoordinate();
    }
    
    /**
     * Gets the multiplex index number for the sample.  An index of 
     * {@code 0} means not indexed.
     * @param illuminaReadId the illumina read id to parse.
     * @return multiplex index number as an int.
     * @throws IllegalArgumentException if the given read id is not a valid
     * Illumina Read ID.
     * @throws NullPointerException if the given id is null.
     */
    public static final int getMultiplexIndex(String illuminaReadId){
    	return createMatcherOrError(illuminaReadId)
    			.getMultiplexIndex();
    }
    /**
     * Gets the pair number if this read is paired-end or mate-paired.
     * @param illuminaReadId the illumina read id to parse.
     * @return multiplex index number as an int.
     * @throws IllegalArgumentException if the given read id is not a valid
     * Illumina Read ID or is not a member of a pair.
     * @throws NullPointerException if the given id is null.
     */
    public static final int getPairNumber(String illuminaReadId){
    	return createMatcherOrError(illuminaReadId)
    			.getPairNumber();

    }
}
