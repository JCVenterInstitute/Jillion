package org.jcvi.jillion.spi;

import org.jcvi.jillion.core.residue.Residue;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.nt.Nucleotide;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class ResidueHelper {

    private ResidueHelper(){
        //can not instantiate
    }


    public static <R extends Residue<R> > Stream<R> getAllResiduesFor(Class<R> residueType){
        Stream<?> stream;
        if(residueType== Nucleotide.class){
            stream = Nucleotide.getAllValues().stream();

        }else{
            stream = Arrays.stream(AminoAcid.values());
        }

        return stream
                .map(n-> (R)n);

    }
}
