/*
 * Created on Jan 20, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly;



public interface PlacedContigClone extends Placed{

    Clone getClone();
    Contig getContig();


}
