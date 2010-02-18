/*
 * Created on Jan 6, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.util.Date;

import org.jcvi.assembly.Placed;

public class DefaultReadAceTag extends AbstractDefaultPlacedAceTag implements ReadAceTag{

    public DefaultReadAceTag(String id, String type, String creator,
            Date creationDate, Placed location, boolean isTransient) {
        super(id, type, creator, creationDate, location, null, isTransient);
    }
    
    

}
