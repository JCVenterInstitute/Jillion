package org.jcvi.jillion.fasta.aa;

import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.ProteinSequence;
import org.jcvi.jillion.core.residue.aa.ProteinSequenceBuilder;
import org.jcvi.jillion.testutils.ProteinSequenceTestUtil;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.*;

public class TestMultithreadProteinFastaWriter {

    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();

    private static Map<String, ProteinFastaRecord> map;


    @BeforeClass
    public static void createDataSet(){
        map = new HashMap<>();
        for(int i=0; i<5_000; i++){
            String id = "seq_" +i;
            map.put(id, new ProteinFastaRecordBuilder(id, ProteinSequenceTestUtil.randomSequence(200)).build());
        }
    }

    @Test
    public void writeSerially() throws IOException {

        File fastaFile = tmpDir.newFile();

        try(ProteinFastaWriter writer = new ProteinFastaWriterBuilder(fastaFile)
                                                .build()){
           for(ProteinFastaRecord f: map.values()){
               writer.write(f);
           }
        }

        assertFileWrittenCorrectly(fastaFile);
    }
    @Test
    public void writeCollection() throws IOException {

        File fastaFile = tmpDir.newFile();

        try(ProteinFastaWriter writer = new ProteinFastaWriterBuilder(fastaFile)
                .build()){
            writer.write(map.values());
        }

        assertFileWrittenCorrectly(fastaFile);
    }

    @Test
    public void multiThreaded() throws IOException {

        File fastaFile = tmpDir.newFile();

        try(ProteinFastaWriter writer = new ProteinFastaWriterBuilder(fastaFile)
                .multiThreaded(true)
                .build()){
            writer.write(map.values());
        }

        assertFileWrittenCorrectly(fastaFile);
    }

    private void assertFileWrittenCorrectly(File fastaFile) throws IOException{
        ProteinFastaDataStore datastore = ProteinFastaDataStore.fromFile(fastaFile);

        assertEquals(map.size(), datastore.getNumberOfRecords());
        for(ProteinFastaRecord f : map.values()){
            ProteinFastaRecord actual = datastore.get(f.getId());
            assertEquals(f, actual);
        }
    }



}
