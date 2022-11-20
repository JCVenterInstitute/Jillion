package org.jcvi.jillion.vcf.dsl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.vcf.AbtractVcfVisitor;
import org.jcvi.jillion.vcf.VcfFileParser;
import org.jcvi.jillion.vcf.VcfFileParserTest;
import org.jcvi.jillion.vcf.VcfFilter;
import org.jcvi.jillion.vcf.VcfFormat;
import org.jcvi.jillion.vcf.VcfHeader;
import org.jcvi.jillion.vcf.VcfInfo;
import org.jcvi.jillion.vcf.VcfNumber;
import org.jcvi.jillion.vcf.VcfValueType;
import org.jcvi.jillion.vcf.VcfVisitor;
import org.jcvi.jillion.vcf.dsl.VcfDSL;
import org.jcvi.jillion.vcf.dsl.VcfDSL.FlagVcfInfoDSL;
import org.jcvi.jillion.vcf.dsl.VcfDSL.FloatVcfInfoDSL;
import org.jcvi.jillion.vcf.dsl.VcfDSL.IntVcfFormatDSL;
import org.jcvi.jillion.vcf.dsl.VcfDSL.IntVcfInfoDSL;
import org.jcvi.jillion.vcf.dsl.VcfDSL.StringVcfFormatDSL;
import org.jcvi.jillion.vcf.dsl.VcfDSL.StringVcfInfoDSL;
import org.jcvi.jillion.vcf.dsl.VcfDSL.VcfFile;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

public class VcfDSLExampleFileTest {

