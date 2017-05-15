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
