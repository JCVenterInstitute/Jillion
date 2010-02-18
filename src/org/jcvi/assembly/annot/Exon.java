/*
 * Created on Dec 16, 2008
 *
 * @author dkatzel
 */
package org.jcvi.assembly.annot;

public interface Exon {

    long getStartPosition();
    long getEndPosition();
    Frame getFrame();
}
