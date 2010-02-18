/*
 * Created on Jul 17, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.frg;

public enum FrgVisitorAction {
    ADD,
    MODIFY,
    DELETE,
    IGNORE;
    
    public static FrgVisitorAction parseAction(char action){
        switch(action){
            case 'A': return ADD;
            case 'M': return MODIFY;
            case 'D': return DELETE;
            case 'I': return IGNORE;
            default:
                throw new IllegalArgumentException("not a Frg action : "+ action);
        }
    }
}
