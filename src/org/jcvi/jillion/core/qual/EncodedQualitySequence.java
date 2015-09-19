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

import org.jcvi.jillion.internal.core.EncodedSequence;

/**
 * {@code DefaultEncodedQualitySequence} 
 * decorates an {@link EncodedSequence} to allow  
 * it to implement the {@link QualitySequence}
 * interface.
 * @author dkatzel
 */
final class EncodedQualitySequence extends EncodedSequence<PhredQuality> implements QualitySequence{

   
   

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
	public byte[] toArray(){
       return ((QualitySymbolCodec)getCodec()).toQualityValueArray(data);
    }


	@Override
	public double getAvgQuality() {
		return ((QualitySymbolCodec)getCodec()).getAvgQuality(data);
	}


	@Override
	public PhredQuality getMinQuality() {
		return ((QualitySymbolCodec)getCodec()).getMinQuality(data);
	}


	@Override
	public PhredQuality getMaxQuality() {
		return ((QualitySymbolCodec)getCodec()).getMaxQuality(data);
	}
	
	
}
