/*
 * Created on Sep 1, 2009
 *
 * @author dkatzel
 */
package org.jcvi;
/**
 * {@code IdConverter} converts an Id of
 * one type into the Id of another
 * ( which might be a different type)
 * that represents the same thing.
 * <p>
 * For Example, IdConverter can be used
 * to map ids to external ids used by a different
 * system.
 * 
 * @author dkatzel
 *
 *
 */
public interface IdConverter<K,V> {
    /**
     * Converts the given id into 
     * its corresponding other id.
     * @param id the id to convert.
     * @return another id that represents the same object.
     */
    V convertId(K id);
}
