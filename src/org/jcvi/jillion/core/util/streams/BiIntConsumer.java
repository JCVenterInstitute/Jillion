package org.jcvi.jillion.core.util.streams;

@FunctionalInterface
public interface BiIntConsumer {
    /**
     * Performs this operation on the given arguments.
     *
     * @param x the first input argument
     * @param y the second input argument
     */
    void accept(int x, int y);
}
