/*
 * Created on Sep 1, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assemblyArchive;

import org.jcvi.assembly.Contig;
import org.jcvi.assembly.PlacedRead;


public class DefaultAssemblyArchiveContigRecord implements AssemblyArchiveContigRecord{

    private final ContigConformation conformation;
    private final Contig<? extends PlacedRead> contig;
    private final AssemblyArchiveType type;
    private final String submitterReference;

    public DefaultAssemblyArchiveContigRecord(String submitterReference,
            Contig<? extends PlacedRead> contig, AssemblyArchiveType type) {
        this(submitterReference, contig, type, ContigConformation.LINEAR);
    }
    /**
     * @param submitterReference
     * @param contig
     * @param type
     * @param conformation
     */
    public DefaultAssemblyArchiveContigRecord(String submitterReference,
            Contig<? extends PlacedRead> contig, AssemblyArchiveType type,
            ContigConformation conformation) {
        this.submitterReference = submitterReference;
        this.contig = contig;
        this.type = type;
        this.conformation = conformation;
    }

    @Override
    public ContigConformation getConformation() {
        return conformation;
    }

    @Override
    public Contig<? extends PlacedRead> getContig() {
        return contig;
    }


    @Override
    public String getSubmitterReference() {
        return submitterReference;
    }

    @Override
    public AssemblyArchiveType getType() {
        return type;
    }
}
