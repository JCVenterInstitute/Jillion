package org.jcvi.jillion_experimental.align;

import java.util.List;

import org.jcvi.jillion_experimental.align.AlnVisitor2.ConservationInfo;


public interface AlnGroupVisitor {

	 /**
     * End of the current  group of aligned reads is about to be
     * visited.  If there are more groups, then the next method to be
     * visited will be {@link #visitBeginGroup()}
     * or {@link #visitEndOfFile()} if the file has been
     * completely parsed.
     */
    void visitEndGroup();
    /**
     * Visit a single read in the current aligned group.
     * @param id the id of this read.
     * @param gappedAlignment the gapped alignment of this read
     * in this read group.  Usually groups are only about 60 residues long
     * so if this read is longer than that, then only a partial alignment
     * will be presented. (Longer reads will span multiple groups).
     */
    void visitAlignedSegment(String id, String gappedAlignment);
    /**
     * Visit a description of the conservation of the residues
     * in the current group.
     * @param conservationInfos a List of {@link ConservationInfo}; 
     * will never be null.
     * there will be one record in the list for each residue in the group.
     * the ith record in the list corresponds to the ith residue in the group.
     */
    void visitConservationInfo(List<ConservationInfo> conservationInfos);
}
