package org.jcvi.jillion.fasta;

import org.jcvi.jillion.core.Defline;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class DeflineConverterAllOneLineTest {

    @Parameterized.Parameters(name = "{0} {1} => {2}")
    public static List<Object[]> data(){
        return List.of(
                new Object[]{"foo", null, "foo"},
                new Object[]{"foo", "bar", "foo bar"},
                new Object[]{"foo", "bar baz", "foo bar baz"}

        );
    }
    private final String id, comment;
    private final Defline expected;
    private static final DeflineConverter sut = DeflineConverters.concatenateComment();

    public DeflineConverterAllOneLineTest(String id, String comment, String expectedId) {
        this.id = id;
        this.comment = comment;
        this.expected = Defline.of(expectedId);
    }

    @Test
    public void convert(){
        Defline actual = sut.apply(id,comment);
        assertEquals(expected, actual);
    }
}
