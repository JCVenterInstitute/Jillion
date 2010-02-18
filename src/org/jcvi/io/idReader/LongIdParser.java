/*
 * Created on May 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io.idReader;

public class LongIdParser implements IdParser<Long> {
    @Override
    public Long parseIdFrom(String idAsString) {
        return Long.parseLong(idAsString);
    }

    @Override
    public boolean isValidId(String string) {
        try{
            Long.parseLong(string);
        }catch(NumberFormatException e){
            return false;
        }
        return true;
    }

}
