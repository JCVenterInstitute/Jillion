/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Feb 3, 2010
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.trace.sanger.phd;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.jcvi.common.core.seq.trace.sanger.PositionSequence;
import org.jcvi.common.core.seq.trace.sanger.PositionSequenceBuilder;
import org.jcvi.common.core.symbol.Sequence;
import org.jcvi.common.core.symbol.qual.QualitySequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;

public class ArtificialPhd implements Phd{
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
    
    private final NucleotideSequence basecalls;
    private final QualitySequence qualities;
   private final Properties comments;
   private final List<PhdTag> tags;
   private PositionSequence fakePositions=null;
   private final int numberOfPositionsForEachPeak;
   private final int numberOfBases;
   private final int positionOfFirstPeak;
   private final String id;
   /**
    * Create an {@link ArtificialPhd} record that matches
    * the way Newbler creates phd records for 454 reads.
    * This is needed so tools like consed will correctly
    * space the fake chromatograms for 454 reads since it uses
    * 454 developed tools which rely on this spacing.
    * @param id the id for this Phd.
    * @param basecalls the basecalls for this Phd.
    * @param qualities the qualities for this Phd.
    * @param comments the comments for this Phd.
    * @param tags the {@link PhdTag}s for this Phd.
    * @return a new {@link ArtificialPhd} which has position data that matches
    * what would have been created by Newbler.
    */
   public static ArtificialPhd createNewbler454Phd(
		   String id,
		   NucleotideSequence basecalls,
           QualitySequence qualities,
           Properties comments, List<PhdTag> tags){
       return new ArtificialPhd(id,basecalls, qualities, comments, tags, NEWBLER_454_START_POSITION,NEWBLER_454_PEAK_SPACING);
   }
   /**
    * Create an {@link ArtificialPhd} record that matches
    * the way Newbler creates phd records for 454 reads.
    * This is needed so tools like consed will correctly
    * space the fake chromatograms for 454 reads since it uses
    * 454 developed tools which rely on this spacing.
    * @param id the id for this Phd.
    * @param basecalls the basecalls for this Phd.
    * @param qualities the qualities for this Phd.
    * @param comments the comments for this Phd.
    * @return a new {@link ArtificialPhd} which has position data that matches
    * what would have been created by Newbler.
    */
   public static ArtificialPhd createNewbler454Phd(
		   String id,
		   NucleotideSequence basecalls,
           QualitySequence qualities,
           Properties comments){
	   return ArtificialPhd.createNewbler454Phd(id,basecalls, qualities, 
               comments,Collections.<PhdTag>emptyList());
   }
   /**
    * Create an {@link ArtificialPhd} record that matches
    * the way Newbler creates phd records for 454 reads.
    * This is needed so tools like consed will correctly
    * space the fake chromatograms for 454 reads since it uses
    * 454 developed tools which rely on this spacing.
    * @param id the id for this Phd.
    * @param basecalls the basecalls for this Phd.
    * @param qualities the qualities for this Phd.
    * @return a new {@link ArtificialPhd} which has position data that matches
    * what would have been created by Newbler.
    */
   public static ArtificialPhd createNewbler454Phd(String id,
		   NucleotideSequence basecalls,
           QualitySequence qualities){
       return ArtificialPhd.createNewbler454Phd(id,basecalls, qualities, 
               new Properties(),Collections.<PhdTag>emptyList());
   }
   /**
    * {@code buildArtificalPhd} creates a {@link DefaultPhd}
    * using the given basecalls and qualities
    * but creates artificial peak data spacing each
    * peak {@code numberOfPositionsForEachPeak} apart.
    * This Phd will have no comments and no {@link PhdTag}s.
    * This method is the same as calling
    * {@link #buildArtificalPhd(NucleotideSequence, Sequence, Properties, List, int)
    * buildArtificalPhd(basecalls, qualities, new Properties(),Collections.<PhdTag>emptyList(),numberOfPositionsForEachPeak)}
    * @param id the id for this Phd.
    * @param basecalls the basecalls for this Phd.
    * @param qualities the qualities for this Phd.
    * @param numberOfPositionsForEachPeak number of positions each
    * peak should be separated as.
    * @return a new DefaultPhd using the given values.
    * @see #buildArtificalPhd(NucleotideSequence, Sequence, Properties, List, int)
    */
   public ArtificialPhd(String id,
		   NucleotideSequence basecalls,
           QualitySequence qualities,
           int numberOfPositionsForEachPeak){
       this(id,basecalls, qualities, new Properties(),Collections.<PhdTag>emptyList(),numberOfPositionsForEachPeak);
   }
   /**
    * {@code buildArtificalPhd} creates a {@link DefaultPhd}
    * using the given basecalls and qualities, comments and tags
    * but creates artificial peak data spacing each
    * peak {@code numberOfPositionsForEachPeak} apart.
    * @param id the id for this Phd.
    * @param basecalls the basecalls for this Phd.
    * @param qualities the qualities for this Phd.
    * @param comments the comments for this Phd.
    * @param tags the {@link PhdTag}s for this Phd.
    * @param numberOfPositionsForEachPeak number of positions each
    * peak should be separated as.
    * @return a new DefaultPhd using the given values.
    */
    public ArtificialPhd(String id, NucleotideSequence basecalls,
            QualitySequence qualities,
           Properties comments, List<PhdTag> tags,int numberOfPositionsForEachPeak){
       this(id,basecalls, qualities,comments, tags,numberOfPositionsForEachPeak,numberOfPositionsForEachPeak);
        
        
    }
    /**
     * {@code buildArtificalPhd} creates a {@link DefaultPhd}
     * using the given basecalls and qualities, comments and tags
     * but creates artificial peak data spacing each
     * peak {@code numberOfPositionsForEachPeak} apart.
     * @param id the id for this Phd.
     * @param basecalls the basecalls for this Phd.
     * @param qualities the qualities for this Phd.
     * @param comments the comments for this Phd.
     * @param tags the {@link PhdTag}s for this Phd.
     * @param numberOfPositionsForEachPeak number of positions each
     * peak should be separated as.
     * @return a new DefaultPhd using the given values.
     */
     public ArtificialPhd(String id, NucleotideSequence basecalls,
             QualitySequence qualities,
            Properties comments, List<PhdTag> tags,int positionOfFirstPeak,int numberOfPositionsForEachPeak){
         this.id = id;
    	 this.basecalls = basecalls;
         this.qualities = qualities;
         this.tags = tags;
         this.comments = comments;
         this.numberOfBases = (int)basecalls.getLength();
         this.numberOfPositionsForEachPeak = numberOfPositionsForEachPeak;
         this.positionOfFirstPeak = positionOfFirstPeak;
         
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
    public String getId(){
    	return id;
    }
    @Override
    public int getNumberOfTracePositions() {
        if(numberOfBases ==0){
            return 0;
        }
        return (numberOfBases+1)*numberOfPositionsForEachPeak;
    }

    @Override
    public synchronized PositionSequence getPositionSequence() {
        if(fakePositions ==null){
        	PositionSequenceBuilder builder = new PositionSequenceBuilder(numberOfBases);
            
            for(int i=0; i< numberOfBases; i++){
               builder.append(i * numberOfPositionsForEachPeak +positionOfFirstPeak );
            }
            this.fakePositions = builder.build();
        }
        return fakePositions;
    }

    @Override
    public NucleotideSequence getNucleotideSequence() {
        return basecalls;
    }

    @Override
    public QualitySequence getQualitySequence() {
        return qualities;
    }
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((basecalls == null) ? 0 : basecalls.hashCode());
		result = prime * result
				+ ((comments == null) ? 0 : comments.hashCode());
		result = prime * result
				+ ((fakePositions == null) ? 0 : fakePositions.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((qualities == null) ? 0 : qualities.hashCode());
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ArtificialPhd)) {
			return false;
		}
		ArtificialPhd other = (ArtificialPhd) obj;
		if (basecalls == null) {
			if (other.basecalls != null) {
				return false;
			}
		} else if (!basecalls.equals(other.basecalls)) {
			return false;
		}
		if (comments == null) {
			if (other.comments != null) {
				return false;
			}
		} else if (!comments.equals(other.comments)) {
			return false;
		}
		if (fakePositions == null) {
			if (other.fakePositions != null) {
				return false;
			}
		} else if (!fakePositions.equals(other.fakePositions)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (qualities == null) {
			if (other.qualities != null) {
				return false;
			}
		} else if (!qualities.equals(other.qualities)) {
			return false;
		}
		if (tags == null) {
			if (other.tags != null) {
				return false;
			}
		} else if (!tags.equals(other.tags)) {
			return false;
		}
		return true;
	}
    
    
}
