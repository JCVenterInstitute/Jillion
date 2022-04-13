/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.internal.core.util;
/**
 * Implementation of sneakyThrow originally created by Reinier Zwitserloot
 * 
 * @see <a href="http://www.mail-archive.com/javaposse@googlegroups.com/msg05984.html">Original Java Posse Post by Reinier Zwitserloot</a>
 * @author dkatzel
 * @since 5.3
 */
public class Sneak {
    public static <T> T sneakyThrow(Throwable t) {
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
