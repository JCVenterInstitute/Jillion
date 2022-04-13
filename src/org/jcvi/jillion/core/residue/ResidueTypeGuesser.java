package org.jcvi.jillion.core.residue;

import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.core.util.SingleThreadAdder;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public final class ResidueTypeGuesser {

    private ResidueTypeGuesser(){
        //can not instantiate
    }
    public enum ResidueTypeGuessedResult{
        DNA,
        RNA,
        AMINO_ACID,
        UNKNOWN
        ;

        protected static ResidueTypeGuessedResult combine(Map<ResidueTypeGuessedResult, SingleThreadAdder> counts){
            if(counts.size() ==1){
                return counts.keySet().iterator().next();
            }

            if(counts.containsKey(UNKNOWN)){
                return UNKNOWN;
            }
            if(counts.containsKey(DNA) || counts.containsKey(RNA)){
                if(counts.containsKey(AMINO_ACID)){
                    //most amino acids are also valid dna
                    int numNucleicAcid = counts.getOrDefault(DNA, new SingleThreadAdder()).intValue();
                    numNucleicAcid += counts.getOrDefault(RNA, new SingleThreadAdder()).intValue();

                    int aa = counts.get(AMINO_ACID).intValue();
                    if(aa > numNucleicAcid){
                       //all bases that are amino acides are also dna
                        return AMINO_ACID;
                    }

                }
                if(counts.containsKey(DNA)){
                    if(counts.containsKey(RNA)){
                        return RNA;
                    }
                    return DNA;
                }
                //if here it's RNA without DNA and AA
                return RNA;
            }
            return UNKNOWN;
        }

    }

    public static ResidueTypeGuessedResult guessSequenceType(String sequence){
        char[] chars = sequence.toCharArray();
        Map<ResidueTypeGuessedResult, SingleThreadAdder> counts = new EnumMap<>(ResidueTypeGuessedResult.class);

        for(int i=0; i< chars.length; i++){
            char c = chars[i];
            if(Character.isWhitespace(c)){
                continue;
            }
            Nucleotide n = Nucleotide.safeParse(chars[i]);
            if(n !=null){
                //it's a nucleotide
                if(n == Nucleotide.Uracil){
                    counts.computeIfAbsent(ResidueTypeGuessedResult.RNA, k-> new SingleThreadAdder()).increment();
                }else{
                    counts.computeIfAbsent(ResidueTypeGuessedResult.DNA, k-> new SingleThreadAdder()).increment();

                }
            }
            //if we are here it's not DNA or whitespace
            AminoAcid aa = AminoAcid.safeParse(Character.toString(c));
            if(aa !=null){
                counts.computeIfAbsent(ResidueTypeGuessedResult.AMINO_ACID, k-> new SingleThreadAdder()).increment();

            }else if(n ==null){
                //isn't whitespace, dna or rna
                counts.computeIfAbsent(ResidueTypeGuessedResult.UNKNOWN, k-> new SingleThreadAdder()).increment();

            }

        }
        return ResidueTypeGuessedResult.combine(counts);


    }
}
