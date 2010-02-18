/*
 * Created on May 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io.idReader;

public class TestLongIdParser extends AbstractIdParser<Long>{

    private Long value = Long.valueOf(123456789L);
    @Override
    protected IdParser<Long> createNewIdParser() {
        return new LongIdParser();
    }

    @Override
    protected String getInvalidId() {
        return "not an Id";
    }

    @Override
    protected Long getValidIdAsCorrectType() {
        return value;
    }

    @Override
    protected String getValidIdAsString() {
        return value.toString();
    }

}
