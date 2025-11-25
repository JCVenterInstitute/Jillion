package org.jcvi.jillion.fasta;

import org.jcvi.jillion.core.Defline;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class DeflineConvertSpaceTest {

    @Parameterized.Parameters(name = "{1} {2} => {3}")
    public static List<Object[]> data(){
        return List.of(
                new Object[]{"_", "foo", null, "foo"},
                new Object[]{"_", "foo", "bar", "foo_bar"},
                new Object[]{"_", "foo", "bar baz", "foo_bar_baz"},

                new Object[]{"|", "foo", null, "foo"},
                new Object[]{"|", "foo", "bar", "foo|bar"},
                new Object[]{"|", "foo", "bar baz", "foo|bar|baz"},

                new Object[]{"||", "foo", null, "foo"},
                new Object[]{"||", "foo", "bar", "foo||bar"},
                new Object[]{"||", "foo", "bar baz", "foo||bar||baz"},

                //id has spaces
                new Object[]{"_", "foo bar", null, "foo_bar"},
                new Object[]{"_", "foo bar", "baz", "foo_bar_baz"}

        );
    }
    private final String id, comment;
    private final Defline expected;
    private final DeflineConverter sut;

    public DeflineConvertSpaceTest(String separator, String id, String comment, String expectedId) {
        this.id = id;
        this.comment = comment;
        this.expected = Defline.of(expectedId);
        this.sut = DeflineConverters.convertAllSpacesTo(separator);
    }

    @Test
    public void convert(){
        Defline actual = sut.apply(id,comment);
        assertEquals(expected, actual);
    }
}
