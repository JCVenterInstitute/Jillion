package org.jcvi.jillion.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jcvi.jillion.internal.core.util.GrowableBitArray;
import org.jcvi.jillion.internal.core.util.GrowableByteArray;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class TestGrowableBitArrayJson {

    private static final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    public void serialize() throws JsonProcessingException {
        boolean[] expected = new boolean[]{true,false, true, true, false, false, true};

        GrowableBitArray sut = new GrowableBitArray(expected);

        String json = objectMapper.writeValueAsString(sut);

        GrowableBitArray actual = objectMapper.readValue(json, GrowableBitArray.class);

        assertArrayEquals(expected, actual.toArray());

    }
}
