package org.jcvi.jillion.sam;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.util.SingleThreadAdder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;
public class TestBamDataStore {

   
    private static SamParser SAM_PARSER;
    private static File SAM_FILE;
    
    private SamFileDataStore sut;
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    @BeforeClass
    public static void setupParser() throws IOException{
        ResourceHelper resources = new ResourceHelper(TestBamDataStore.class);
        SAM_FILE = resources.getFile("example.bam");
        SAM_PARSER = SamParserFactory.create(SAM_FILE);
    }
    
    @Before
    public void setupSut() throws IOException{
        sut = new SamFileDataStoreBuilder(SAM_FILE)
                    .build();
    }
    
    @Test
    public void getHeader() throws IOException, DataStoreException{
        assertEquals(SAM_PARSER.getHeader(), sut.getHeader());
    }
    
    @Test
    public void getRecordCount() throws IOException, DataStoreException{
        SingleThreadAdder count = new SingleThreadAdder();
        SAM_PARSER.parse(new AbstractSamVisitor() {

            @Override
            public void visitRecord(SamVisitorCallback callback,
                    SamRecord record, VirtualFileOffset start,
                    VirtualFileOffset end) {
                count.increment();
            }
            
        });
        
        long expected = count.longValue();
        assertTrue(expected > 0);
        assertEquals(expected, sut.getNumberOfRecords());
    }
    
    @Test
    public void getRecordThatDoesntExistShouldReturnNull() throws DataStoreException{
        assertNull(sut.get("fake"));
        assertFalse(sut.contains("fake"));
    }
    
    @Test(expected = NullPointerException.class)
    public void getNullIdShouldThrowNPE() throws DataStoreException{
        sut.get(null);
    }
    
    @Test
    public void getIdAfterClosedShouldThrowDataStoreException() throws IOException, DataStoreException{
        sut.close();
        expectedException.expectMessage("closed");
        
        sut.get("fake");
    }
    
    @Test
    public void randomAccessId() throws IOException, DataStoreException{
        String queryName = "r001";
     
     List<SamRecord> expectedRecord = new ArrayList<SamRecord>();
     
     SAM_PARSER.parse(new AbstractSamVisitor() {
       
        @Override
        public void visitRecord(SamVisitorCallback callback, SamRecord record,
                VirtualFileOffset start, VirtualFileOffset end) {
            
            if(queryName.equals(record.getQueryName())){
                expectedRecord.add(record);
            }
  
        }
         
    });
     
     assertEquals(expectedRecord, sut.getAllRecordsFor(queryName));
    }
    
    @Test
    public void filteredDataStore() throws IOException, DataStoreException{
        SamFileDataStore filteredSut = new SamFileDataStoreBuilder(SAM_FILE)
                                                .filter(record -> record.getQueryName().equals("r003"))
                                                .build();
        
        assertFalse(filteredSut.contains("r001"));
        assertNull(filteredSut.get("r001"));
        assertTrue(filteredSut.getAllRecordsFor("r001").isEmpty());
    }
    
    @Test(expected = NullPointerException.class)
    public void getRecordsByReferenceNulldReferenceShouldThrowNPE() throws DataStoreException{
        sut.getAlignedRecords(null);
    }
    @Test
    public void getRecordsByReferenceInvalidReferenceShouldThrowException() throws DataStoreException{
        
        expectedException.expect(DataStoreException.class);
        expectedException.expectMessage("no reference with name");
        
        sut.getAlignedRecords("fake");
    }
    
    @Test
    public void getAlignmentsByReference() throws IOException, DataStoreException{
        //only 1 ref in file called 'ref'
        List<SamRecord> expected = new ArrayList<>();
        SAM_PARSER.parse(new AbstractSamVisitor() {

            @Override
            public void visitRecord(SamVisitorCallback callback,
                    SamRecord record, VirtualFileOffset start,
                    VirtualFileOffset end) {
                expected.add(record);
            }
            
        });
        
        assertEquals(expected, sut.getAlignedRecords("ref")
                                    .toStream()
                                    .collect(Collectors.toList()));
    }
    
    @Test
    public void getFilteredAlignmentsByReference() throws IOException, DataStoreException{
        //only 1 ref in file called 'ref'
        SamFileDataStore filteredSut = new SamFileDataStoreBuilder(SAM_FILE)
                                                .filter(record -> record.getQueryName().equals("r003"))
                                                .build();
        List<SamRecord> expected = new ArrayList<>();
        SAM_PARSER.parse(new AbstractSamVisitor() {

            @Override
            public void visitRecord(SamVisitorCallback callback,
                    SamRecord record, VirtualFileOffset start,
                    VirtualFileOffset end) {
                if("r003".equals(record.getQueryName())){
                    expected.add(record);
                }
            }
            
        });
        
        assertEquals(expected, filteredSut.getAlignedRecords("ref")
                                    .toStream()
                                    .collect(Collectors.toList()));
    }
    
    @Test
    public void getAlignmentsByReferenceAndRange() throws IOException, DataStoreException{
        //only 1 ref in file called 'ref'
        Range range = Range.of(27, 200);
        
        List<SamRecord> expected = new ArrayList<>();
        SAM_PARSER.parse(new AbstractSamVisitor() {

            @Override
            public void visitRecord(SamVisitorCallback callback,
                    SamRecord record, VirtualFileOffset start,
                    VirtualFileOffset end) {
                if(record.getAlignmentRange().intersects(range)){
                    expected.add(record);
                }
            }
            
        });
        //1 of the 2 r001 reads should be filtered out
        assertEquals(1, expected.stream().map(r -> r.getQueryName()).filter(s -> s.equals("r001")).count());
        
        assertEquals(expected, sut.getAlignedRecords("ref", range)
                                    .toStream()
                                    .collect(Collectors.toList()));
    }

    @Test
    public void getFilteredAlignmentsByReferenceAndRange() throws IOException, DataStoreException{
        SamFileDataStore filteredSut = new SamFileDataStoreBuilder(SAM_FILE)
                                    .filter(record -> !record.getQueryName().equals("r001"))
                                    .build();

        Range range = Range.of(27, 200);
        
        List<SamRecord> expected = new ArrayList<>();
        SAM_PARSER.parse(new AbstractSamVisitor() {

            @Override
            public void visitRecord(SamVisitorCallback callback,
                    SamRecord record, VirtualFileOffset start,
                    VirtualFileOffset end) {
                if(!record.getQueryName().equals("r001") && record.getAlignmentRange().intersects(range)){
                    expected.add(record);
                }
            }
            
        });
        //1 of the 2 r001 reads should be filtered out
        assertEquals(0, expected.stream().map(r -> r.getQueryName()).filter(s -> s.equals("r001")).count());
        
        assertEquals(expected, filteredSut.getAlignedRecords("ref", range)
                                    .toStream()
                                    .collect(Collectors.toList()));
    }

}
