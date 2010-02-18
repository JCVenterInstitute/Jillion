/*
 * Created on Feb 3, 2010
 *
 * @author dkatzel
 */
package org.jcvi.trace.sanger.phd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.num.ShortGlyph;
import org.jcvi.glyph.num.ShortGlyphFactory;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.sequence.Peaks;

public class ArtificialPhd implements Phd{

    private static final ShortGlyphFactory PEAK_FACTORY = ShortGlyphFactory.getInstance();
    
    private final NucleotideEncodedGlyphs basecalls;
    private final EncodedGlyphs<PhredQuality> qualities;
   private final Properties comments;
   private final List<PhdTag> tags;
   private Peaks fakePositions=null;
   private final int numberOfPositionsForEachPeak;
   private final int numberOfBases;
   /**
    * {@code buildArtificalPhd} creates a {@link DefaultPhd}
    * using the given basecalls and qualities
    * but creates artificial peak data spacing each
    * peak {@code numberOfPositionsForEachPeak} apart.
    * This Phd will have no comments and no {@link PhdTag}s.
    * This method is the same as calling
    * {@link #buildArtificalPhd(NucleotideEncodedGlyphs, EncodedGlyphs, Properties, List, int)
    * buildArtificalPhd(basecalls, qualities, new Properties(),Collections.<PhdTag>emptyList(),numberOfPositionsForEachPeak)}
    * 
    * @param basecalls the basecalls for this Phd.
    * @param qualities the qualities for this Phd.
    * @param numberOfPositionsForEachPeak number of positions each
    * peak should be separated as.
    * @return a new DefaultPhd using the given values.
    * @see #buildArtificalPhd(NucleotideEncodedGlyphs, EncodedGlyphs, Properties, List, int)
    */
   public ArtificialPhd(NucleotideEncodedGlyphs basecalls,
           EncodedGlyphs<PhredQuality> qualities,
           int numberOfPositionsForEachPeak){
       this(basecalls, qualities, new Properties(),Collections.<PhdTag>emptyList(),numberOfPositionsForEachPeak);
   }
   /**
    * {@code buildArtificalPhd} creates a {@link DefaultPhd}
    * using the given basecalls and qualities, comments and tags
    * but creates artificial peak data spacing each
    * peak {@code numberOfPositionsForEachPeak} apart.
    * @param basecalls the basecalls for this Phd.
    * @param qualities the qualities for this Phd.
    * @param comments the comments for this Phd.
    * @param tags the {@link PhdTag}s for this Phd.
    * @param numberOfPositionsForEachPeak number of positions each
    * peak should be separated as.
    * @return a new DefaultPhd using the given values.
    */
    public ArtificialPhd(NucleotideEncodedGlyphs basecalls,
            EncodedGlyphs<PhredQuality> qualities,
           Properties comments, List<PhdTag> tags,int numberOfPositionsForEachPeak){
        this.basecalls = basecalls;
        this.qualities = qualities;
        this.tags = tags;
        this.comments = comments;
        this.numberOfBases = (int)basecalls.getLength();
        this.numberOfPositionsForEachPeak = numberOfPositionsForEachPeak;
        
        
    }

    @Override
    public Properties getComments() {
        return comments;
    }

    @Override
    public List<PhdTag> getTags() {
        return tags;
    }

    @Override
    public int getNumberOfTracePositions() {
        if(numberOfBases ==0){
            return 0;
        }
        return (numberOfBases+1)*numberOfPositionsForEachPeak;
    }

    @Override
    public synchronized Peaks getPeaks() {
        if(fakePositions ==null){
            List<ShortGlyph> fakePositions = new ArrayList<ShortGlyph>(numberOfBases);
            
            for(int i=0; i< numberOfBases; i++){
                fakePositions.add(PEAK_FACTORY.getGlyphFor(i * numberOfPositionsForEachPeak +numberOfPositionsForEachPeak ));
            }
            this.fakePositions = new Peaks(fakePositions);
        }
        return fakePositions;
    }

    @Override
    public NucleotideEncodedGlyphs getBasecalls() {
        return basecalls;
    }

    @Override
    public EncodedGlyphs<PhredQuality> getQualities() {
        return qualities;
    }
}
