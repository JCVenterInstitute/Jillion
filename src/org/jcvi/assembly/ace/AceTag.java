/*
 * Created on Jan 5, 2010
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.util.Date;


public interface AceTag{

    String getType();
    String getCreator();
    Date getCreationDate();
    /**
     * Get the data (not counting header info or comments) in the tag as a String.
     * @return the data or {@code null} if no Data exists.
     */
    String getData();
}
