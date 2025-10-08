package org.jcvi.jillion.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jcvi.jillion.internal.core.util.GrowableIntArray;
import org.jcvi.jillion.internal.core.util.GrowableLongArray;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class TestGrowableLongArrayJson {

    private static final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    public void serialize() throws JsonProcessingException {
        long[] expected = new long[]{1,2,3,4,5,6,7,10};

        GrowableLongArray sut = new GrowableLongArray(expected);

        String json = objectMapper.writeValueAsString(sut);

        GrowableLongArray actual = objectMapper.readValue(json, GrowableLongArray.class);

        assertArrayEquals(expected, actual.toArray());

    }
}
