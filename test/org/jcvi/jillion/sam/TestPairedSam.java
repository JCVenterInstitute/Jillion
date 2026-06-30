package org.jcvi.jillion.sam;

import org.jcvi.jillion.core.testUtil.SlowTests;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(SlowTests.class)
public class TestPairedSam {

    private final ResourceHelper resourceHelper = new ResourceHelper(TestPairedSam.class);

    @Test
    public void parseWithVisitor() throws IOException {
        File compressedSam =resourceHelper.getFile("SRR36918111.sam.xz");

        SamFileParser sut = new SamFileParser(compressedSam, NullSamAttributeValidator.INSTANCE);

        parseStats(sut);

    }
    @Test
    public void parseWithFactory() throws IOException {
        File compressedSam =resourceHelper.getFile("SRR36918111.sam.xz");

        SamParser sut = SamParserFactory.create(compressedSam);

        parseStats(sut);

    }

    private static void parseStats(SamParser sut) throws IOException {
        boolean[] visitedEof =new boolean[1];
        sut.parse(new AbstractSamVisitor() {


            int numMapped=0;
            int numUnmapped=0;
            int numMated;
            int numFwd=0;
            int numRev=0;
            @Override
            public void visitRecord(SamVisitorCallback callback, SamRecord record, VirtualFileOffset start, VirtualFileOffset end) {
                 if(record.mapped()){
                     numMapped++;
                 }else{
                     numUnmapped++;
                 }
                SamRecordFlags flags = record.getFlags();
                if(flags.contains(SamRecordFlag.HAS_MATE_PAIR)){
                    numMated++;
                }
                if(flags.contains(SamRecordFlag.REVERSE_COMPLEMENTED)){
                    numRev++;
                }else if(record.mapped()){
                    numFwd++;
                }
            }

            @Override
            public void visitEnd() {
                visitedEof[0] =  true;
                assertEquals(numMapped, 1314653);
                assertEquals(numUnmapped,345123);
                assertEquals(numMated,1659776);
                assertEquals(numFwd, 657854);
                assertEquals(numRev, 656799);


            }
        });

        assertTrue(visitedEof[0]);
    }
}
