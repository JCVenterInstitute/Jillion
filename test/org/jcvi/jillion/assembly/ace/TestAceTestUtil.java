package org.jcvi.jillion.assembly.ace;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;
public class TestAceTestUtil {

	@Test
	public void testSingleAceVisitor() throws IOException, DataStoreException{
		ResourceHelper resources = new ResourceHelper(TestAceTestUtil.class);
		File aceFile = resources.getFile("files/fluSample.ace");
		
		AceFileContigDataStore datastore= new AceFileDataStoreBuilder(aceFile)
												.build();
		final AceContig expected = datastore.get("22934-PB1");
		
		AceHandler sut = AceTestUtil.createAceHandlerFor(expected);
		
		sut.accept(new AbstractAceFileVisitor2() {

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
