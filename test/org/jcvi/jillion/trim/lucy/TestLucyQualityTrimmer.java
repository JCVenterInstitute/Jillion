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
package org.jcvi.jillion.trim.lucy;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceDataStore;
import org.jcvi.jillion.fasta.FastaRecordDataStoreAdapter;
import org.jcvi.jillion.fasta.qual.QualityFastaFileDataStoreBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.trim.QualityTrimmer;
import org.jcvi.jillion.trim.lucy.LucyQualityTrimmerBuilder;
import org.junit.Before;
import org.junit.Test;

/**
 * @author dkatzel
 *
 *
 */
public class TestLucyQualityTrimmer {
    private final ResourceHelper resources;
    private QualitySequenceDataStore qualities;
    
    QualityTrimmer sut = new LucyQualityTrimmerBuilder(30)
                            .addTrimWindow(30, 0.01F)
                            .addTrimWindow(10, 0.035F)
                            .build();
    
    public TestLucyQualityTrimmer(){
    	resources = new ResourceHelper(TestLucyQualityTrimmer.class);
    }
    @Before
    public void setup() throws  IOException{
        File qualFile = resources.getFile("files/fullLength.qual");
		qualities = FastaRecordDataStoreAdapter.wrap(QualitySequenceDataStore.class, 
				new QualityFastaFileDataStoreBuilder(qualFile).build());
    }
    
    @Test
    public void SAJJA07T27G07MP1F() throws DataStoreException{
        final QualitySequence fullQualities = qualities.get("SAJJA07T27G07MP1F");
        Range actualTrimRange = sut.trim(fullQualities);
        Range expectedRange = Range.of(CoordinateSystem.RESIDUE_BASED, 12,678);
        assertEquals(expectedRange, actualTrimRange);
    }
    @Test
    public void SAJJA07T27G07MP675R() throws DataStoreException{
        final QualitySequence fullQualities = qualities.get("SAJJA07T27G07MP675R");
        Range actualTrimRange = sut.trim(fullQualities);
        Range expectedRange = Range.of(CoordinateSystem.RESIDUE_BASED, 16,680);
        assertEquals(expectedRange, actualTrimRange);
    }
    
    @Test
    public void noGoodQualityDataShouldReturnEmptyRange() throws FileNotFoundException, IOException, DataStoreException{
        File qualFile = resources.getFile("files/bad.qual");
		QualitySequenceDataStore badQualDataStore =FastaRecordDataStoreAdapter.wrap(QualitySequenceDataStore.class, 
				new QualityFastaFileDataStoreBuilder(qualFile).build());
        final QualitySequence badQualities = badQualDataStore.get("SCJIA01T48H08PB26F");
        assertEquals(new Range.Builder().build(), sut.trim(badQualities));
    }
    
    @Test
    public void bTrashShouldReturnEmptyRange() throws FileNotFoundException, IOException, DataStoreException{
        File qualFile = resources.getFile("files/trash.qual");
		QualitySequenceDataStore trashQualDataStore =FastaRecordDataStoreAdapter.wrap(QualitySequenceDataStore.class, 
				new QualityFastaFileDataStoreBuilder(qualFile).build());
        final QualitySequence trashQualities = trashQualDataStore.get("JBYHA01T19A06PB2A628FB");
        assertEquals(new Range.Builder().build(), sut.trim(trashQualities));
    }
    
    @Test
    public void wTrashShouldReturnEmptyRange() throws FileNotFoundException, IOException, DataStoreException{
        File qualFile = resources.getFile("files/trash.qual");
		QualitySequenceDataStore trashQualDataStore =FastaRecordDataStoreAdapter.wrap(QualitySequenceDataStore.class, 
				new QualityFastaFileDataStoreBuilder(qualFile).build());
        final QualitySequence trashQualities = trashQualDataStore.get("JBZTB06T19E09NA1F");
        assertEquals(new Range.Builder().build(), sut.trim(trashQualities));
    }
    
    @Test
    public void twoGoodQualityRegionsShouldShiftDownstreamRegionCoordinatesCorrectly() throws IOException, DataStoreException{
    	 File qualFile = resources.getFile("files/2regions.qual");
 		QualitySequenceDataStore datastore =FastaRecordDataStoreAdapter.wrap(QualitySequenceDataStore.class, 
 				new QualityFastaFileDataStoreBuilder(qualFile).build());
         final QualitySequence quals = datastore.get("JUZHA08T56D09NS903R");
         assertEquals(Range.of(84,374), sut.trim(quals));
    }
}
