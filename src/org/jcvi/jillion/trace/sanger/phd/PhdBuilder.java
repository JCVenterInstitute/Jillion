package org.jcvi.jillion.trace.sanger.phd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.jcvi.jillion.core.util.Builder;
import org.jcvi.jillion.trace.sanger.PositionSequence;

public final class PhdBuilder implements Builder<Phd>{

	
	/**
     * The Position of the first peak in a Newbler created
     * fake 454 phd record.
     */
    private static final int NEWBLER_454_START_POSITION = 15;
    /**
     * The number of positions between every basecall
     * in a Newbler created fake 454 phd record.
     */
    private static final int NEWBLER_454_PEAK_SPACING = 19;
	
	private final String id;
	private final NucleotideSequence sequence;
	private final QualitySequence qualities;
	
	private PositionSequence peaks;
	private boolean fakePositions=false;
	private int fakePeakStartPosition = NEWBLER_454_START_POSITION;
	private int fakePeakSpacing=NEWBLER_454_PEAK_SPACING;
	
	private Map<String,String> comments= Collections.emptyMap();
	private List<PhdWholeReadItem> wrs = Collections.emptyList();
	private List<PhdReadTag> readTags = Collections.emptyList();
	
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
			throw new IllegalStateException("sequence and qualities have unequal lengths");
		}
		this.id = id;
		this.sequence = sequence;
		this.qualities = qualities;
	}

	public PhdBuilder peaks(PositionSequence peaks){
		if(peaks!=null){
			if(peaks.getLength() !=qualities.getLength()){
				throw new IllegalStateException("peaks and qualities have unequal lengths");
			}
		}
		this.peaks = peaks;
		
		return this;
	}
	
	public PhdBuilder fakePeaks(){
		verifyDoNotHaveActualPeaks();
		fakePositions=true;
		return this;
	}
	public PhdBuilder fakePeaks(int firstPeakPosition, int spacing){		
		verifyDoNotHaveActualPeaks();
		if(firstPeakPosition<1){
			throw new IllegalArgumentException("first peak position must be >0");
		}
		if(spacing<1){
			throw new IllegalArgumentException("peak spacing must be >0");
		}
		//TODO should we check that we don't get an overflow of peaks if seqlength is loo large?
		fakePositions=true;
		this.fakePeakSpacing = spacing;
		this.fakePeakStartPosition = firstPeakPosition;
		return this;
	}

	private void verifyDoNotHaveActualPeaks() {
		if(peaks !=null){
			throw new IllegalStateException("can not fake peaks when real peaks have been provided");
		}
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
