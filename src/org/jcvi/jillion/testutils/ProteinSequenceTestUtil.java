package org.jcvi.jillion.testutils;

import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.residue.aa.ProteinSequenceBuilder;

import java.util.EnumSet;
import java.util.Random;

public final class ProteinSequenceTestUtil {
    private static Random random = new Random();
    private static AminoAcid NON_GAPS[];

    static{

        NON_GAPS =  EnumSet.complementOf(EnumSet.of(AminoAcid.Gap))
                .stream().toArray(i-> new AminoAcid[i]);
    }

    private ProteinSequenceTestUtil(){
        //can not instantiate
    }

    public static ProteinSequence randomSequence(int length){
        ProteinSequenceBuilder builder = new ProteinSequenceBuilder(length);
        for(int i=0; i<length; i++){
            builder.append(NON_GAPS[random.nextInt(NON_GAPS.length)]);
        }
        builder.turnOffDataCompression(true);
        return builder.build();
    }
}
