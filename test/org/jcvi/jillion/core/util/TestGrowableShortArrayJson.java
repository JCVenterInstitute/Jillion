package org.jcvi.jillion.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jcvi.jillion.internal.core.util.GrowableLongArray;
import org.jcvi.jillion.internal.core.util.GrowableShortArray;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class TestGrowableShortArrayJson {

    private static final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    public void serialize() throws JsonProcessingException {
        short[] expected = new short[]{1,2,3,4,5,6,7,10};

        GrowableShortArray sut = new GrowableShortArray(expected);

        String json = objectMapper.writeValueAsString(sut);

        GrowableShortArray actual = objectMapper.readValue(json, GrowableShortArray.class);

        assertArrayEquals(expected, actual.toArray());

    }
}
