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
package org.jcvi.jillion.align.exonerate.vulgar;

import java.util.HashMap;
import java.util.Map;

public enum VulgarOperation {


        Match("M"),
        Codon("C"),
        Gap("G"),
        Non_equivalenced_region("N"),
        Splice_5("5"),
        Splice_3("3"),
        Intron("I"),
        Split_Codon("S"),
        Frameshift("F")
        ;
    
        private String code;
        
        
        private static Map<String, VulgarOperation> map;
        static{
            map = new HashMap<>();
            for(VulgarOperation o : values()){
                map.put(o.code, o);
            }
        }
        VulgarOperation(String code){
            this.code = code;
        }

        public static VulgarOperation getByCode(String code){
            return map.get(code);
        }

}
