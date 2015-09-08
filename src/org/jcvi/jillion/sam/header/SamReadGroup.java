/*******************************************************************************
 * Copyright (c) 2009 - 2015 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * 	
 * 	Contributors:
 *         Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.sam.header;

import java.util.Date;
import java.util.Locale;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
/**
 * {@code ReadGroup} is a way
 * to group reads as belonging to a particular
 * set.  Often Read Groups are used to note
 * which reads came from which sequencing run
 * or pool.
 * @author dkatzel
 *
 */
public interface SamReadGroup {

    /**
     * The predicted median insert size.
     * @return the median insert size as an Integer;
     * or {@code null} if not specified.
     */
    Integer getPredictedInsertSize();

    /**
     * Get the platform/technology used to produce the reads
     * in this read group.
     * @return a {@link PlatformTechnology} or {@code null}
     * if not specified.
     */
    PlatformTechnology getPlatform();

    /**
     * Get the unique ID. The value of ID is used in the
     * {@link org.jcvi.jillion.sam.attribute.ReservedSamAttributeKeys#READ_GROUP}
     * tag.
     * 
     * @return a String; will never be null.
     */
    String getId();

    /**
     * Get the unique Platform unit 
     * (e.g. flowcell-barcode.lane for Illumina or slide for SOLiD).
     * @return the the platform unit as a String;
     * may be {@code null} if not provided.
     */
    String getPlatformUnit();

    /**
     * Get the key sequence for each read group
     * (454 and iontorrent only).
     * @return the {@link NucleotideSequence}
     * representing the keysequence or
     * {@code null} if the keysequence is not specified
     * or is not used by this read group.
     */
    NucleotideSequence getKeySequence();

    /**
     * Get the flow order for each read group
     * (454 and iontorrent only).
     * @return the {@link NucleotideSequence}
     * representing the floworder or
     * {@code null} if the floworder is not specified
     * or is not used by this read group.
     */
    NucleotideSequence getFlowOrder();

    /**
     * Get the sequencingCenter that produced
     * this read group.
     * @return the sequencingCenter of this read group as a String;
     * may be {@code null} if this information is not provided.
     */
    String getSequencingCenter();

    /**
     * Description of the program.
     * @return the description of what this program
     * does as a String;
     * may be {@code null} if not provided.
     */
    String getDescription();

    /**
     * Get the Library name of this read group.
     * @return the library name used to construct
     * this read group as a String;
     * may be {@code null} if not provided.
     */
    String getLibrary();

    /**
     * Get the programs used for processing
     * this read group.
     * @return an String or
     * {@code null} if not specified.
     */
    String getPrograms();

    /**
     * Get the name of the pool being sequenced
     * or the name of the sample of being sequenced
     * if there is no pool.
     * @return a String; may be {@code null}
     * if not specified.
     */
    String getSampleOrPoolName();

    /**
     * Date the run was produced.
     * @return a new {@link Date}
     * instance; or {@code null}
     * if the run date is not specified.
     */
    Date getRunDate();
    

    public enum PlatformTechnology{
            /**
             * Sanger.
             */
            CAPILLARY,
            LS454,
            ILLUMINA,
            SOLID,
            HELICOS,
            IONTORRENT,
            PACBIO
            ;
            
            public static PlatformTechnology parse(String value){
                    return valueOf(value.toUpperCase(Locale.US));
            }
    }

}
