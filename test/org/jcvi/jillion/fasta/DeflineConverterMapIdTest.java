package org.jcvi.jillion.fasta;

import org.jcvi.jillion.core.Defline;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class DeflineConverterMapIdTest {
    /**
     * This is a way to force the compiler to accept our array types.
     */
    private static Function<String, String> function(Function<String, String> func){
        return func;
    }

    @Parameterized.Parameters(name = "{0} {1} => {2}")
    public static List<Object[]> data(){
        return List.of(
                new Object[]{Function.identity(), "foo", "foo"},
                new Object[]{ function(id-> null), "foo", null},
                new Object[]{function(id-> id+"_bar"), "foo", "foo_bar"}

        );
    }
    private final String id;
    private final Defline expected;
    private final DeflineConverter sut;

    public DeflineConverterMapIdTest(Function<String,String> function, String id, String expectedId) {
        this.id = id;
        this.expected = Defline.of(expectedId);
        this.sut = DeflineConverters.map(function);
    }

    @Test
    public void convert(){
        Defline actual = sut.apply(id,null);
        assertEquals(expected, actual);
    }
}
