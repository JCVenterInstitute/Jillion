/*
 * Created on Jul 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace;

public interface TraceIdGenerator<Type,Id> {

    Id generateIdFor(Type input);
}
