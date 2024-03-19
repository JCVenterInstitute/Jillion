package org.jcvi.jillion.trace.fastq;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.jcvi.jillion.core.qual.QualitySequence;
import org.jcvi.jillion.core.qual.QualitySequenceBuilder;
import org.jcvi.jillion.core.util.MapUtil;
import org.jcvi.jillion.testutils.NucleotideSequenceTestUtil;

public class TestFastqDatasetUtil {
	private static Random random = new Random();
	
	public static Map<String, FastqRecord> createRandomFastqDataset(int numberOfRecords, int seqLength) {
		Map<String, FastqRecord> map = new ConcurrentHashMap<>(MapUtil.computeMinHashMapSizeWithoutRehashing(numberOfRecords));
        for(int i=0; i<numberOfRecords; i++){
            String id = "seq_" +i;
            map.put(id, FastqRecordBuilder.create(id,
                    NucleotideSequenceTestUtil.createRandom(seqLength),

                    createRandomQualitySequence(seqLength)).build());
        }
		return map;
	}

    public static QualitySequence createRandomQualitySequence(int length){
        byte[] quals = new byte[length];
        for(int i=0; i< length; i++){
            quals[i] = (byte)(random.nextInt(50) + 10);
        }
        return  new QualitySequenceBuilder(quals).build();
    }
}
