package org.jcvi.jillion.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.junit.Test;

import static org.junit.Assert.*;
public class TestJsonSerializationRange {

    ObjectMapper objectMapper = new ObjectMapper();

    @Data
    public static class Foo{
        Range myRange;
    }


    @Test
    public void writeAsJsonAndParseBack() throws JsonProcessingException {

        Foo sut = new Foo();
        Range range = Range.of(1,20);
        sut.setMyRange(range);


        String actual = objectMapper.writeValueAsString(sut);
        assertEquals("{\"myRange\":\"1..20\"}", actual);

        Foo reParsed = objectMapper.readValue(actual, Foo.class);
        assertEquals(sut, reParsed);
    }
}
