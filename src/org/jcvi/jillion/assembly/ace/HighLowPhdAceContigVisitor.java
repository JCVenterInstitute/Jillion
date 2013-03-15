package org.jcvi.jillion.assembly.ace;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jcvi.jillion.assembly.ace.consed.ConsedUtil;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.qual.PhredQuality;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.core.util.MapUtil;
import org.jcvi.jillion.trace.sanger.phd.ArtificialPhd;
import org.jcvi.jillion.trace.sanger.phd.Phd;

public class HighLowPhdAceContigVisitor extends AbstractAceContigVisitor{

	public static final PhredQuality DEFAULT_LOW_QUALITY = PhredQuality.valueOf(15);
	public static final PhredQuality DEFAULT_HIGH_QUALITY = AceFileUtil.ACE_DEFAULT_HIGH_QUALITY_THRESHOLD;
	   
	private final Map<String, Phd> phds;
    private final Map<String, Direction> directions;
    
    private final byte lowQuality;
    private final byte highQuality;
    
    public HighLowPhdAceContigVisitor(int expectedNumberOfReads){
    	this(expectedNumberOfReads, DEFAULT_LOW_QUALITY.getQualityScore(), DEFAULT_HIGH_QUALITY.getQualityScore());
    }
    
    public HighLowPhdAceContigVisitor(int expectedNumberOfReads,
    		int lowQuality, int highQuality){
    	if(expectedNumberOfReads<1){
    		throw new IllegalArgumentException("expected number of reads must be >=1");
    	}
    	if(lowQuality <1 || lowQuality > Byte.MAX_VALUE){
    		throw new IllegalArgumentException("low quality must be valid quality value : "+ lowQuality);
    	}
    	if(highQuality <1 || highQuality > Byte.MAX_VALUE){
    		throw new IllegalArgumentException("high quality must be valid quality value : " + highQuality);
    	}
    	if(lowQuality <=highQuality){
    		throw new IllegalArgumentException("high quality must be higher than low quality value " + lowQuality + " vs " + highQuality);
    	}
    	int mapSize = MapUtil.computeMinHashMapSizeWithoutRehashing(expectedNumberOfReads);
        phds = new LinkedHashMap<String, Phd>(mapSize);
        directions = new HashMap<String, Direction>(mapSize);
		
        this.lowQuality = (byte)lowQuality;
        this.highQuality = (byte)highQuality;
    }
    
    @Override
	public void visitAlignedReadInfo(String readId,
			Direction dir, int gappedStartOffset) {
		directions.put(readId, dir);
	}

	

	public final Map<String, Phd> getPhds() {
		return phds;
	}

	@Override
	public AceContigReadVisitor visitBeginRead(String readId, int gappedLength) {
		return new IndividualReadPhdBuilderVisitor(readId, gappedLength, directions.get(readId));
	}
    
    
    private class IndividualReadPhdBuilderVisitor extends AbstractAceContigReadVisitor{
		private final QualitySequenceBuilder highLowQualities;
		private final NucleotideSequenceBuilder sequenceBuilder;
		private final Direction dir;
		private final String readId;
		
		public IndividualReadPhdBuilderVisitor(String readId, int gappedLength, Direction dir){
			this.readId = readId;
			this.dir = dir;
			highLowQualities = new QualitySequenceBuilder(gappedLength);
			sequenceBuilder = new NucleotideSequenceBuilder(gappedLength);
		}
		
		@Override
		public void visitBasesLine(String mixedCaseBasecalls) {
			highLowQualities.append(toHighLowQualities(mixedCaseBasecalls));
			sequenceBuilder.append(mixedCaseBasecalls);
		}
		
		private byte[] toHighLowQualities(String bases){
        	
            String ungappedGappedBases =ConsedUtil.convertAceGapsToContigGaps(bases).replaceAll("-", "");
            char[] chars = ungappedGappedBases.toCharArray();
            byte[] qualities = new byte[chars.length];
            for(int i=0; i<chars.length; i++){
                if(Character.isUpperCase(chars[i])){
                    qualities[i]=highQuality;
                }else{
                	qualities[i]=lowQuality;
                }
            }
           return qualities;
        }
		@Override
		public void visitEnd() {
			sequenceBuilder.ungap();
			if(dir==Direction.REVERSE){
				sequenceBuilder.reverseComplement();
				highLowQualities.reverse();
			}
			 Phd phd = new ArtificialPhd(readId, 
					 sequenceBuilder.build(),
					 highLowQualities.build(),19);
             phds.put(readId,phd);
		}
	}

    

}
