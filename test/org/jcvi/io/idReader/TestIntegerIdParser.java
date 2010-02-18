/*
 * Created on May 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io.idReader;


public class TestIntegerIdParser extends AbstractIdParser<Integer> {

   
    @Override
    protected IdParser createNewIdParser(){
        return new IntegerIdParser();
    }
    @Override
    protected Integer getValidIdAsCorrectType() {
        return Integer.valueOf(1234);
    }
    @Override
    protected String getValidIdAsString() {
        return "1234";
    }
    @Override
    protected String getInvalidId() {
        return "notAnId";
    }
}
