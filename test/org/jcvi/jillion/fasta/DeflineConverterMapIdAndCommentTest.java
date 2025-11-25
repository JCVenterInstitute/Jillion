package org.jcvi.jillion.fasta;

import org.jcvi.jillion.core.Defline;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class DeflineConverterMapIdAndCommentTest {
    /**
     * This is a way to force the compiler to accept our array types.
     */
    private static Function<String, String> function(Function<String, String> func){
        return func;
    }

    @Parameterized.Parameters
    public static List<Object[]> data(){
        return List.of(
                new Object[]{null,null, "foo", null, "foo", null},
                new Object[]{null,null, "foo", "bar", "foo", "bar"},

                new Object[]{Function.identity(),Function.identity(), "foo", null, "foo", null},
                new Object[]{Function.identity(),Function.identity(), "foo", "bar", "foo", "bar"},

                new Object[]{Function.identity(),null, "foo", null, "foo", null},
                new Object[]{Function.identity(),null, "foo", "bar", "foo", "bar"},

                new Object[]{ function(id-> null), Function.identity(),  "foo", "bar", null,null},
                new Object[]{ function(id-> null), null,  "foo", "bar", null,null},

                new Object[]{function(id-> id+"_bar"),null, "foo", null,"foo_bar",null},
                new Object[]{function(id-> id+"_bar"),null, "foo", "baz","foo_bar","baz"},

                new Object[]{null,
                    function(comment-> {
                            if(comment==null) {
                                return "[program=Jillion]";
                            }
                            return comment +" " + "[program=Jillion]";
                        }),
                 "foo", null,"foo","[program=Jillion]"},
                new Object[]{null,
                        function(comment-> {
                            if(comment==null) {
                                return "[program=Jillion]";
                            }
                            return comment +" " + "[program=Jillion]";
                        }),
                        "foo", "bar","foo","bar [program=Jillion]"}


        );
    }
    private final String id, comment;
    private final Defline expected;
    private final DeflineConverter sut;
    private final DeflineConverter biSut, biSutIdOnly;

    public DeflineConverterMapIdAndCommentTest(Function<String,String> idFunction, Function<String,String> commentFunction,  String id, String comment, String expectedId, String expectedComment) {
        this.id = id;
        this.comment = comment;
        this.expected = Defline.of(expectedId, expectedComment);
        this.sut = DeflineConverters.map(idFunction, commentFunction);
        this.biSut = DeflineConverters.map(idFunction==null?null:
                (a, b)-> idFunction.apply(a),
                commentFunction==null? null: (a, b)-> commentFunction.apply(b));

        this.biSutIdOnly = DeflineConverters.map(

                (a, b)->  Defline.of(idFunction==null? a: idFunction.apply(a), commentFunction==null?comment: commentFunction.apply(b))
                );
    }

    @Test
    public void convert(){
        Defline actual = sut.apply(id,comment);
        assertEquals(expected, actual);
    }
    @Test
    public void convertBiFunctionIdOnly(){
        Defline actual = biSutIdOnly.apply(id,comment);
        assertEquals(expected, actual);
    }

    @Test
    public void convertBiFunctionIdAndComment(){
        Defline actual = biSut.apply(id,comment);
        assertEquals(expected, actual);
    }
}
