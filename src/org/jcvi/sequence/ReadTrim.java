/*
 * Created on Oct 2, 2009
 *
 * @author dkatzel
 */
package org.jcvi.sequence;

import org.jcvi.Range;

public interface ReadTrim {

    String getReadId();
    Range getTrimRange(TrimType trimType);
}
