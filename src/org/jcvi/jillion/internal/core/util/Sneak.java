package org.jcvi.jillion.internal.core.util;
/**
 * Implementation of sneakyThrow originally created by Reinier Zwitserloot
 * 
 * @see <a href="http://www.mail-archive.com/javaposse@googlegroups.com/msg05984.html">Original Java Posse Post by Reinier Zwitserloot</a>
 * @author dkatzel
 * @since 5.3
 */
public class Sneak {
    public static RuntimeException sneakyThrow(Throwable t) {
        if (t == null)
            throw new NullPointerException("t");
        Sneak.<RuntimeException> sneakyThrow0(t);
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void sneakyThrow0(Throwable t) throws T {
        throw (T) t;
    }
}
