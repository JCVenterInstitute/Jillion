package org.jcvi.jillion.align.pairwise;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.jcvi.jillion.align.AminoAcidSubstitutionMatrix;
import org.jcvi.jillion.align.BlosumMatrices;
import org.jcvi.jillion.align.NucleotideSubstitutionMatrices;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.junit.Test;
public class TestMultipleLocalAlignment {

    @Test
    public void multipleLocalNucleotideMatches(){
        //                                                           1         2         3         4         5         6
        //                                                 0123456789012345678901234567890123456789012345678901234567890123456
        //                                                          ACCGGT   ACCGGT A-CCGGT             ACCGGT   ACCGGT
     //NucleotideSequence subject = NucleotideSequence.of("AAAACCCCCACCGGTTTTACCGGTTACCCGGTAATTGTGTGTGTGACGGGTTTTA-C-GTTTAAAA");
       NucleotideSequence subject = NucleotideSequence.of("AAAACCCCCACCGGTTTTACCGGTTACCCGGTAATTGTGTGTGTGACGGGTTTTACGTTTAAAA");
       NucleotideSequence query = NucleotideSequence.of("ACCGGT");
       
       List<NucleotidePairwiseSequenceAlignment> alignments = PairwiseAlignmentBuilder.createNucleotideAlignmentBuilder(query, subject,
                                                                                                               NucleotideSubstitutionMatrices.getNuc44())
                                                                                                              .gapPenalty(-2)
                                                                                                             .findMultiple()
                                                                                                             .filter(a-> a.getScore() > 15)
                                                                                                             .collect(Collectors.toList());
       
     
//       System.out.println("num alignments found = "+ alignments.size());
//       
//       alignments.forEach(alignment ->{
//           System.out.println(alignment);
//       });
       assertEquals(5, alignments.size());
       assertEquals(Range.of(9,14), alignments.get(0).getSubjectRange().asRange());
       assertEquals(Range.of(18,23), alignments.get(1).getSubjectRange().asRange());
       assertEquals(Range.of(25,31), alignments.get(2).getSubjectRange().asRange());
       assertEquals(Range.of(45,50), alignments.get(3).getSubjectRange().asRange());
       assertEquals(Range.of(54,57), alignments.get(4).getSubjectRange().asRange());
    }
    
    @Test
    public void multipleProteins(){
        AminoAcidSubstitutionMatrix blosom50 = BlosumMatrices.blosum50();
        
        ProteinSequence subject = ProteinSequence.of("FVSPREAKRQMKKDLKSQEPCAGLPTKVSHRTSPALKTLEPMWMDSNRTAALRASFLKCQKK*"
                                                    + "TPELSHF*RQHHALSNYLTGLPALNGRSSC*WMPLN*ASKTRVMRGRVYRYMMQSNA*RHFSAGKSPTL*NHMKRA*"
                                                    + "TPITSWLGSKCWQNSKILKMRRKSQKQRT*RKRAS*SGHLVRIWHRRR*TLRIARMLAI*DSMTVMNQSLDR*QAGSR"
                                                    + "VNSTRHVN*QIQVGLSLMK*GKTLLQLSTLRV*EETTSQRKYPIAGLLNT**KECT*TQPC*MHPVQPWMTSN*FQ**A"
                                                    + "NAGPKKGGGRLICMDSL*KEDPI*EMTPM**TL*AWNSLLLTRGWSHTSGKSTVFSR*ETCSYGLQ*AKCQGPCSCM*EP"
                                                    + "MGLPRSR*SGAWK*GDAFFNPFNKLRA*LKPSLLSKRRT*PKNSLKTNQKHGQLESHPKGWRKAPLGRCAEPYWQNLYSTA"
                                                    + "YMHLHNSRDFQLNQESCFSLSRHLGTTWNLGPSILGGYMKQLRSA*LMIPGFCLMRLGSTPSSHMH*NSCGNATICYPYCPK"
                +"FVSPREEKRQLKKGLKSQEQCASLPTKVSRRTSPALKILEPM"                                    
                );
        
        
        ProteinSequence query = ProteinSequence.of("FVSPREEKRQLKKGLKSQEQCASLPTKVSRRTSPALKILEPM");
        
        List<ProteinPairwiseSequenceAlignment> actual = PairwiseAlignmentBuilder.createProtienAlignmentBuilder(query, subject, blosom50)
                                                            .gapPenalty(-8, -8)
                                                            .findMultiple()
                                                            .filter(a-> a.getScore() > 100)
                                                            .collect(Collectors.toList());
        
        assertEquals(2, actual.size());
        assertEquals(Range.of(540, 581), actual.get(0).getSubjectRange().asRange());
        assertEquals(Range.of(0,41), actual.get(1).getSubjectRange().asRange());
        
//        System.out.println("num alignments found = "+ actual.size());
//        
//        actual.forEach(alignment ->{
//            System.out.println(alignment);
//        });
    }
}
