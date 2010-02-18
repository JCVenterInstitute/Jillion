/*
 * Created on Jul 16, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace;


public interface TraceFileNameIdGenerator extends TraceIdGenerator<String, String>{

    String generateIdFor(String fileName);
}
