/*
 * Created on Oct 2, 2009
 *
 * @author dkatzel
 */
package org.jcvi.sequence;

public enum TrimType {

    CLR("clear range"),
    CLV("clear of vector"),
    CLB("quality Trim Range")
    ;
    private final String description;
    
    private TrimType(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
    
}
