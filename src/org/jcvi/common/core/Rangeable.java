package org.jcvi.common.core;

/**
 * {@code Rangeable} is a interface
 * to denote that an object can be expressed
 * as a {@link Range}.
 * @author dkatzel
 *
 */
public interface Rangeable {

	Range asRange();
}
