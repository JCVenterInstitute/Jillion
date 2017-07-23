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
package org.jcvi.jillion.core.qual;

import java.util.Optional;
import java.util.OptionalDouble;

import org.jcvi.jillion.internal.core.EncodedSequence;

/**
 * {@code DefaultEncodedQualitySequence} 
 * decorates an {@link EncodedSequence} to allow  
 * it to implement the {@link QualitySequence}
 * interface.
 * @author dkatzel
 */
final class EncodedQualitySequence extends EncodedSequence<PhredQuality> implements QualitySequence{

   
        private Stats stats;
        
	public EncodedQualitySequence(QualitySymbolCodec codec, byte[] data) {
		super(codec, data);
	}


	@Override
	public int hashCode(){
		return super.hashCode();
	}
	/**
    * {@inheritDoc}
    */
    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (!(obj instanceof QualitySequence)){
        	return false;
        }
        return super.equals(obj);
    }
    
    /**
	 * {@inheritDoc}
	 */
	@Override
    public byte[] toArray(){
       return ((QualitySymbolCodec)getCodec()).toQualityValueArray(data);
    }


	@Override
	public OptionalDouble getAvgQuality() {
	   
	    computeStatsIfNeeded();
	    if(stats.avg < 0){
	        return OptionalDouble.empty();
            }
		return OptionalDouble.of(stats.avg);
	}


	@Override
	public Optional<PhredQuality> getMinQuality() {
	    computeStatsIfNeeded();
	    byte value = stats.min;
	    if(value < 0){
	        return Optional.empty();
	    }
	    return Optional.of(PhredQuality.valueOf(value));
	}


	@Override
	public Optional<PhredQuality> getMaxQuality() {
	    computeStatsIfNeeded();
            byte value = stats.max;
            if(value < 0){
                return Optional.empty();
            }
            return Optional.of(PhredQuality.valueOf(value));
	}

	private void computeStatsIfNeeded(){
	    if(stats !=null){
	        return;
	    }
	   
	    byte[] array = toArray();
	    if(array.length==0){
	        stats= new Stats((byte)-1, (byte)-1, -1);
	        return;
	    }
	    byte min = Byte.MAX_VALUE;
	    byte max = Byte.MIN_VALUE;
	    long total = 0L;
	    
	    for(int i=0; i< array.length; i++){
	        byte v = array[i];
	        if( v < min){
	            min = v;
	        }
	        if(v > max){
	            max = v;
	        }
	        total +=v;
	    }
	    
	    stats = new Stats(min, max, total/ (double)array.length);
	}

    @Override
    public QualitySequenceBuilder toBuilder() {
        //this is a work around to fix the problem
        //of calling toBuilder() using reflection
        //which doesn't see default methods.
        //The reflection calls are used in some of the 
        //qualityFastaDataStore implementations.
        //
        //without this override, we will get an AbstractMethodError
        return QualitySequence.super.toBuilder();
    }
	
	

    private static class Stats{
        private final byte min, max;
        private final double avg;
        
        protected Stats(byte min, byte max, double avg) {
            this.min = min;
            this.max = max;
            this.avg = avg;
        }
        
    }
}
