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
/*
 * Created on Jan 5, 2010
 *
 * @author dkatzel
 */
package org.jcvi.jillion.fasta.pos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.datastore.DataStoreException;
import org.jcvi.jillion.core.pos.Position;
import org.jcvi.jillion.core.pos.PositionSequence;
import org.jcvi.jillion.core.pos.PositionSequenceBuilder;
import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public abstract class AbstractTestPositionFastaFileDataStore {
	private static final String QUAL_FILE_PATH = "1119369023656.peak";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	PositionFastaRecord expected = new PositionFastaRecord(
			"1119369023656", new PositionSequenceBuilder(
							new short[] { 2, 9, 29, 42, 55, 71, 92, 102, 125,
									140, 163, 184, 194, 207, 220, 235, 248,
									260, 271, 285, 298, 307, 316, 328, 343,
									355, 364, 379, 391, 403, 417, 429, 442,
									456, 468, 483, 493, 504, 517, 529, 540,
									550, 561, 574, 585, 593, 605, 619, 632,
									643, 654, 663, 675, 686, 700, 716, 728,
									740, 753, 764, 775, 786, 797, 810, 821,
									833, 845, 857, 870, 878, 889, 902, 915,
									927, 940, 953, 966, 979, 990, 1003, 1013,
									1025, 1035, 1047, 1059, 1072, 1086, 1097,
									1109, 1123, 1136, 1148, 1159, 1171, 1180,
									1192, 1204, 1217, 1229, 1244, 1255, 1267,
									1279, 1293, 1303, 1314, 1325, 1337, 1348,
									1361, 1373, 1385, 1397, 1410, 1422, 1432,
									1444, 1455, 1467, 1480, 1492, 1504, 1516,
									1528, 1540, 1553, 1564, 1575, 1587, 1598,
									1611, 1623, 1633, 1644, 1656, 1668, 1680,
									1692, 1704, 1717, 1729, 1741, 1753, 1765,
									1774, 1785, 1797, 1810, 1822, 1835, 1847,
									1859, 1871, 1883, 1896, 1907, 1920, 1931,
									1944, 1956, 1968, 1980, 1993, 2005, 2016,
									2028, 2042, 2053, 2064, 2076, 2088, 2101,
									2112, 2124, 2136, 2148, 2160, 2171, 2184,
									2196, 2208, 2218, 2230, 2243, 2255, 2265,
									2278, 2289, 2302, 2315, 2326, 2339, 2351,
									2363, 2375, 2386, 2398, 2411, 2423, 2436,
									2448, 2460, 2471, 2483, 2495, 2507, 2519,
									2531, 2544, 2556, 2568, 2580, 2592, 2604,
									2614, 2626, 2637, 2649, 2662, 2674, 2686,
									2697, 2709, 2721, 2733, 2744, 2756, 2768,
									2780, 2792, 2805, 2817, 2829, 2841, 2853,
									2865, 2876, 2888, 2900, 2912, 2924, 2936,
									2948, 2960, 2972, 2984, 2996, 3008, 3021,
									3033, 3045, 3056, 3069, 3081, 3092, 3104,
									3116, 3128, 3139, 3150, 3162, 3174, 3187,
									3198, 3210, 3222, 3234, 3246, 3258, 3270,
									3282, 3295, 3306, 3320, 3332, 3344, 3356,
									3369, 3380, 3392, 3405, 3416, 3428, 3439,
									3451, 3463, 3476, 3487, 3500, 3512, 3524,
									3536, 3548, 3559, 3570, 3582, 3594, 3606,
									3619, 3630, 3641, 3654, 3666, 3677, 3689,
									3701, 3713, 3725, 3735, 3748, 3759, 3772,
									3784, 3796, 3807, 3820, 3831, 3844, 3855,
									3868, 3880, 3892, 3904, 3916, 3928, 3940,
									3952, 3963, 3975, 3987, 3998, 4010, 4022,
									4034, 4045, 4056, 4069, 4081, 4093, 4106,
									4117, 4129, 4142, 4155, 4166, 4178, 4190,
									4202, 4214, 4226, 4237, 4250, 4261, 4273,
									4285, 4297, 4308, 4320, 4331, 4343, 4354,
									4366, 4377, 4391, 4402, 4415, 4428, 4441,
									4452, 4464, 4476, 4488, 4500, 4512, 4525,
									4537, 4549, 4561, 4573, 4585, 4597, 4610,
									4620, 4631, 4644, 4656, 4668, 4679, 4690,
									4703, 4715, 4727, 4739, 4750, 4762, 4773,
									4786, 4799, 4811, 4822, 4834, 4847, 4860,
									4872, 4885, 4897, 4909, 4920, 4933, 4945,
									4957, 4969, 4981, 4992, 5004, 5015, 5028,
									5039, 5051, 5062, 5074, 5086, 5098, 5110,
									5122, 5134, 5146, 5158, 5171, 5184, 5196,
									5208, 5220, 5232, 5243, 5254, 5266, 5278,
									5289, 5302, 5315, 5327, 5339, 5352, 5363,
									5376, 5388, 5400, 5411, 5422, 5435, 5446,
									5458, 5471, 5483, 5496, 5508, 5520, 5532,
									5544, 5555, 5567, 5579, 5589, 5601, 5612,
									5624, 5636, 5648, 5660, 5673, 5685, 5697,
									5710, 5723, 5733, 5744, 5757, 5768, 5780,
									5793, 5804, 5818, 5830, 5842, 5853, 5864,
									5877, 5889, 5902, 5913, 5925, 5936, 5950,
									5961, 5973, 5986, 5998, 6011, 6023, 6035,
									6047, 6059, 6071, 6083, 6095, 6107, 6119,
									6131, 6143, 6154, 6165, 6176, 6189, 6200,
									6212, 6224, 6236, 6249, 6260, 6271, 6283,
									6295, 6307, 6320, 6333, 6345, 6357, 6369,
									6381, 6394, 6405, 6416, 6428, 6440, 6451,
									6464, 6476, 6488, 6500, 6512, 6523, 6536,
									6548, 6561, 6574, 6586, 6598, 6610, 6621,
									6632, 6644, 6656, 6668, 6681, 6693, 6705,
									6717, 6729, 6741, 6752, 6764, 6775, 6787,
									6800, 6811, 6823, 6836, 6848, 6861, 6872,
									6884, 6897, 6907, 6919, 6931, 6944, 6956,
									6968, 6981, 6993, 7005, 7017, 7029, 7041,
									7053, 7066, 7077, 7089, 7102, 7113, 7125,
									7136, 7148, 7161, 7174, 7185, 7197, 7208,
									7220, 7232, 7246, 7258, 7269, 7281, 7292,
									7304, 7315, 7327, 7339, 7352, 7364, 7376,
									7388, 7399, 7411, 7423, 7435, 7447, 7459,
									7472, 7484, 7494, 7505, 7518, 7530, 7542,
									7554, 7566, 7578, 7591, 7602, 7614, 7625,
									7637, 7648, 7661, 7674, 7686, 7697, 7710,
									7721, 7733, 7745, 7756, 7768, 7780, 7792,
									7805, 7817, 7829, 7840, 7853, 7865, 7877,
									7889, 7900, 7913, 7925, 7936, 7948, 7960,
									7972, 7983, 7996, 8009, 8021, 8032, 8043,
									8055, 8067, 8079, 8091, 8102, 8114, 8125,
									8137, 8148, 8160, 8173, 8185, 8196, 8208,
									8221, 8233, 8243, 8254, 8267, 8279, 8291,
									8303, 8315, 8327, 8339, 8352, 8364, 8375,
									8387, 8398, 8411, 8422, 8432, 8444, 8457,
									8469, 8481, 8493, 8504, 8515, 8529, 8540,
									8552, 8563, 8574, 8587, 8600, 8613, 8625,
									8635, 8647, 8660, 8672, 8683, 8695, 8707,
									8718, 8729, 8741, 8754, 8766, 8778, 8791,
									8802, 8813, 8825, 8835, 8846, 8858, 8870,
									8883, 8895, 8907, 8919, 8931, 8943, 8953,
									8964, 8977, 8990, 9001, 9012, 9027, 9038,
									9049, 9060, 9072, 9084, 9095, 9108, 9118,
									9130, 9141, 9154, 9166, 9178, 9190, 9201,
									9213, 9225, 9236, 9247, 9258, 9269, 9281,
									9293, 9305, 9315, 9326, 9337, 9348, 9361,
									9371, 9379, 9391, 9401, 9417, 9427, 9438,
									9448, 9463, 9475, 9490, 9502, 9519, 9532,
									9551, 9563, 9580, 9598, 9612, }).build());
	ResourceHelper RESOURCES = new ResourceHelper(
			AbstractTestPositionFastaFileDataStore.class);

	PositionFastaDataStore sut;
	@Before
	public void setup() throws IOException, Exception{
		sut = createPositionFastaMap(RESOURCES
				.getFile(QUAL_FILE_PATH));
	}
	@Test
	public void parseFile() throws Exception {
		assertEquals(1, sut.getNumberOfRecords());
		PositionFastaRecord actual = sut.get("1119369023656");
		assertEquals(expected, actual);

		assertEquals(expected.getSequence().getLength(), actual.getLength());
		
	}

	protected abstract PositionFastaDataStore createPositionFastaMap(
			File fastaFile) throws Exception;
	
	
	@Test
    public void getSubSequenceById() throws IOException, DataStoreException{
    	assertEquals(getSubSequence( expected.getSequence(), 100), sut.getSubSequence("1119369023656", 100));
    	assertEquals(getSubSequence( expected.getSequence(), 50), sut.getSubSequence(expected.getId(), 50));
    	assertEquals(getSubSequence( expected.getSequence(), 87), sut.getSubSequence(expected.getId(), 87));
    }
    
    @Test
    public void getSubSequenceByIdThatDoesNotExistShouldReturnNull() throws IOException, DataStoreException{
    	assertNull(sut.getSequence("does not exist"));
    }
    @Test
    public void getSubSequenceRangeById() throws IOException, DataStoreException{
    	Range range = Range.of(35, 349);
    	assertEquals(getSubSequence( expected.getSequence(), range), sut.getSubSequence(expected.getId(), range));
     }
    @Test
    public void getSubSequenceByOffsetThatDoesNotExistShouldReturnNull() throws IOException, DataStoreException{
    	assertNull(sut.getSubSequence("does not exist", 100));
    }
    
    @Test
    public void getSubSequenceByRangeThatDoesNotExistShouldReturnNull() throws IOException, DataStoreException{
    	assertNull(sut.getSubSequence("does not exist", Range.ofLength(100)));
    }
    
    @Test
    public void getSubSequenceNullRangeShouldThrowNPE() throws IOException, DataStoreException{
    	
    	expectedException.expect(NullPointerException.class);    	
    	sut.getSubSequence(expected.getId(), null);
    }
    
    @Test
    public void getSubSequenceNegativeOffsetShouldThrowIllegalArgumentException() throws IOException, DataStoreException{
    	
    	expectedException.expect(IllegalArgumentException.class);
    	expectedException.expectMessage("negative");
    	sut.getSubSequence(expected.getId(), -1);
    	
    }
    
    @Test
    public void getSubSequenceBeyondLengthOffsetShouldThrowIllegalArgumentException() throws IOException, DataStoreException{
    	
    	expectedException.expect(IllegalArgumentException.class);
    	expectedException.expectMessage("beyond sequence length");
    	sut.getSubSequence(expected.getId(), 1_000_000);
    	
    }
    
    
    private PositionSequence getSubSequence(PositionSequence fullSeq, int startOffset){
    	Range range = Range.of(startOffset, fullSeq.getLength() -1);
    	return getSubSequence(fullSeq, range);
    	
    }

	private PositionSequence getSubSequence(PositionSequence fullSeq, Range range) {
		//to really test we aren't going to use the helper trim methods on the builder
		//but just the iterator
		PositionSequenceBuilder builder = new PositionSequenceBuilder();
		Iterator<Position> iter = fullSeq.iterator(range);
    	while(iter.hasNext()){
    		builder.append(iter.next());
    	}
    	return builder.build();
	}
}
