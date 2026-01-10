package org.jcvi.jillion.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jcvi.jillion.internal.core.util.GrowableByteArray;
import org.jcvi.jillion.internal.core.util.GrowableLongArray;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class TestGrowableByteArrayJson {

    private static final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    public void serialize() throws JsonProcessingException {
        byte[] expected = new byte[]{1,2,3,4,5,6,7,10};

        GrowableByteArray sut = new GrowableByteArray(expected);

        String json = objectMapper.writeValueAsString(sut);

        GrowableByteArray actual = objectMapper.readValue(json, GrowableByteArray.class);

        assertArrayEquals(expected, actual.toArray());

    }
}
