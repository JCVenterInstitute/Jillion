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
package org.jcvi.jillion.experimental.align;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.easymock.EasyMockSupport;
import org.easymock.IAnswer;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.experimental.align.AlnGroupVisitor.ConservationInfo;
import org.jcvi.jillion.experimental.align.AlnVisitor.AlnVisitorCallback;
import org.jcvi.jillion.experimental.align.AlnVisitor.AlnVisitorCallback.AlnVisitorMemento;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Before;
import org.junit.Test;
/**
 * @author dkatzel
 *
 *
 */
public class TestAlnFileParser extends EasyMockSupport{

    private final ResourceHelper resources = new ResourceHelper(TestAlnFileParser.class);
    private AlnVisitor sut;
    private  Set<String> ids;
    
    private AlnVisitorCallback tempCallback;
    private AlnVisitorMemento tempMemento;
    
    
    @Before
    public void setup(){
        sut = createMock(AlnVisitor.class);
        
        ids = new HashSet<String>();
        
        ids.add("gi|304633245|gb|HQ003817.1|");
        ids.add("gi|317140354|gb|HQ413315.1|");
        ids.add("gi|33330439|gb|AF534906.1|");
        ids.add("gi|9626158|ref|NC_001405.1|");
        ids.add("gi|56160492|ref|AC_000007.1|");
        ids.add("gi|33465830|gb|AY339865.1|");
        ids.add("gi|58177684|gb|AY601635.1|");
        
        sut.visitHeader(isA(String.class));
    }
    
