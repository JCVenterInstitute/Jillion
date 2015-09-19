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
package org.jcvi.jillion.assembly.consed.ace;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.assembly.consed.ace.AbstractAceContigBuilderVisitor;
import org.jcvi.jillion.assembly.consed.ace.AbstractAceFileVisitor;
import org.jcvi.jillion.assembly.consed.ace.AceContig;
import org.jcvi.jillion.assembly.consed.ace.AceContigBuilder;
import org.jcvi.jillion.assembly.consed.ace.AceContigVisitor;
import org.jcvi.jillion.assembly.consed.ace.AceFileDataStore;
import org.jcvi.jillion.assembly.consed.ace.AceFileDataStoreBuilder;
import org.jcvi.jillion.assembly.consed.ace.AceFileVisitorCallback;
import org.jcvi.jillion.assembly.consed.ace.AceParser;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;
public class TestAceTestUtil {

	@Test
	public void testSingleAceVisitor() throws IOException, DataStoreException{
		ResourceHelper resources = new ResourceHelper(TestAceTestUtil.class);
		File aceFile = resources.getFile("files/fluSample.ace");
		
		AceFileDataStore datastore= new AceFileDataStoreBuilder(aceFile)
												.build();
		final AceContig expected = datastore.get("22934-PB1");
		
		AceParser sut = AceTestUtil.createAceHandlerFor(expected);
		
		sut.parse(new AbstractAceFileVisitor() {

			@Override
			public AceContigVisitor visitContig(
					AceFileVisitorCallback callback, String contigId,
					int numberOfBases, int numberOfReads,
					int numberOfBaseSegments, boolean reverseComplemented) {
				
				return new AbstractAceContigBuilderVisitor(contigId, numberOfBases, numberOfReads) {
					
					@Override
					protected void visitContig(AceContigBuilder builder) {
						AceContig actual = builder.build();
						assertEquals(expected, actual);						
					}
				};
			}
			
		});
		
		
	}
}
