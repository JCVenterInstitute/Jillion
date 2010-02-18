/*
 * Created on Mar 26, 2009
 *
 * @author dkatzel
 */
package org.jcvi.sequence;

import java.util.Arrays;
import java.util.List;

public class DefaultMated<T> implements Mated<T> {

    private final List<T> mates; 
    
    public DefaultMated(T...mates){
        this(Arrays.asList(mates));
    }
    public DefaultMated( List<T> mates){
        this.mates = mates;
    }
    
    @Override
    public List<T> getMates() {
        return mates;
    }


}
