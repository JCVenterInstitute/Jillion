package org.jcvi.jillion.orf;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.Frame;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.Codon;
import org.jcvi.jillion.core.residue.aa.IupacTranslationTables;
import org.jcvi.jillion.core.residue.aa.ProteinSequenceBuilder;
import org.jcvi.jillion.core.residue.aa.TranslationTable;
import org.jcvi.jillion.core.residue.aa.TranslationVisitor;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;

public class OrfFinder {
    public List<Orf> find(NucleotideSequence seq){
        return find(seq, IupacTranslationTables.STANDARD);
    }
    public List<Orf> find(NucleotideSequence seq, TranslationTable translationTable){
        List<Orf> orfs = new ArrayList<>();
        for(Frame f : Frame.values()){
            OrfVisitor visitor = new OrfVisitor(f);
            translationTable.translate(seq, f, visitor);
            visitor.getOrf().ifPresent(orf -> orfs.add(orf));
        }
        
        return orfs;
    }
    
    private static class OrfVisitor implements TranslationVisitor{

        ProteinSequenceBuilder builder = new ProteinSequenceBuilder();
        Frame frame;
        
        public OrfVisitor(Frame frame) {
            this.frame = frame;
        }

        long startCoord =-1; long stopCoord =-1;
        boolean hasStart=false;
        boolean hasStop=false;
        
        Orf orf=null;
        @Override
        public void visitCodon(long nucleotideCoordinate, Codon codon) {
            builder.append(codon.getAminoAcid());
            
        }

        @Override
        public FoundStartResult foundStart(long nucleotideCoordinate,
                Codon codon) {
            if(hasStart){
                //already seen a start
                builder.append(codon.getAminoAcid());
            }else{
            hasStart=true;
            startCoord = nucleotideCoordinate;
          //hardcode an M if this is our first start
            //which may 
            //not be the amino acid returned by 
            //#getAminoAcid() depending on the translation table
            builder.append(AminoAcid.Methionine);
            }
            return FoundStartResult.CONTINUE;
        }

        @Override
        public FoundStopResult foundStop(long nucleotideCoordinate,
                Codon codon) {
            hasStop = true;
            stopCoord = nucleotideCoordinate+=2; // only +2 to include last base in aa
            builder.append(codon.getAminoAcid());
            return FoundStopResult.STOP;
        }

        @Override
        public void end() {
            if(hasStart && hasStop){
                orf = new Orf(frame, builder.build(), Range.of(startCoord, stopCoord));
            }
            
        }
        
        public Optional<Orf> getOrf(){
            return Optional.ofNullable(orf);
        }
        
    }
}
