package org.jcvi.jillion.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jcvi.jillion.internal.core.util.GrowableCharArray;
import org.jcvi.jillion.internal.core.util.GrowableShortArray;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class TestGrowableCharArrayJson {

    private static final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    public void serialize() throws JsonProcessingException {
        char[] expected = new char[]{'A','B','D','F', 'Z'};

        GrowableCharArray sut = new GrowableCharArray(expected);

        String json = objectMapper.writeValueAsString(sut);

        GrowableCharArray actual = objectMapper.readValue(json, GrowableCharArray.class);

        assertArrayEquals(expected, actual.toArray());

    }
}
