/*
 * Created on May 6, 2009
 *
 * @author dkatzel
 */
package org.jcvi.io.idReader;

public class IntegerIdParser implements IdParser<Integer> {

   

    @Override
    public Integer parseIdFrom(String idAsString) {
        return Integer.parseInt(idAsString);
    }

    @Override
    public boolean isValidId(String string) {
        try{
            Integer.parseInt(string);
        }catch(NumberFormatException e){
            return false;
        }
        return true;
    }

}
