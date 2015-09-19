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
package org.jcvi.jillion.assembly.ca.frg;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;

public final class FrgTestUtil {

	private FrgTestUtil(){
		//can not instantiate
	}
	
	public static final int ENCODING_ORIGIN = 0x30;
    public static  QualitySequence decodeQualitySequence(String encodedValues){
    	QualitySequenceBuilder builder = new QualitySequenceBuilder(encodedValues.length());
    	for(int i=0; i<encodedValues.length(); i++){
    		builder.append(encodedValues.charAt(i) - ENCODING_ORIGIN);
    	}
    	
    	return builder.build();
    }
}
