/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
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
package org.jcvi.common.examples;

import java.math.BigDecimal;

import org.jcvi.jillion.internal.core.util.iter.AbstractBlockingStreamingIterator;

/**
 * Example of using a {@link BlockableStreamingIterator}
 * to compute more and more accurate values of PI.
 * @author dkatzel
 */
public class BlockableStreamingIterator {
    /**
     * Example implementation of {@link AbstractBlockingStreamingIterator}
     * which will compute the approximate value of PI.
     * Each {@link BigDecimal} returned by this iterator
     * will be a more accurate approximation.
     * @author dkatzel
     */
    private static class ApproximatePiIterator extends AbstractBlockingStreamingIterator<BigDecimal>{
        private final int numOfIterations;
        public static final BigDecimal FOUR = BigDecimal.valueOf(4);
        /**
         * Constructor.
         * @param numOfIterations the number of approximate values
         * to iterate over.  Each value returned will be closer
         * and closer to the real value of PI.
         */
        public ApproximatePiIterator(int numOfIterations) {
            this.numOfIterations = numOfIterations;
        }


        /**
        * 
        * <p/>
        * Computes the value of &pi; using the Madhava–Leibniz series:
        * <p/>
        * &pi; = 4 &sum; (-1)<sup>k</sup> / (2k + 1)
        * <p/>
        * This is a simple to implement algorithm
        * but is very inefficient since thousands of iterations need to
        * be made to get a reasonable answer.  This is only
        * should be used for example purposes please use {@link Math#PI}
        * for any real work that needs PI.
        * <p/>
        * {@inheritDoc}
        */
        @Override
        protected void backgroundThreadRunMethod() throws RuntimeException {
            BigDecimal currentValue = BigDecimal.valueOf(1);
            this.blockingPut(FOUR);
            for(int i=1; i<numOfIterations; i++){
                BigDecimal x = BigDecimal.valueOf(1D/(2*i+1));
                if(i%2==0){
                    currentValue = currentValue.add(x);
                }else{
                    currentValue = currentValue.subtract(x);
                }
                this.blockingPut(currentValue.multiply(FOUR));
            }
        }
        
    }
    
    public static void main(String[] args){
        ApproximatePiIterator approxPi = new ApproximatePiIterator(1000000);
        approxPi.start();
        while(approxPi.hasNext()){
            System.out.println(approxPi.next());
        }
    }
}
