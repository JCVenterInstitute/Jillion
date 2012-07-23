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
 * Created on Sep 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.common.core.seq.read.trace.archive2;

import java.util.Locale;

/**
 * All currently supported Fields in a Trace Info XML
 * document.
 * @author dkatzel
 * @see <a href="http://www.ncbi.nlm.nih.gov/Traces/trace.cgi?cmd=show&f=rfc&m=doc&s=rfc">
 Trace Archive RFC Documentation</a>
 *
 */
public enum TraceInfoField {
    /**
     * Genbank/EMBL/DDBJ accession number
     */
     ACCESSION,
     /**
     * The forward amplification primer sequence
     */
     AMPLIFICATION_FORWARD,
     /**
     * The reverse amplification primer sequence
     */
     AMPLIFICATION_REVERSE,
     /**
     * The expected amplification size for a pair of 
                         primers
     */
     AMPLIFICATION_SIZE,
     /**
     * Anonymous ID for an individual.
     */
     ANONYMIZED_ID,
     /**
     * Number of times the 
                 sequencing project has been attempted by the center and/or submitted to the 
                 Trace Archive.
     */
     ATTEMPT,
     /**
     * File name with base calls
     */
     BASE_FILE,
     /**
     * Name of the sequencing center.
     */
     CENTER_NAME,
     /**
     * Center defined project name
     */
     CENTER_PROJECT,
     /**
     * Description of the chemistry used in the sequencing reaction
     */
     CHEMISTRY,
     /**
     * Type of chemistry used in the sequencing reaction
     */
     CHEMISTRY_TYPE,
     /**
     * Chromosome to which the 
                 trace is assigned
     */
     CHROMOSOME,
     /**
     * Left clip of the read, in 
                 base pairs, based on quality analysis
     */
     CLIP_QUALITY_LEFT,
     /**
     * Right clip of the read, 
                 in base pairs, based on quality analysis.
     */
     CLIP_QUALITY_RIGHT,
     /**
     * Left clip of the read, in 
                 base pairs, based on vector sequence.
     */
     CLIP_VECTOR_LEFT,
     /**
     * Right clip of the read, 
                 in base pairs, based on vector sequence.
     */
     CLIP_VECTOR_RIGHT,
     /**
     * The name of the clone 
                 from which the trace was derived
     */
     CLONE_ID,
     /**
     * Semi-colon delimited 
                 list of clones if the Strategy is PoolClone.
     */
     CLONE_ID_LIST,
     /**
     * The full date, in "Mar  2 2006 12:00AM" format, 
                 on which an environmental sample was collected.
     */
     COLLECTION_DATE,
     /**
     * Repository ( 
                 GenBank/EMBL/DDBJ) accession identifier for the cloning vector.
     */
     CVECTOR_ACCESSION,
     /**
     * Center defined code 
                 for the cloning vector
     */
     CVECTOR_CODE,
     /**
     * Depth (in meters) at which an 
                 environmental sample was collected.
     */
     DEPTH,
     /**
     * Elevation (in meters) at which 
                 an environmental sample was collected
     */
     ELEVATION,
     /**
     * Type of 
                 environment from which an environmental sample was collected.
     */
     ENVIRONMENT_TYPE,
     /**
     * Extra ancillary information wrapped 
                            around in a EXTENDED_DATA block, where actual values are provided 
                            with a special &lt;field&gt; tag
     */
     EXTENDED_DATA,
     /**
     * File describing 
                 the features and their locations on a chip.
     */
     FEATURE_ID_FILE,
     /**
     * Reference to a common 
                          FEATURE_ID_FILE which
                          should be submitted first.
     */
     FEATURE_ID_FILE_NAME,
     /**
     * File giving 
                 the signal and variance for features on a chip.
     */
     FEATURE_SIGNAL_FILE,
     /**
     * Reference to a common 
                          FEATURE_SIGNAL_FILE which
                          should be submitted first.
     */
     FEATURE_SIGNAL_FILE_NAME,
     /**
     * Gene name or some other common identifier
     */
     GENE_NAME,
     /**
     * The largest filter 
                 used to stratify an environmental sample.
     */
     HI_FILTER_SIZE,
     /**
     * The condition of 
                 the host from which an environmental sample was obtained.
     */
     HOST_CONDITION,
     /**
     * Unique identifier for the 
                 specific host from which an environmental sample was taken.
     */
     HOST_ID,
     /**
     * Specific location 
                 on the host from which an environmental sample was collected.
     */
     HOST_LOCATION,
     /**
     * The host from which an environmental sample was obtained.
     */
     HOST_SPECIES,
     /**
     * Publicly available identifier to denote a specific
                    individual or sample from which a trace was derived.
     */
     INDIVIDUAL_ID,
     /**
     * Flanking sequence at the cloning junction.
     */
     INSERT_FLANK_LEFT,
     /**
     * Flanking sequence at the cloning junction.
     */
     INSERT_FLANK_RIGHT,
     /**
     * Expected size of the insert (referred to by the value in the TEMPLATE_ID field) in base pairs.
     */
     INSERT_SIZE,
     /**
     * Approximate standard deviation of value in INSERT_SIZE field
     */
     INSERT_STDEV,
     /**
     * The latitude measurement (using 
                 standard GPS notation) from which a sample was collected.
     */
     LATITUDE,
     /**
     * The source of the clone identified in the CLONE_ID field
     */
     LIBRARY_ID,
     /**
     * The longitude measurement (using standard GPS notation) from which a sample
                     was collected.
     */
     LONGITUDE,
     /**
     * The smallest filter size used to stratify an environmental sample.
     */
     LO_FILTER_SIZE,
     /**
     * Project ID generated by the  
                                  Genome Project database at NCBI/NLM/NIH
     */
     NCBI_PROJECT_ID,
     /**
     * Description of species for BARCODE project 
                                    from which trace is derived.
     */
     ORGANISM_NAME,
     /**
     * Name of file that contains the list of peak values.
     */
     PEAK_FILE,
     /**
     * The pH at which an environmental sample was collected.
     */
     PH,
     /**
     * Id to group traces picked at the same time.
     */
     PICK_GROUP_ID,
     /**
     * Country in which the  biological sample was collected and/or common name for a given location.
     */
     PLACE_NAME,
     /**
     * Submitter defined plate id
     */
     PLATE_ID,
     /**
     * Center provided id 
                 to designate a population from which a trace (or group of traces) was derived.
     */
     POPULATION_ID,
     /**
     * ID that defines 
                 groups of traces prepared at the same time.
     */
     PREP_GROUP_ID,
     /**
     * The primer sequence (used 
                 in the sequencing reaction)
     */
     PRIMER,
     /**
     * Identifier for the sequencing primer used.
     */
     PRIMER_CODE,
     /**
     * A ';' delimited list 
                 of primers used in a mapping experiment (such as AFLP)
     */
     PRIMER_LIST,
     /**
     * The program used to create the trace file.
     */
     PROGRAM_ID,
     /**
     * Term by which to group traces from different centers
                  based on a common project.
     */
     PROJECT_NAME,
     /**
     * Name of file containing the quality scores.
     */
     QUAL_FILE,
     /**
     * Reference 
                 accession (use accession and version to specify a particular instance of a 
                 sequence) used as the basis for a re-sequencing project. In case of Comparative strategy show the basis for primers design.
     */
     REFERENCE_ACCESSION,
     /**
     * Finish position for a particular amplicon in
                      re-sequencing or comparative projects.
     */
     REFERENCE_ACC_MAX,
     /**
     * Start position for a particular amplicon in
                         re-sequencing or comparative projects.
     */
     REFERENCE_ACC_MIN,
     /**
     * Sequence offset of accession specified in
                     REFERENCE_ACCESSION field to define the coordinate start position used 
                              as the basis for a re-sequencing project.
     */
     REFERENCE_OFFSET,
     /**
     * Finish position for a entire
                    re-sequencing region. This region may include several amplicons.
     */
     REFERENCE_SET_MAX,
     /**
     * Start position for a entire
      re-sequencing region. This region may include several amplicons.
     */
     REFERENCE_SET_MIN,
     /**
     * Date the sequencing reaction was run.
     */
     RUN_DATE,
     /**
     * ID used to group traces run on the same machine.
     */
     RUN_GROUP_ID,
     /**
     * Lane or capillary of the trace
     */
     RUN_LANE,
     /**
     * ID of the specific sequencing machine on which a trace was obtained
     */
     RUN_MACHINE_ID,
     /**
     * Type or model of machine on which a trace was obtained.
     */
     RUN_MACHINE_TYPE,
     /**
     * The salinity at which an environmental sample was collected 
                                     measured in parts per thousand units (promille).
     */
     SALINITY,
     /**
     * Center specified M13/PUC library that is actually sequenced.
     */
     SEQ_LIB_ID,
     /**
     * Source of the DNA
     */
     SOURCE_TYPE,
     /**
     * Description of species from which trace is derived
     */
     SPECIES_CODE,
     /**
     * Strain from which a trace is derived.
     */
     STRAIN,
     /**
     * Experimental STRATEGY
     */
     STRATEGY,
     /**
     * Type of submission
     */
     SUBMISSION_TYPE,
     /**
     * GenBank/EMBL/DDBJ accession of the sequencing vector.
     */
     SVECTOR_ACCESSION,
     /**
     * Center defined code for the sequencing vector
     */
     SVECTOR_CODE,
     /**
     * The temperature (in <sup>o</sup>C) at which an environmental sample was collected.
     */
     TEMPERATURE,
     /**
     * Submitter defined identifier for the sequencing template.
     */
     TEMPLATE_ID,
     /**
     * Defines the end of the 
                 template contained in the read.
     */
     TRACE_END,
     /**
     * Filename with the 
                 trace, relative to the top of the volume.
     */
     TRACE_FILE,
     /**
     * Format of the trace file.
     */
     TRACE_FORMAT,
     /**
     * Center defined trace identifier.
     */
     TRACE_NAME,
     /**
     * Sequencing strategy by which 
                                     the trace was obtained.
     */
     TRACE_TYPE_CODE,
     /**
     * GenBank/EMBL/DDBJ 
                 accession for transposon used in generating sequencing template.
     */
     TRANSPOSON_ACC,
     /**
     * Center defined 
                 code for transposon used in generating sequencing template.
     */
     TRANSPOSON_CODE,
     /**
     * Center defined well 
                 identifier for the sequencing reaction.
     */
     WELL_ID,
     /**
     * Length of the trace in base pairs.
     */
     BASECALL_LENGTH,
     /**
     * Number of  base pairs for which quality score exceed 20.
     */
     BASES_20,
     /**
     * Number of  base pairs for which quality score exceed 40.
     */
     BASES_40,
     /**
     * Number of  base pairs for which quality score exceed 60.
     */
     BASES_60,
     /**
     * Date on which the data was loaded.
     */
     LOAD_DATE,
     /**
     * TI's of the reads obtained from the other end of the same template.
     */
     MATE_PAIR,
     /**
     * TI that replaced the 
                 current TI as "active"
     */
     REPLACED_BY,
     /**
     * indicates the status of the trace
     */
     STATE,
     /**
     * NCBI Taxonomy ID.
     */
     TAXID,
     /**
     * Trace unique internal Identifier.
     */
     TI,
     /**
     * Date on which the data was updated/replaced.
     */
     UPDATE_DATE,
     /**
     * Public identifier for a given version of a genome assembly
     */
     ASSEMBLY_ID,
     /**
     * 
     */
     CHROMOSOME_REGION,
     /**
      * Name of subspecies.
      */
     SUBSPECIES_ID,
     
     /**
     * Direction of the read.
     */
     TRACE_DIRECTION;
     
     public static TraceInfoField parseTraceInfoField(String traceInfoField){
         return TraceInfoField.valueOf(traceInfoField.toUpperCase(Locale.US));
     }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

}
