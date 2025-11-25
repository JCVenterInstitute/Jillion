package org.jcvi.jillion.fasta;

import org.jcvi.jillion.core.Defline;
import org.jcvi.jillion.core.testUtil.TestUtil;
import org.junit.Test;
import static org.junit.Assert.*;
public class DeflineTest {

    @Test
    public void factoryNullIdReturnsNull(){
        assertNull(Defline.of(null));
        assertNull(Defline.of(null, "bar"));
    }

    @Test
    public void getters(){
        Defline sut = Defline.of("foo", "bar");
        assertEquals("foo", sut.getId());
        assertEquals("bar", sut.getComment());
    }
    @Test
    public void nullCommentOK(){
        Defline sut = Defline.of("foo", null);
        assertEquals("foo", sut.getId());
        assertNull(sut.getComment());
    }
    @Test
    public void singleArgFactoryMakesNullComment(){
        Defline sut = Defline.of("foo");
        assertEquals("foo", sut.getId());
        assertNull(sut.getComment());
    }

    @Test
    public void equalsAndHashCodeSameRef(){
        Defline sut = Defline.of("foo", "bar");
        TestUtil.assertEqualAndHashcodeSame(sut, sut);
    }
    @Test
    public void equalsAndHashCodeSameValues(){
        Defline sut = Defline.of("foo", "bar");
        Defline sut2 = Defline.of("foo", "bar");
        TestUtil.assertEqualAndHashcodeSame(sut, sut2);
    }
    @Test
    public void equalsAndHashCodeSameValuesNullComment(){
        Defline sut = Defline.of("foo", null);
        Defline sut2 = Defline.of("foo");
        TestUtil.assertEqualAndHashcodeSame(sut, sut2);
    }
    @Test
    public void equalsAndHashCodeDifferentWithDiffIdValues(){
        Defline sut = Defline.of("foo", "bar");
        Defline sut2 = Defline.of("foo2", "bar");
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, sut2);
    }
    @Test
    public void equalsAndHashCodeDifferentWithDiffCommentValues(){
        Defline sut = Defline.of("foo", "bar");
        Defline sut2 = Defline.of("foo", "bar2");
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, sut2);
    }
    @Test
    public void equalsAndHashCodeDifferentWithOneNullCommentValues(){
        Defline sut = Defline.of("foo", "bar");
        Defline sut2 = Defline.of("foo");
        TestUtil.assertNotEqualAndHashcodeDifferent(sut, sut2);
    }

}
