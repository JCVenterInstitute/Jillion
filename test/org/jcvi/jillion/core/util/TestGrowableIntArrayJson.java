package org.jcvi.jillion.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jcvi.jillion.internal.core.util.GrowableIntArray;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestGrowableIntArrayJson {

    private static final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    public void serialize() throws JsonProcessingException {
        int[] expected = new int[]{1,2,3,4,5,6,7,10};

        GrowableIntArray sut = new GrowableIntArray(expected);

        String json = objectMapper.writeValueAsString(sut);

        GrowableIntArray actual = objectMapper.readValue(json, GrowableIntArray.class);

        assertArrayEquals(expected, actual.toArray());

    }
}
