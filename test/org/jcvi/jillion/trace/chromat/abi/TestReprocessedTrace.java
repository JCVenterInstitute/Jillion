package org.jcvi.jillion.trace.chromat.abi;

import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.trace.chromat.Chromatogram;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Test for Bugfix https://github.com/JCVenterInstitute/Jillion/issues/7
 * The issue was the buggy version of the ABI parser
 * assumed the datablocks for the original vs current versions of the bases, qualities, peaks etc
 * was always in the same order : original then current.  But a non-JCVI user
 * reported getting strange quality values on some traces
 * and investigation showed the datablocks for original and current
 * aren't always in the same order so we have to look at the tag number
 * not the offset into the datablock.
 *
 * Created by katzelda on 1/17/19.
 */
public class TestReprocessedTrace {
    private ResourceHelper resources = new ResourceHelper(TestReprocessedTrace.class);

    @Test
    public void testDataBlocksOutOfOrder() throws IOException{

        File file = resources.getFile("files/5565810.ab1");

        AbiChromatogramParser abiChromatogramParser = AbiChromatogramParser.create(file);

        AbiChromatogram abiChromatogram = new AbiChromatogramBuilder("id", file).build();

        Chromatogram original = abiChromatogram.getOriginalChromatogram();


        /*
        Q00 ,Q00 ,Q07 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q11 ,Q00 ,Q09 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,
        Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q08 ,Q00 ,Q00 ,Q00 ,Q51 ,Q00 ,Q00 ,Q19 ,Q30 ,Q13 ,Q19 ,Q00 ,
        Q00 ,Q00 ,Q51 ,Q00 ,Q00 ,Q00 ,Q51 ,Q20 ,Q26 ,Q00 ,Q00 ,Q00 ,Q00 ,Q33 ,Q00 ,Q00 ,Q00 ,
        Q00 ,Q56 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q54 ,Q48 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q54 ,
        Q00 ,Q00 ,Q43 ,Q00 ,Q00 ,Q54 ,Q00 ,Q00 ,Q55 ,Q00 ,Q32 ,Q00 ,Q54 ,Q00 ,Q56 ,Q00 ,Q00 ,
        Q00 ,Q46 ,Q00 ,Q00 ,Q54 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q56 ,Q00 ,Q00 ,Q00 ,Q00 ,
        Q00 ,Q54 ,Q54 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q41 ,Q00 ,Q00 ,Q00 ,Q54 ,Q55 ,Q00 ,Q00 ,Q00 ,
        Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q35 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q56 ,Q58 ,Q58 ,Q58 ,Q58 ,
        Q54 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q54 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q54 ,
        Q00 ,Q00 ,Q00 ,Q55 ,Q54 ,Q00 ,Q00 ,Q00 ,Q00 ,Q54 ,Q00 ,Q00 ,Q58 ,Q00 ,Q56 ,Q00 ,Q00 ,
        Q00 ,Q24 ,Q00 ,Q43 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q54 ,Q00 ,Q00 ,Q00 ,Q51 ,Q00 ,Q00 ,
        Q00 ,Q00 ,Q58 ,Q54 ,Q55 ,Q00 ,Q00 ,Q51 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q54 ,Q56 ,
        Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q56 ,Q00 ,Q00 ,Q00 ,Q55 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,
        Q54 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q43 ,Q00 ,Q00 ,Q00 ,Q55 ,Q00 ,Q54 ,Q00 ,Q00 ,
        Q00 ,Q00 ,Q00 ,Q00 ,Q54 ,Q55 ,Q00 ,Q58 ,Q54 ,Q00 ,Q56 ,Q00 ,Q00 ,Q56 ,Q58 ,Q00 ,Q00 ,
        Q00 ,Q56 ,Q00 ,Q00 ,Q00 ,Q00 ,Q54 ,Q00 ,Q55 ,Q29 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q55 ,Q58 ,
        Q00 ,Q00 ,Q00 ,Q58 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q54 ,Q00 ,Q55 ,Q58 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q56 ,Q58 ,Q00 ,Q00 ,Q00 ,Q00 ,Q54 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q00 ,Q55 ,Q00 ,Q00 ,Q00 ,Q58 ,Q00 ,Q54 ,Q58 ,Q00 ,Q00 ,Q00 ,Q49 ,Q00 ,Q00 ,Q00 ,Q55
         */
        byte minQuality =  abiChromatogram.getQualitySequence().getMinQuality().get().getQualityScore();

        assertTrue(Byte.toString(minQuality), minQuality >0);

        /*
        [2, 12, 30, 49, 65, 76, 83, 95, 111, 123, 142, 152, 175, 192, 204, 220, 232,
        246, 254, 262, 273, 286, 295, 307, 323, 336, 351, 362, 375, 383, 393, 407,
        415, 428, 441, 456, 471, 482, 493, 507, 520, 536, 546, 559, 575, 585, 594,
         605, 615, 626, 637, 651, 662, 674, 687, 696, 708, 722, 732, 744, 759, 772,
         782, 794, 808, 820, 834, 847, 856, 869, 879, 890, 904, 915, 929, 941, 953,
         966, 975, 989, 1002, 1012, 1024, 1038, 1049, 1061, 1071, 1081, 1094, 1107, 1118, 1128, 1141, 1153, 1165, 1177, 1189, 1199, 1210, 1223, 1235, 1247, 1260, 1273, 1284, 1294, 1304, 1315, 1326, 1338, 1349, 1361, 1372, 1383, 1393, 1405, 1418, 1431, 1444, 1456, 1469, 1480, 1491, 1502, 1514, 1524, 1537, 1549, 1561, 1573, 1584, 1596, 1608, 1621, 1633, 1645, 1657, 1667, 1678, 1691, 1704, 1716, 1726, 1739, 1751, 1761, 1772, 1784, 1795, 1807, 1820, 1833, 1846, 1856, 1869, 1881, 1893, 1906, 1918, 1930, 1942, 1954, 1965, 1977, 1989, 2001, 2014, 2026, 2039, 2050, 2061, 2073, 2085, 2096, 2108, 2119, 2130, 2142, 2153, 2166, 2178, 2192, 2203, 2215, 2227, 2239, 2250, 2261, 2273, 2285, 2297, 2308, 2319, 2332, 2344, 2357, 2370, 2382, 2394, 2407, 2418, 2429, 2439, 2452, 2465, 2477, 2490, 2503, 2515, 2527, 2539, 2550, 2560, 2573, 2584, 2596, 2607, 2619, 2633, 2644, 2658, 2671, 2682, 2693, 2705, 2716, 2729, 2742, 2753, 2764, 2775, 2788, 2800, 2813, 2824, 2838, 2849, 2862, 2874, 2885, 2898, 2910, 2922, 2933, 2945, 2956, 2966, 2979, 2991, 3004, 3016, 3028, 3039, 3050, 3061, 3073, 3086, 3099, 3110, 3124, 3134, 3147, 3157, 3169, 3182, 3192, 3204, 3216, 3229, 3240, 3251, 3263, 3275, 3288, 3300, 3311, 3322, 3335, 3346, 3358, 3370, 3383, 3395, 3406, 3418, 3430, 3442, 3454, 3467, 3479, 3491, 3503, 3514, 3526, 3538, 3549, 3561, 3574, 3587, 3598, 3610, 3622, 3634, 3647, 3658, 3671, 3683, 3695, 3707, 3719, 3730, 3741, 3753, 3766, 3778, 3790, 3802, 3813, 3824, 3835, 3847, 3859, 3871, 3883, 3895, 3907, 3917, 3928, 3940, 3952, 3964, 3976, 3987, 3999, 4011, 4021, 4034, 4046, 4059, 4071, 4082, 4094, 4105, 4118, 4129, 4137, 4150, 4160, 4179, 4203, 4215, 4228, 4242, 4254, 4266, 4278, 4289, 4298, 4314, 4334, 4355, 4364, 4381, 4397, 4409, 4422, 4438, 4449, 4461, 4475, 4485, 4500, 4516, 4528, 4543, 4554, 4565, 4579, 4592, 4607, 4620, 4632, 4643, 4657, 4669, 4684, 4694, 4710, 4720, 4728, 4743, 4755, 4768, 4777, 4788, 4798, 4809, 4821, 4830, 4842, 4856, 4869, 4881, 4894, 4905, 4916, 4927, 4938, 4950, 4962, 4977, 4990, 5001, 5013, 5028, 5037, 5050, 5060, 5074, 5083, 5096, 5109, 5123, 5134, 5147, 5158, 5170, 5185, 5196, 5209, 5220, 5231, 5244, 5256, 5269, 5279, 5291, 5304, 5317, 5329, 5343, 5353, 5365, 5378, 5391, 5403, 5414, 5426, 5440, 5452, 5463, 5477, 5489, 5502, 5512, 5526, 5539, 5551, 5560, 5571, 5587, 5599, 5612, 5622, 5638, 5652, 5664, 5674, 5689, 5703, 5715, 5725, 5739, 5748, 5762, 5772, 5785, 5796, 5811, 5825, 5837, 5847, 5862, 5876, 5886, 5897, 5912, 5923, 5934, 5946, 5956, 5969, 5982, 5995, 6011, 6022, 6036, 6046, 6059, 6072, 6083, 6095, 6107, 6120, 6131, 6142, 6156, 6165, 6177, 6192, 6206]
         */

        //should be
        /*
        [307, 323, 336, 351, 362, 375, 383, 393, 407, 415, 428, 441, 456, 471, 482, 493, 507, 520, 536, 546, 559, 575, 585, 594, 605, 615, 626, 637, 651, 662, 674, 687, 696, 708, 722, 732, 744, 759, 772, 782, 794, 808, 820, 834, 847, 856, 869, 879, 890, 904, 915, 929, 941, 953, 966, 975, 989, 1002, 1012, 1024, 1038, 1049, 1061, 1071, 1081, 1094, 1107, 1118, 1128, 1141, 1153, 1165, 1177, 1189, 1199, 1210, 1223, 1235, 1247, 1260, 1273, 1284, 1294, 1304, 1315, 1326, 1338, 1349, 1361, 1372, 1383, 1393, 1405, 1418, 1431, 1444, 1456, 1469, 1480, 1491, 1502, 1514, 1524, 1537, 1549, 1561, 1573, 1584, 1596, 1608, 1621, 1633, 1645, 1657, 1667, 1678, 1691, 1704, 1716, 1726, 1739, 1751, 1761, 1772, 1784, 1795, 1807, 1820, 1833, 1846, 1856, 1869, 1881, 1893, 1906, 1918, 1930, 1942, 1954, 1965, 1977, 1989, 2001, 2014, 2026, 2039, 2050, 2061, 2073, 2085, 2096, 2108, 2119, 2130, 2142, 2153, 2166, 2178, 2192, 2203, 2215, 2227, 2239, 2250, 2261, 2273, 2285, 2297, 2308, 2319, 2332, 2344, 2357, 2370, 2382, 2394, 2407, 2418, 2429, 2439, 2452, 2465, 2477, 2490, 2503, 2515, 2527, 2539, 2550, 2560, 2573, 2584, 2596, 2607, 2619, 2633, 2644, 2658, 2671, 2682, 2693, 2705, 2716, 2729, 2742, 2753, 2764, 2775, 2788, 2800, 2813, 2824, 2838, 2849, 2862, 2874, 2885, 2898, 2910, 2922, 2933, 2945, 2956, 2966, 2979, 2991, 3004, 3016, 3028, 3039, 3050, 3061, 3073, 3086, 3099, 3110, 3124, 3134, 3147, 3157, 3169, 3182, 3192, 3204, 3216, 3229, 3240, 3251, 3263, 3275, 3288, 3300, 3311, 3322, 3335, 3346, 3358, 3370, 3383, 3395, 3406, 3418, 3430, 3442, 3454, 3467, 3479, 3491, 3503, 3514, 3526, 3538, 3549, 3561, 3574, 3587, 3598, 3610, 3622, 3634, 3647, 3658, 3671, 3683, 3695, 3707, 3719, 3730, 3741, 3753, 3766, 3778, 3790, 3802, 3813, 3824, 3835, 3847, 3859, 3871, 3883, 3895, 3907, 3917, 3928, 3940, 3952, 3964, 3976, 3987, 3999, 4011, 4021, 4034, 4046, 4059, 4071, 4082, 4094, 4105, 4118, 4129, 4137, 4150, 4160, 4179]
         */
        assertEquals(307, abiChromatogram.getPeakSequence().get(0).getValue());

    }
}