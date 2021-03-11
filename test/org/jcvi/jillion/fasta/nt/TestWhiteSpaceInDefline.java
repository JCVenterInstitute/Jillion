package org.jcvi.jillion.fasta.nt;

import org.jcvi.jillion.core.io.InputStreamSupplier;
import org.jcvi.jillion.core.residue.nt.NucleotideSequenceBuilder;
import org.jcvi.jillion.fasta.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.junit.Assert.*;

public class TestWhiteSpaceInDefline {

    @Test
    public void test() throws IOException {
        String data = ">hCoV-19/Hong Kong/VM20002849/2020|EPI_ISL_414571|2020-02-22\n" +
                "ACGTACGT\n" +
                ">hCoV-19/Hong Kong/VM20002907/2020|EPI_ISL_414517|2020-02-25\n" +
                "GGGGGGGGGGGGGGG\n";

        Map<String, NucleotideFastaRecord> records = new LinkedHashMap<>();

        FastaParser parser = FastaFileParser.create(new ByteArrayInputStream(data.getBytes()));
        parser.parse(new FastaVisitor() {


            @Override
            public FastaRecordVisitor visitDefline(FastaVisitorCallback callback, String id, String optionalComment) {
                return new AbstractNucleotideFastaRecordVisitor(optionalComment ==null? id: id +" "+ optionalComment, null, true) {
                    @Override
                    protected void visitRecord(NucleotideFastaRecord fastaRecord) {
                        records.put(fastaRecord.getId(), fastaRecord);
                    }
                };
            }

            @Override
            public void visitEnd() {

            }

            @Override
            public void halted() {

            }


        });

        assertEquals(2, records.size());
    }


}
