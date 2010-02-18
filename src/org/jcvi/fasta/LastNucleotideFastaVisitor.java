/*
 * Created on Nov 11, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

public class LastNucleotideFastaVisitor extends SingleNucleotideFastaVisitor{

    @Override
    protected boolean acceptVisitedFasta() {
        //always accept
        return true;
    }

    

}
