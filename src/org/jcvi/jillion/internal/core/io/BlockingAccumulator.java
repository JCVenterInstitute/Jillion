package org.jcvi.jillion.internal.core.io;

import org.jcvi.jillion.internal.core.util.Sneak;

import java.util.concurrent.BlockingQueue;

public class BlockingAccumulator {

    private  final BlockingQueue<Object> queue;

    public BlockingAccumulator(BlockingQueue<Object> queue) {
        this.queue = queue;
    }

    public void put(Object o) {
        try {
            queue.put(o);
        } catch (InterruptedException e) {
            Sneak.sneakyThrow(e);
        }
    }
}
