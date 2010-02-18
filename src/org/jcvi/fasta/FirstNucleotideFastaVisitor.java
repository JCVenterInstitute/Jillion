/*
 * Created on Nov 11, 2009
 *
 * @author dkatzel
 */
package org.jcvi.fasta;

public class FirstNucleotideFastaVisitor extends SingleNucleotideFastaVisitor{

    
    @Override
    protected boolean acceptVisitedFasta() {        
        return getRecord()==null;
    }

}
