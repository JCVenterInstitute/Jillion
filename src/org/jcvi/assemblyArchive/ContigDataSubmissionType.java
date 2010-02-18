/*
 * Created on Sep 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assemblyArchive;

public enum ContigDataSubmissionType {
    /**
     * Contig Data is output directly in the XML.
     */
     INLINE,
     /**
      * Contig Data is output to a file and then referenced by file in the
      * XML.
      */
     FILE
}
