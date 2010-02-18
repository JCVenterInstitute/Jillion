/*
 * Created on Nov 24, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.util.Date;

import org.jcvi.Range;
import org.jcvi.assembly.contig.AbstractContigFileVisitor;
import org.jcvi.sequence.SequenceDirection;

public abstract class AbstractAceAdaptedContigFileDataStore extends AbstractContigFileVisitor{

    private DefaultAceContig.Builder contigBuilder;
    private final Date phdDate;
    
    /**
     * Create a new AceAdapted Contig File DataStore using the given phdDate.
     * @param phdDate the date all faked phd files should be timestamped with.
     */
    public AbstractAceAdaptedContigFileDataStore(Date phdDate) {
        this.phdDate = new Date(phdDate.getTime());
    }

    @Override
    protected void visitBeginContig(String contigId, String consensus) {
        contigBuilder = new DefaultAceContig.Builder(contigId, consensus);
    }

    @Override
    protected void visitEndOfContig() {
        visitAceContig(contigBuilder.build());
        
    }

    @Override
    protected void visitRead(String readId, int offset, Range validRange,
            String basecalls, SequenceDirection dir) {
        PhdInfo info =new DefaultPhdInfo(readId, readId+".phd.1", phdDate);
        contigBuilder.addRead(readId, basecalls ,offset, dir, 
                validRange ,info);
        
    }

    protected abstract void visitAceContig(DefaultAceContig aceContig);
    

    
}
