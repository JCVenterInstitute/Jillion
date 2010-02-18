/*
 * Created on Dec 11, 2008
 *
 * @author dkatzel
 */
package org.jcvi.assembly.annot.ref;

public interface Snp {

    int getBin();
    String getChromosome();
    int getStartOffset();
    int getLength();
    String getName();
    int getScore();
    String getNcbiReference();
    String getUscsReference();
}
