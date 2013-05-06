package org.jcvi.jillion.assembly.consed.phd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.trace.chromat.Chromatogram;
/**
 * {@code PhdBuilder} is a Builder that will create 
 * a {@link Phd} object using the given values.
 * @author dkatzel
 *
 */
public final class PhdBuilder implements Builder<Phd>{

	
	/**
     * The Position of the first peak in a Newbler created
     * fake 454 phd record.
     */
    private static final int DEFAULT_START_POSITION = 15;
    /**
     * The number of positions between every basecall
     * in a Newbler created fake 454 phd record.
     */
    private static final int DEFAULT_PEAK_SPACING = 19;
	
	private final String id;
	private final NucleotideSequence sequence;
	private final QualitySequence qualities;
	
	private PositionSequence peaks;
	private boolean fakePositions=false;
	private int fakePeakStartPosition = DEFAULT_START_POSITION;
	private int fakePeakSpacing=DEFAULT_PEAK_SPACING;
	
	private Map<String,String> comments= Collections.emptyMap();
	private List<PhdWholeReadItem> wrs = Collections.emptyList();
	private List<PhdReadTag> readTags = Collections.emptyList();
	/**
	 * Create a new {@link PhdBuilder} using the given 
	 * required values.
	 * @param id the id of the Phd to build; can not be null.
	 * @param sequence the {@link NucleotideSequence} of the Phd to build; can not be null.
	 * @param qualities the {@link QualitySequence} of the Phd to build; can not be null.
	 * @throws NullPointerException if any parameters are null.
	 * @throws IllegalArgumentException if the sequence.getLength() != qualities.getLength()
	 */
	public PhdBuilder(String id, NucleotideSequence sequence,
			QualitySequence qualities) {
		if(id==null){
			throw new NullPointerException("id can not be null");
		}
		if(sequence==null){
			throw new NullPointerException("sequence can not be null");
		}
		if(qualities==null){
			throw new NullPointerException("qualities can not be null");
		}
		if(sequence.getLength() !=qualities.getLength()){
			throw new IllegalArgumentException("sequence and qualities have unequal lengths");
		}
		this.id = id;
		this.sequence = sequence;
		this.qualities = qualities;
	}
	/**
	 * Create a new {@link PhdBuilder} which has its id, sequence,
	 * qualities and positions
	 * initialized to the values from the given {@link Chromatogram}.
	 * @param chromatogram the {@link Chromatogram} to initialize the builder values to;
	 * can not be null;
	 * @throws NullPointerException if chromatogram is null.
	 */
	public PhdBuilder(Chromatogram chromatogram){
		if(chromatogram ==null){
			throw new NullPointerException("chromatogram can not be null");
		}
		this.id = chromatogram.getId();
		this.sequence = chromatogram.getNucleotideSequence();
		this.qualities = chromatogram.getQualitySequence();
		this.peaks = chromatogram.getPositionSequence();
		
	}
	/**
	 * Create a new {@link PhdBuilder} which has its values
	 * initialized to the values from the given {@link Phd}.
	 * @param phd the phd to initialize the builder values to;
	 * can not be null;
	 * @throws NullPointerException if phd is null.
	 */
	public PhdBuilder(Phd phd){
		if(phd ==null){
			throw new NullPointerException("phd to copy can not be null");
		}
		this.id = phd.getId();
		this.sequence = phd.getNucleotideSequence();
		this.qualities = phd.getQualitySequence();
		this.peaks = phd.getPositionSequence();
		this.comments = new LinkedHashMap<String, String>(phd.getComments());
		this.wrs = new ArrayList<PhdWholeReadItem>(phd.getWholeReadItems());
		this.readTags = new ArrayList<PhdReadTag>(phd.getReadTags());
		
	}
	
