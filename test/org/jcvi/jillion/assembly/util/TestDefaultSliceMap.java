/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public License.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.assembly.util;

import org.jcvi.jillion.assembly.AssembledRead;
import org.jcvi.jillion.assembly.Contig;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;

/**
 * @author dkatzel
 *
 *
 */
public class TestDefaultSliceMap extends AbstractTestSliceMap{

    /**
    * {@inheritDoc}
    */
    @Override
    protected SliceMap createSliceMapFor(Contig<AssembledRead> contig,
            QualitySequenceDataStore qualityDatastore, GapQualityValueStrategy qualityValueStrategy) {
        try {
			return DefaultSliceMap.create(contig,
			        qualityDatastore, qualityValueStrategy);
		} catch (DataStoreException e) {
			throw new RuntimeException("error building slice map", e);
		}
    }

}
