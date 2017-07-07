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
package org.jcvi.jillion.core.io;
/**
 * Helper class to compute buffer sizes in bytes.
 * 
 * @author dkatzel
 *
 *
 * @since 5.3
 */
public final class BufferSize {

    private static int MAX_KB = Integer.MAX_VALUE>>10;
    private static int MAX_MB = Integer.MAX_VALUE>>20;
    
    private static final int CACHE_SIZE = 65;
    private static final int[] ks = new int[CACHE_SIZE];
    private static final int[] mbs = new int[CACHE_SIZE];
    static{
        for(int i=1; i< CACHE_SIZE; i++){
            ks[i] = i<<10;
            mbs[i] = i<<20;
        }
    }
    private BufferSize(){
        //can not instantiate
    }
    /**
     * Get the number of bytes in the given Kilobyte size.
     * For example, kb(1) will return 1024.
     * 
     * @param size the number of kilobytes to compute.
     * @return the number of bytes that is.
     * 
     * @throws IllegalArgumentException if size is negative or
     * would produce a value larger than {@link Integer#MAX_VALUE}.
     * 
     */
    public static int kb(int size){
       if(size < 0 || size > MAX_KB){
           throw new IllegalArgumentException("number of kb out of range : " + size + "kb");
       }
        if(size< CACHE_SIZE){
            return ks[size];
        }
        return size<<10;
    }
    /**
     * Get the number of bytes in the given Megabyte size.
     * For example, mb(1) will return 1048576.
     * 
     * @param size the number of megabytes to compute.
     * @return the number of bytes that is.
     * 
     * @throws IllegalArgumentException if size is negative or
     * would produce a value larger than {@link Integer#MAX_VALUE}.
     * 
     */
    public static int mb(int size){
        if(size < 0 || size > MAX_MB){
            throw new IllegalArgumentException("number of mb out of range : " + size + "mb");
        }
        if(size< CACHE_SIZE){
            return mbs[size];
        }
        return size<<20;
    }
    
}