	private static final ResourceHelper RESOURCES = new ResourceHelper(VcfDSLExampleFileTest.class);

	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();
	//VcfInfo(id=AF, description=Allele Frequency, number=InfoNumberTypeAndValue [type=DOT, value=null], type=Float, parameters={Type=Float, Description=Allele Frequency, Number=., ID=AF
	/**
	 * This is the AF spec from the example file from VCF 4.0 more recent specs updated the number from DOT -> A
	 * which would fail our test if we used the compiled RESERVED one so make a custom one for this test only.
	 */
	private static VcfInfo OLD_AF =  VcfInfo.builder()
			.id("AF").description("Allele frequency")
			.number(VcfNumber.DOT)
			.type(VcfValueType.Float)
			.build();
	@Test
	public void matchesExample() throws IOException {
		
		VcfDSL dsl = new VcfDSL();
		
		dsl.property("fileformat","VCFv4.0")
			.property("fileDate","20090805")
			.property("source","myImputationProgramV3.1")
			.property("reference","1000GenomesPilot-NCBI36")
			.property("phasing","partial");
		/*
		 * ##INFO=<ID=DP,Number=1,Type=Integer,Description="Total Depth">
##INFO=<ID=AF,Number=.,Type=Float,Description="Allele Frequency">
##INFO=<ID=AA,Number=1,Type=String,Description="Ancestral Allele">
##INFO=<ID=DB,Number=0,Type=Flag,Description="dbSNP membership, build 129">
##INFO=<ID=H2,Number=0,Type=Flag,Description="HapMap2 membership">

		 */
		IntVcfInfoDSL ns = dsl.intInfo(VcfInfo.RESERVED_NS);
		IntVcfInfoDSL dpInfo = dsl.intInfo(VcfInfo.RESERVED_DP);
		FloatVcfInfoDSL af = dsl.floatInfo(OLD_AF);
		StringVcfInfoDSL aa = dsl.stringInfo(VcfInfo.RESERVED_AA);
		FlagVcfInfoDSL db = dsl.flagInfo(VcfInfo.RESERVED_DB);
		FlagVcfInfoDSL h2 = dsl.flagInfo(VcfInfo.RESERVED_H2);
		
		VcfFilter q10Filter= dsl.filter("q10", "Quality below 10");
		VcfFilter s50Filter= dsl.filter("s50", "Less than 50% of samples have data");
		/*
		 * ##FORMAT=<ID=GT,Number=1,Type=String,Description="Genotype">
##FORMAT=<ID=GQ,Number=1,Type=Integer,Description="Genotype Quality">
##FORMAT=<ID=DP,Number=1,Type=Integer,Description="Read Depth">
##FORMAT=<ID=HQ,Number=2,Type=Integer,Description="Haplotype Quality">

		 */
		StringVcfFormatDSL gt = dsl.stringFormat(VcfFormat.RESERVED_GT);
		IntVcfFormatDSL gq = dsl.intFormat(VcfFormat.RESERVED_GQ);
		IntVcfFormatDSL dp = dsl.intFormat(VcfFormat.RESERVED_DP);
		IntVcfFormatDSL hq = dsl.intFormat(VcfFormat.RESERVED_HQ);
		//NA00001	NA00002	NA00003
		VcfFile vcfFile = dsl
//				.addGenotype("NA00001")
//				.addGenotype("NA00002")
//				.addGenotype("NA00003")
				.beginLines()
		//20	14370	rs6054257	G	A	29	PASS
		//NS=3;DP=14;AF=0.5;DB;H2	GT:GQ:DP:HQ	0|0:48:1:51,51	1|0:48:8:51,51	1/1:43:5:.,.
			.line("20", 14370, "rs6054257", "G", "A", 29)
				.passed()
				.info(ns, 3)
				.info(dpInfo, 14)
				.info(af, 0.5)
				.info(db)
				.info(h2)
				.genotypes(gt,gq,dp,hq)
					.add("NA00001")
						.set(gt,"0|0")
						.set(gq, 48)
						.set(dp, 1)
						.set(hq, 51,51)
						.done()
					.add("NA00002")
						.set(gt,"1|0")
						.set(gq, 48)
						.set(dp, 8)
						.set(hq, 51,51)
						.done()
					.add("NA00003")
						.set(gt,"1/1")
						.set(gq, 43)
						.set(dp, 5)
						.set(hq, null,null) // unset
						.done()
					.doneAllGenotypes() // use instead of .done().done() , this test does both ways just to make sure both paths work
				//20	17330	.	T	A	3	q10	NS=3;DP=11;AF=0.017	GT:GQ:DP:HQ	0|0:49:3:58,50	0|1:3:5:65,3	0/0:41:3
				.line("20", 17330, ".", "T", "A", 3)
				.filter(q10Filter)
				.info(ns,3)
				.info(dpInfo,11)
				.info(af, 0.017)
				.genotypes(gt,gq,dp,hq)
					.add("NA00001")
						.set(gt, "0|0")
						.set(gq, 49)
						.set(dp, 3)
						.set(hq, 58,50)
						.done()
					.add("NA00002")
						.set(gt, "0|1")
						.set(gq, 3)
						.set(dp, 5)
						.set(hq, 65,3)
						.done()
					.add("NA00003")
						.set(gt, "0/0")
						.set(gq, 41)
						.set(dp, 3) //note hp not set and ignored as trailing unset
						.done()
					.done()
				.done()
			//20	1110696	rs6040355	A	G,T	67	PASS	NS=2;DP=10;AF=0.333,0.667;AA=T;DB	GT:GQ:DP:HQ	1|2:21:6:23,27	2|1:2:0:18,2	2/2:35:4
			.line("20", 1110696, "rs6040355",	"A", "G,T",	67)
				.passed()
				.info(ns,2)
				.info(dpInfo,10)
				.info(af, 0.333,0.667)
				.info(aa, "T")
				.info(db)
				.genotypes(gt,gq,dp,hq)
					.add("NA00001")
						.set(gt, "1|2")
						.set(gq, 21)
						.set(dp, 6)
						.set(hq, 23,27)
						.done()
					.add("NA00002")
						.set(gt, "2|1")
						.set(gq, 2)
						.set(dp, 0)
						.set(hq, 18,2)
						.done()
					.add("NA00003")
						.set(gt, "2/2")
						.set(gq, 35)
						.set(dp, 4)
						.done()
					.done()
				.done()
			//20	1230237	.	T	.	47	PASS	NS=3;DP=13;AA=T	GT:GQ:DP:HQ	0|0:54:7:56,60	0|0:48:4:51,51	0/0:61:2
			.line("20", 1230237, ".", "T", ".", 47)
				.passed()
				.info(ns, 3)
				.info(dpInfo, 13)
				.info(aa, "T")
				.genotypes(gt,gq,dp,hq)
					.add("NA00001")
					.set(gt, "0|0")
					.set(gq, 54)
					.set(dp, 7)
					.set(hq, 56,60)
					.done()
					.add("NA00002")
					.set(gt, "0|0")
					.set(gq, 48)
					.set(dp, 4)
					.set(hq, 51,51)
					.done()
					.add("NA00003")
					.set(gt, "0/0")
					.set(gq, 61)
					.set(dp, 2)
					.done()
				.done()
				.done()
			//20	1234567	microsat1	GTCT	G,GTACT	50	PASS	NS=3;DP=9;AA=G	GT:GQ:DP	0/1:35:4	0/2:17:2	1/1:40:3
			.line("20", 1234567, "microsat1", "GTCT", "G,GTACT", 50)
				.passed()
				.info(ns,3)
				.info(dpInfo,9)
				.info(aa,"G")
				.genotypes(gt,gq,dp)
					.add("NA00001")
						.set(gt, "0/1")
						.set(gq, 35)
						.set(dp, 4)
					.done()
					.add("NA00002")
						.set(gt, "0/2")
						.set(gq, 17)
						.set(dp, 2)
					.done()
					.add("NA00003")
						.set(gt, "1/1")
						.set(gq, 40)
						.set(dp, 3)
					.done()
				.done()
			.done()
			.done();
		
		File outFile = tmpDir.newFile("out.vcf");
		vcfFile.writeVcf(outFile);
		
		File f = RESOURCES.getFile("../files/example.vcf");
		VcfMatcher expectedMatcher = new VcfMatcher();
		VcfFileParser.createParserFor(f).parse(expectedMatcher);
		
		VcfMatcher actualMatcher = new VcfMatcher();
		VcfFileParser.createParserFor(outFile).parse(actualMatcher);
		
		expectedMatcher.assertMatches(actualMatcher);
	}
	