	private PhdBuilder(PhdBuilder copy){
		this.id = copy.id;
		this.sequence = copy.sequence;
		this.qualities = copy.qualities;
		this.peaks = copy.peaks;
		this.comments = new LinkedHashMap<String, String>(copy.comments);
		this.wrs = new ArrayList<PhdWholeReadItem>(copy.wrs);
		this.readTags = new ArrayList<PhdReadTag>(copy.readTags);
		this.fakePeakSpacing = copy.fakePeakSpacing;
		this.fakePeakStartPosition = copy.fakePeakStartPosition;
		this.fakePositions = copy.fakePositions;
		
	}
	/**
	 * Create a Deep copy of this PhdBuilder.
	 * Any modifications to either the original builder
	 * or its copy will not affect the values of the other.
	 * @return a new {@link PhdBuilder} instance;
	 * will never be null.
	 */
	public PhdBuilder copy(){
		return new PhdBuilder(this);
	}
	/**
	 * Set the peaks of this {@link PhdBuilder}.
	 * If this method is not called, and this
	 * builder is not instructed to fake the peak data
	 * either by calling {@link #fakePeaks()} or {@link #fakePeaks(int, int)}
	 * then the built {@link Phd#getPositionSequence()} will return null.
	 * If this method is called AFTER a call to 
	 * {@link #fakePeaks()} or {@link #fakePeaks(int, int)}
	 * then the given peaks value will be used instead
	 * of fake peaks.
	 * @param peaks the {@link PositionSequence}
	 * of the peak positions to use for this phd;
	 * may be null to signify that the phd 
	 * should not have peaks.
	 * 
	 * @return this.
	 * @throws IllegalArgumentException if the given
	 * peaks value is not-null and does not have the same
	 * length as the sequence or qualities.
	 */
	public PhdBuilder peaks(PositionSequence peaks){
		if(peaks!=null && peaks.getLength() !=qualities.getLength()){
			throw new IllegalArgumentException("peaks and qualities have unequal lengths");
		}
		this.peaks = peaks;
		this.fakePositions = false;
		return this;
	}
	/**
	 * Auto-generated fake peak data instead of the given peak
	 * data set by {@link #peaks(PositionSequence)}. 
	 * The peaks will start and be equally spaced using the same
	 * format the Newbler uses to fake peak data
	 * for 454 traces in consed.
	 * @return this
	 */
	public PhdBuilder fakePeaks(){
		return fakePeaks(DEFAULT_START_POSITION, DEFAULT_PEAK_SPACING);
	}
	/**
	 * Auto-generated fake peak data instead of the given peak
	 * data set by {@link #peaks(PositionSequence)}. 
	 * The peaks will start at {@code firstPeakPosition}
	 * and be evenly spaced by {@code spacing} positions.
	 * @param firstPeakPosition the position of the first peak;
	 * must be >0.
	 * @param spacing the spacing between peaks to use;
	 * must be >0.
	 * @return this
	 * @throws IllegalArgumentException if any parameters
	 * are <=0
	 */
	public PhdBuilder fakePeaks(int firstPeakPosition, int spacing){		
		if(firstPeakPosition<1){
			throw new IllegalArgumentException("first peak position must be >0");
		}
		if(spacing<1){
			throw new IllegalArgumentException("peak spacing must be >0");
		}
		//TODO should we check that we don't get an overflow of peaks if seqlength is too large?
		fakePositions=true;
		this.fakePeakSpacing = spacing;
		this.fakePeakStartPosition = firstPeakPosition;
		//erase old peaks to free memory
		this.peaks =null;
		return this;
	}

	
	
	public PhdBuilder comments(Map<String,String> comments){
		this.comments = new LinkedHashMap<String, String>(comments);
		return this;
	}
	
	public PhdBuilder readTags(List<PhdReadTag> readTags){
		this.readTags = new ArrayList<PhdReadTag>(readTags);
		return this;
	}
	
	public PhdBuilder wholeReadItems(List<PhdWholeReadItem> wholeReadItems){
		this.wrs = new ArrayList<PhdWholeReadItem>(wholeReadItems);
		return this;
	}

	@Override
	public Phd build() {
		if(fakePositions){
			return new ArtificialPhd(id, sequence, qualities, 
					comments, wrs, readTags, fakePeakStartPosition, fakePeakSpacing);
		}
		return new DefaultPhd(id, sequence, qualities, 
				peaks, comments, wrs, readTags);
	}

}
