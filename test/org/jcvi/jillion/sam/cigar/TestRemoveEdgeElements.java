package org.jcvi.jillion.sam.cigar;

import org.junit.Test;

import static org.junit.Assert.*;
public class TestRemoveEdgeElements {

    @Test
    public void noHardClipsToRemove(){
        Cigar cigar = Cigar.parse("240M");

        assertEquals(cigar, cigar.toBuilder().removeHardClips().build());
    }

    @Test
    public void oneHardClipSideToRemove(){


        assertEquals(Cigar.parse("240M"),
                Cigar.parse("3H240M")
                        .toBuilder().removeHardClips().build());
    }
    @Test
    public void otherOneHardClipSideToRemove(){


        assertEquals(Cigar.parse("240M"),
                Cigar.parse("240M4H")
                        .toBuilder().removeHardClips().build());
    }

    @Test
    public void bothHardClipSideToRemove(){


        assertEquals(Cigar.parse("240M"),
                Cigar.parse("3H240M4H")
                        .toBuilder().removeHardClips().build());
    }

    @Test
    public void oneInsertionSideToRemove(){


        assertEquals(Cigar.parse("240M"),
                Cigar.parse("1I240M")
                        .toBuilder().removeEdgeInsertions().build());
    }

    @Test
    public void otherInsertionSideToRemove(){


        assertEquals(Cigar.parse("240M"),
                Cigar.parse("240M4I")
                        .toBuilder().removeEdgeInsertions().build());
    }

    @Test
    public void bothInsertionSideToRemove(){


        assertEquals(Cigar.parse("240M"),
                Cigar.parse("3I240M4I")
                        .toBuilder().removeEdgeInsertions().build());
    }

    @Test
    public void multiOneInsertionSideToRemove(){


        assertEquals(Cigar.parse("240M"),
                Cigar.parse("3I1I240M")
                        .toBuilder().removeEdgeInsertions().build());
    }
    @Test
    public void allInsertionsToBeRemoved(){


        assertEquals(0,
                Cigar.parse("3I5I6I")
                        .toBuilder().removeEdgeInsertions().build()
                        .getNumberOfElements());
    }

    @Test
    public void multiOtherInsertionSideToRemove(){


        assertEquals(Cigar.parse("240M"),
                Cigar.parse("240M4I6I")
                        .toBuilder().removeEdgeInsertions().build());
    }

    @Test
    public void multibothInsertionSideToRemove(){


        assertEquals(Cigar.parse("240M"),
                Cigar.parse("3I3I240M4I6I")
                        .toBuilder().removeEdgeInsertions().build());
    }
}
