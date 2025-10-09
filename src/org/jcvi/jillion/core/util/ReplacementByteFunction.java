package org.jcvi.jillion.core.util;

import java.util.function.IntUnaryOperator;

/**
 * A functional interface to change one byte into another.
 * @since 6.1
 */
@FunctionalInterface
public interface ReplacementByteFunction {

    byte applyAsByte(byte value);
}
