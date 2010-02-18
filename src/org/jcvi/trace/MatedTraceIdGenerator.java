/*
 * Created on Jul 21, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace;

import java.util.List;

public interface MatedTraceIdGenerator<Type,Id> {

    List<Id> generateIdsFor(Type forward, Type reverse);
}