	private static class VcfMatcher extends AbtractVcfVisitor{

		private VcfHeader header;
		private List<List<String>> dataLines = new ArrayList<>();
		@Override
		public void visitData(VcfVisitorCallback callback, String chromId, int position, String id, String refBase,
				String altBase, int quality, String filter, String info, String format, List<String> extraFields) {
			List<String> fields = new ArrayList<String>();
			
			dataLines.add(fields);
			
			fields.add(chromId);
			fields.add(Integer.toString(position));
			fields.add(id);
			fields.add(refBase);
			fields.add(altBase);
			fields.add(Integer.toString(quality));
			fields.add(filter);
			fields.add(info);
			fields.add(format);
			fields.addAll(extraFields);
			
		}

		@Override
		protected void visitHeader(VcfVisitorCallback callback, VcfHeader header) {
			this.header = header;
			
		}

		public void assertMatches(VcfMatcher other) {
			assertEquals("properties", header.getProperties(), other.header.getProperties());
			assertEquals("contig info", header.getContigInfos(), other.header.getContigInfos());
			assertEquals("extra columns", header.getExtraColumns(), other.header.getExtraColumns());
			assertEquals("filters", header.getFilters(), other.header.getFilters());
			for(int i=0; i< header.getInfos().size(); i++) {
				assertEquals(header.getInfos().get(i), other.header.getInfos().get(i));
			}
			
//			assertEquals("info", header.getInfos(), other.header.getInfos());
			assertEquals("formats", header.getFormats(), other.header.getFormats());
//			assertEquals("header", header, other.header);
			for(int i=0; i< dataLines.size(); i++) {
				assertEquals("data line " + i, dataLines.get(i), other.dataLines.get(i));
			}
			//do this check after so partial test what we have so far
			assertEquals("number of data lines", dataLines.size(), other.dataLines.size());
		}
		
	}
}
