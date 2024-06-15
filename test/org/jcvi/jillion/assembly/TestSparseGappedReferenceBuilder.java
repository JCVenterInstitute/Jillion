package org.jcvi.jillion.assembly;

import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import static org.easymock.EasyMock.*;
public class TestSparseGappedReferenceBuilder extends TestGappedReferenceBuilder{

    @Override
    protected GappedReferenceBuilder createSut(String seqPrefix) {
        NucleotideSequence seq = mock(NucleotideSequence.class);
        expect(seq.getLength()).andReturn((long) Integer.MAX_VALUE).anyTimes();
        expect(seq.getNumberOfGaps()).andReturn(0);
        NucleotideSequence actual = NucleotideSequence.of(seqPrefix);

        expect(seq.toBuilder(anyInt())).andReturn(actual.toBuilder());
        replay(seq);

        return new GappedReferenceBuilder(seq);
    }
}