    @Test
    public void testInputStreamCanOnlyBeParsedOnce() throws IOException{
        setupVisitAllGroups();
        replayAll();
        InputStream in =null;
        try{
            in = resources.getFileAsStream("files/example.aln");
            AlnParser parser = AlnFileParser.create(in);
            assertTrue(parser.canParse());
			parser.parse(sut);
            verifyAll();
            assertFalse(parser.canParse());
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    
    @Test
    public void inputStreamCanNotCreateMementos() throws IOException{
    	expect(sut.visitGroup(eq(ids), isA(AlnVisitorCallback.class))).andAnswer(new IAnswer<AlnGroupVisitor>() {

			@Override
			public AlnGroupVisitor answer() throws Throwable {
				AlnVisitorCallback callback =(AlnVisitorCallback)getCurrentArguments()[1];
				assertFalse(callback.canCreateMemento());
				try{
					callback.createMemento();
					fail("should throw exception if memento can not be created");
				}catch(UnsupportedOperationException expected){
					//expected.printStackTrace();
				}
				callback.haltParsing();
				return null;
			}
		
        });
        sut.halted();
        replayAll();
        InputStream in =null;
        try{
            in = resources.getFileAsStream("files/example.aln");
            AlnParser parser = AlnFileParser.create(in);
            assertTrue(parser.canParse());
			parser.parse(sut);
            verifyAll();
            assertFalse(parser.canParse());
        }finally{
            IOUtil.closeAndIgnoreErrors(in);
        }
    }
    @Test
    public void testFileCanBeParsedMultipleTimes() throws IOException{
        setupVisitAllGroups();
        replayAll();
        AlnParser parser = AlnFileParser.create(resources.getFile("files/example.aln"));
        assertTrue(parser.canParse());
		parser.parse(sut);
		 assertTrue(parser.canParse());
        verifyAll();
    }
    
    @Test
    public void skipAllGroups() throws IOException{
    	setupSkipAllGroups();
        replayAll();
        AlnParser parser = AlnFileParser.create(resources.getFile("files/example.aln"));
        assertTrue(parser.canParse());
		parser.parse(sut);
		 assertTrue(parser.canParse());
        verifyAll();
    }
    @Test
    public void skipGroup2() throws IOException{
    	setupSkipGroup2Only();
        replayAll();
        AlnParser parser = AlnFileParser.create(resources.getFile("files/example.aln"));
        assertTrue(parser.canParse());
		parser.parse(sut);
		 assertTrue(parser.canParse());
        verifyAll();
    }
    @Test
    public void haltAtGroup2() throws IOException{
    	setupHaltAtGroup2();
        replayAll();
        AlnParser parser = AlnFileParser.create(resources.getFile("files/example.aln"));
        assertTrue(parser.canParse());
		parser.parse(sut);
		 assertTrue(parser.canParse());
        verifyAll();
    }
    @Test
    public void haltAtInsideGroup1End() throws IOException{
    	setupHaltInsideGroup1();
        replayAll();
        AlnParser parser = AlnFileParser.create(resources.getFile("files/example.aln"));
        assertTrue(parser.canParse());
		parser.parse(sut);
		 assertTrue(parser.canParse());
        verifyAll();
    }
    @Test
    public void haltAtInsideGroup3End() throws IOException{
    	setupHaltInsideGroup3();
        replayAll();
        AlnParser parser = AlnFileParser.create(resources.getFile("files/example.aln"));
        assertTrue(parser.canParse());
		parser.parse(sut);
		 assertTrue(parser.canParse());
        verifyAll();
    }
    
    
    @Test
    public void useMementoToRevisitGroup2And3() throws IOException{
    	setupVisitAllGroupsAndThenRewindAndReVisit2And3();
        replayAll();
        AlnParser parser = AlnFileParser.create(resources.getFile("files/example.aln"));
        assertTrue(parser.canParse());
		parser.parse(sut);
		 assertTrue(parser.canParse());
		 parser.parse(sut, tempMemento);
		 assertTrue(parser.canParse());
        verifyAll();
    }
    
    
    @Test
    public void notAnAlnFileThrowIOException() throws IOException{
    	String input = "This is not an aln file\nblah blah blah\n#1234";
    	InputStream in =IOUtil.toInputStream(input);
    	AlnVisitor visitor = createMock(AlnVisitor.class);
    	try{
    		 AlnFileParser.create(in).parse(visitor);
    		 fail("should throw IOException");
    	}catch(IOException expected){
    		//pass
    	}finally{
    		IOUtil.closeAndIgnoreErrors(in);
    	}
    	
    }
    
    @Test
    public void parseThrowsException() throws IOException{
    	Throwable expected = new Throwable("expected");
    	setupThrowExceptionInGroup1(expected);
    	replayAll();
    	 AlnParser parser = AlnFileParser.create(resources.getFile("files/example.aln"));
         assertTrue(parser.canParse());
         boolean thrown=false;
 		try{
 			parser.parse(sut);
 		}catch(Throwable t){
 			thrown=true;
 			//the cause is our expected exception
 			assertEquals(expected, t.getCause());
 		}
 		assertTrue("exception not thrown", thrown);
    }
    
    
    /**
     * Don't return a groupVisitor for all groups
     * effectively skipping all groups
     * (even though we parse the entire thing).
     */
    private void setupSkipAllGroups() {       
        
    	skipNextGroup();  
    	skipNextGroup();        
    	skipNextGroup();
        
        sut.visitEnd();
        
    }
    /**
     * Visit group1, then use the callback
     * inside the call to visitGroup2
     * to halt parsing.
     */
    private void setupHaltAtGroup2() {
    	visitGroup1();  
     
        expect(sut.visitGroup(eq(ids), isA(AlnVisitorCallback.class))).andAnswer(new IAnswer<AlnGroupVisitor>() {

			@Override
			public AlnGroupVisitor answer() throws Throwable {
				AlnVisitorCallback callback =(AlnVisitorCallback)getCurrentArguments()[1];
				callback.haltParsing();
				return null;
			}
		
        });
        sut.halted();
    }
    /**
     * Visit group1, then use the callback
     * inside the call to visitGroup2
     * to halt parsing.
     */
    private void setupHaltInsideGroup1() {
    	
    	final AlnGroupVisitor group1 = setupGroup1ExpectationsAndHaltCallbackAtEndOfGroup();       
        expect(sut.visitGroup(eq(ids), isA(AlnVisitorCallback.class))).andAnswer(new IAnswer<AlnGroupVisitor>() {

			@Override
			public AlnGroupVisitor answer() throws Throwable {
				tempCallback = (AlnVisitorCallback)getCurrentArguments()[1];
				
				return group1;
			}
        	
		});
        sut.halted();
    }
    /**
     * Visit group1, then use the callback
     * inside the call to visitGroup2
     * to halt parsing.
     */
    private void setupHaltInsideGroup3() {
    	visitGroup1();
    	visitGroup2();
    	final AlnGroupVisitor group3 = setupGroup3ExpectationsAndHaltCallbackAtEndOfGroup();       
        expect(sut.visitGroup(eq(ids), isA(AlnVisitorCallback.class))).andAnswer(new IAnswer<AlnGroupVisitor>() {

			@Override
			public AlnGroupVisitor answer() throws Throwable {
				tempCallback = (AlnVisitorCallback)getCurrentArguments()[1];
				
				return group3;
			}
        	
		});
        sut.halted();
    }
    
    /**
     * Visit group1, then use the callback
     * inside the call to visitGroup2
     * to halt parsing.
     */
    private void setupThrowExceptionInGroup1(final Throwable exception) {
    	
    	setupGroup1ExpectationsAndHaltCallbackAtEndOfGroup();       
        expect(sut.visitGroup(eq(ids), isA(AlnVisitorCallback.class))).andAnswer(new IAnswer<AlnGroupVisitor>() {

			@Override
			public AlnGroupVisitor answer() throws Throwable {
				throw exception;
			}
        	
		});
        sut.halted();
    }

	private void haltCallback(){
    	tempCallback.haltParsing();
    }
    /**
     * Don't return a groupVisitor for all groups
     * effectively skipping all groups
     * (even though we parse the entire thing).
     */
    private void setupSkipGroup2Only() {       
        
    	visitGroup1();  
    	skipNextGroup();        
    	visitGroup3();
        
        sut.visitEnd();
        
    }
    private void skipNextGroup() {
        expect(sut.visitGroup(eq(ids), isA(AlnVisitorCallback.class))).andReturn(null);
    }
    /**
     * 
     */
    private void setupVisitAllGroups() {       
        
        visitGroup1();  
        visitGroup2();        
        visitGroup3();
        
        sut.visitEnd();
        
    }
    /**
     * 
     */
    private void setupVisitAllGroupsAndThenRewindAndReVisit2And3() {  
    	
        visitGroup1();  
        final AlnGroupVisitor group2 = setupGroup2Expectations();
        
        expect(sut.visitGroup(eq(ids), isA(AlnVisitorCallback.class))).andAnswer(new IAnswer<AlnGroupVisitor>() {

			@Override
			public AlnGroupVisitor answer() throws Throwable {
				tempMemento =((AlnVisitorCallback)getCurrentArguments()[1]).createMemento();
				return group2;
			}
		
        });  
        visitGroup3();
        
        sut.visitEnd();
        
        visitGroup2();        
        visitGroup3();
        
        sut.visitEnd();

        
    }
	private void visitGroup3() {
		AlnGroupVisitor group3 = setupGroup3Expectations();        
        expect(sut.visitGroup(eq(ids), isA(AlnVisitorCallback.class))).andReturn(group3);
	}

	private AlnGroupVisitor setupGroup3Expectations() {
		AlnGroupVisitor group3 = createMock(AlnGroupVisitor.class);
        
        group3.visitAlignedSegment("gi|304633245|gb|HQ003817.1|",  "AAGGTATATTATTGATGATG");
        group3.visitAlignedSegment("gi|317140354|gb|HQ413315.1|",  "AAGGTATATTATTGATGATG");
        group3.visitAlignedSegment("gi|33330439|gb|AF534906.1|",   "AAGGTATATTATTGATGATG");
        group3.visitAlignedSegment("gi|9626158|ref|NC_001405.1|",  "AAGGTATATTAT-GATGATG");
        group3.visitAlignedSegment("gi|56160492|ref|AC_000007.1|", "AAGGTATATTAT-GATGATG");
        group3.visitAlignedSegment("gi|33465830|gb|AY339865.1|",   "AAGGTATATTATTGATGATG");
        group3.visitAlignedSegment("gi|58177684|gb|AY601635.1|",   "AAGGTATATTATTGATGATG");
        group3.visitConservationInfo(parseConservationInfoFor("************ *******"));
        group3.visitEndGroup();
		return group3;
	}
	private void visitGroup2() {
		AlnGroupVisitor group2 = setupGroup2Expectations();
        
        expect(sut.visitGroup(eq(ids), isA(AlnVisitorCallback.class))).andReturn(group2);
	}

	private AlnGroupVisitor setupGroup2Expectations() {
		AlnGroupVisitor group2 = createMock(AlnGroupVisitor.class);
       
        group2.visitAlignedSegment("gi|304633245|gb|HQ003817.1|",  "GGGGTGGAGTTTGTGACGTGGCGCGGGGCGTGGGAACGGGGCGGGTGACG");
        group2.visitAlignedSegment("gi|317140354|gb|HQ413315.1|",  "GGGGTGGAGTTTGTGACGTGGCGCGGGGCGTGGGAACGGGGCGGGTGACG");
        group2.visitAlignedSegment("gi|33330439|gb|AF534906.1|",   "GGGGTGGAGTTTGTGACGTGGCGCGGGGCGTGGGAACGGGGCGGGTGACG");
        group2.visitAlignedSegment("gi|9626158|ref|NC_001405.1|",  "GGGGTGGAGTTTGTGACGTGGCGCGGGGCGTGGGAACGGGGCGGGTGACG");
        group2.visitAlignedSegment("gi|56160492|ref|AC_000007.1|", "GGGGTGGAGTTTGTGACGTGGCGCGGGGCGTGGGAACGGGGCGGGTGACG");
        group2.visitAlignedSegment("gi|33465830|gb|AY339865.1|",   "GGGGTGGAGTTTGTGACGTGGCGCGGGGCGTGGGAACGGGGCGGGTGACG");
        group2.visitAlignedSegment("gi|58177684|gb|AY601635.1|",   "GGGGTGGAGTTTGTGACGTGGCGCGGGGCGTGGGAACGGGGCGGGTGACG");
        group2.visitConservationInfo(parseConservationInfoFor("**************************************************"));
        group2.visitEndGroup();
		return group2;
	}
	private void visitGroup1() {
		AlnGroupVisitor group1 = setupGroup1Expectations();        
        expect(sut.visitGroup(eq(ids), isA(AlnVisitorCallback.class))).andReturn(group1);
        
	}

	private AlnGroupVisitor setupGroup1Expectations() {
		AlnGroupVisitor group1 = createMock(AlnGroupVisitor.class);
        group1.visitAlignedSegment("gi|304633245|gb|HQ003817.1|",  "CATCATCAATAATATACCTTATTTTGGATTGAAGCCAATATGATAATGAG");
        group1.visitAlignedSegment("gi|317140354|gb|HQ413315.1|",  "-ATCATCAATAATATACCTTATTTTGGATTGAAGCCAATATGATAATGAG");
        group1.visitAlignedSegment("gi|33330439|gb|AF534906.1|",   "CATCATCAATAATATACCTTATTTTGGATTGAAGCCAATATGATAATGAG");
        group1.visitAlignedSegment("gi|9626158|ref|NC_001405.1|",  "CATCATCA-TAATATACCTTATTTTGGATTGAAGCCAATATGATAATGAG");
        group1.visitAlignedSegment("gi|56160492|ref|AC_000007.1|", "CATCATCA-TAATATACCTTATTTTGGATTGAAGCCAATATGATAATGAG");
        group1.visitAlignedSegment("gi|33465830|gb|AY339865.1|",   "CATCATCAATAATATACCTTATTTTGGATTGAAGCCAATATGATAATGAG");
        group1.visitAlignedSegment("gi|58177684|gb|AY601635.1|",   "CATCATCAATAATATACCTTATTTTGGATTGAAGCCAATATGATAATGAG");
        group1.visitConservationInfo(parseConservationInfoFor(" ******* *****************************************"));
        group1.visitEndGroup();
		return group1;
	}
	private AlnGroupVisitor setupGroup1ExpectationsAndHaltCallbackAtEndOfGroup() {
		AlnGroupVisitor group1 = createMock(AlnGroupVisitor.class);
        group1.visitAlignedSegment("gi|304633245|gb|HQ003817.1|",  "CATCATCAATAATATACCTTATTTTGGATTGAAGCCAATATGATAATGAG");
        group1.visitAlignedSegment("gi|317140354|gb|HQ413315.1|",  "-ATCATCAATAATATACCTTATTTTGGATTGAAGCCAATATGATAATGAG");
        group1.visitAlignedSegment("gi|33330439|gb|AF534906.1|",   "CATCATCAATAATATACCTTATTTTGGATTGAAGCCAATATGATAATGAG");
        group1.visitAlignedSegment("gi|9626158|ref|NC_001405.1|",  "CATCATCA-TAATATACCTTATTTTGGATTGAAGCCAATATGATAATGAG");
        group1.visitAlignedSegment("gi|56160492|ref|AC_000007.1|", "CATCATCA-TAATATACCTTATTTTGGATTGAAGCCAATATGATAATGAG");
        group1.visitAlignedSegment("gi|33465830|gb|AY339865.1|",   "CATCATCAATAATATACCTTATTTTGGATTGAAGCCAATATGATAATGAG");
        group1.visitAlignedSegment("gi|58177684|gb|AY601635.1|",   "CATCATCAATAATATACCTTATTTTGGATTGAAGCCAATATGATAATGAG");
        group1.visitConservationInfo(parseConservationInfoFor(" ******* *****************************************"));
        group1.visitEndGroup();
        expectLastCall().andAnswer(new IAnswer<Void>(){

			@Override
			public Void answer() throws Throwable {
				haltCallback();
				return null;
			}
        	
        });
		return group1;
	}
	
	private AlnGroupVisitor setupGroup3ExpectationsAndHaltCallbackAtEndOfGroup() {
		AlnGroupVisitor group3 = createMock(AlnGroupVisitor.class);
        
        group3.visitAlignedSegment("gi|304633245|gb|HQ003817.1|",  "AAGGTATATTATTGATGATG");
        group3.visitAlignedSegment("gi|317140354|gb|HQ413315.1|",  "AAGGTATATTATTGATGATG");
        group3.visitAlignedSegment("gi|33330439|gb|AF534906.1|",   "AAGGTATATTATTGATGATG");
        group3.visitAlignedSegment("gi|9626158|ref|NC_001405.1|",  "AAGGTATATTAT-GATGATG");
        group3.visitAlignedSegment("gi|56160492|ref|AC_000007.1|", "AAGGTATATTAT-GATGATG");
        group3.visitAlignedSegment("gi|33465830|gb|AY339865.1|",   "AAGGTATATTATTGATGATG");
        group3.visitAlignedSegment("gi|58177684|gb|AY601635.1|",   "AAGGTATATTATTGATGATG");
        group3.visitConservationInfo(parseConservationInfoFor("************ *******"));
        group3.visitEndGroup();
        expectLastCall().andAnswer(new IAnswer<Void>(){

			@Override
			public Void answer() throws Throwable {
				haltCallback();
				return null;
			}
        	
        });
		return group3;
	}
    
    private List<ConservationInfo> parseConservationInfoFor(String info){
        List<ConservationInfo> result = new ArrayList<ConservationInfo>(info.length());
        for(int i=0; i< info.length(); i++){
            switch(info.charAt(i)){
                case '*' :  result.add(ConservationInfo.IDENTICAL);
                            break;
                case ':' :  result.add(ConservationInfo.CONSERVED_SUBSITUTION);
                            break;
                case '.' :  result.add(ConservationInfo.SEMI_CONSERVED_SUBSITUTION);
                            break;
                default:    result.add(ConservationInfo.NOT_CONSERVED);
                            break;
            }
        }
        return result;
    }
}
