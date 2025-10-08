package org.jcvi.jillion.core.util;

import java.util.function.IntUnaryOperator;

/**
 * A functional interface to change one int into another.
 * @since 6.1
 */
@FunctionalInterface
public interface ReplacementFunction extends IntUnaryOperator {

}
