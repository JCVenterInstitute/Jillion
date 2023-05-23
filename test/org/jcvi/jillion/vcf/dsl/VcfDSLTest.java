package org.jcvi.jillion.vcf.dsl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.jcvi.jillion.vcf.VcfFormat;
import org.jcvi.jillion.vcf.VcfInfo;
import org.jcvi.jillion.vcf.dsl.VcfDSL.FlagVcfInfoDSL;
import org.jcvi.jillion.vcf.dsl.VcfDSL.FloatVcfInfoDSL;
import org.jcvi.jillion.vcf.dsl.VcfDSL.IntVcfFormatDSL;
import org.jcvi.jillion.vcf.dsl.VcfDSL.IntVcfInfoDSL;
import org.jcvi.jillion.vcf.dsl.VcfDSL.LineModeVcfDSL;
import org.jcvi.jillion.vcf.dsl.VcfDSL.StringVcfFormatDSL;
import org.jcvi.jillion.vcf.dsl.VcfDSL.StringVcfInfoDSL;
import org.jcvi.jillion.vcf.dsl.VcfDSL.VcfFile;
import org.junit.Test;

import static org.junit.Assert.*;

public class VcfDSLTest {

	private static final String EXPECTED_LINE = "20\t14370\trs6054257\tG\tA\t29\tPASS\tNS=3;DP=14;AF=0.5;DB;H2\tGT:GQ:DP:HQ\t0|0:48:1:51,51\t1|0:48:8:51,51\t1/1:43:5:.,.";

	@Test
	public void singleLine() throws IOException {
		VcfDSL dsl = new VcfDSL();
		
		IntVcfInfoDSL ns = dsl.intInfo(VcfInfo.RESERVED_NS);
		IntVcfInfoDSL dpInfo = dsl.intInfo(VcfInfo.RESERVED_DP);
		FloatVcfInfoDSL af = dsl.floatInfo(VcfInfo.RESERVED_AF);
		StringVcfInfoDSL aa = dsl.stringInfo(VcfInfo.RESERVED_AA);
		FlagVcfInfoDSL db = dsl.flagInfo(VcfInfo.RESERVED_DB);
		FlagVcfInfoDSL h2 = dsl.flagInfo(VcfInfo.RESERVED_H2);
		
		StringVcfFormatDSL gt = dsl.stringFormat(VcfFormat.RESERVED_GT);
		IntVcfFormatDSL gq = dsl.intFormat(VcfFormat.RESERVED_GQ);
		IntVcfFormatDSL dp = dsl.intFormat(VcfFormat.RESERVED_DP);
		IntVcfFormatDSL hq = dsl.intFormat(VcfFormat.RESERVED_HQ);
		
		VcfFile vcf = dsl.beginLines()
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
				.doneAllGenotypes()
				.done();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		vcf.writeVcf(out);
		String lastLine = getLastLine(out.toByteArray());
		assertEquals(EXPECTED_LINE, lastLine);
	}
	
	@Test
	public void outOfOrderFormat() throws IOException {
		VcfDSL dsl = new VcfDSL();
		
		IntVcfInfoDSL ns = dsl.intInfo(VcfInfo.RESERVED_NS);
		IntVcfInfoDSL dpInfo = dsl.intInfo(VcfInfo.RESERVED_DP);
		FloatVcfInfoDSL af = dsl.floatInfo(VcfInfo.RESERVED_AF);
		StringVcfInfoDSL aa = dsl.stringInfo(VcfInfo.RESERVED_AA);
		FlagVcfInfoDSL db = dsl.flagInfo(VcfInfo.RESERVED_DB);
		FlagVcfInfoDSL h2 = dsl.flagInfo(VcfInfo.RESERVED_H2);
		
		StringVcfFormatDSL gt = dsl.stringFormat(VcfFormat.RESERVED_GT);
		IntVcfFormatDSL gq = dsl.intFormat(VcfFormat.RESERVED_GQ);
		IntVcfFormatDSL dp = dsl.intFormat(VcfFormat.RESERVED_DP);
		IntVcfFormatDSL hq = dsl.intFormat(VcfFormat.RESERVED_HQ);
		
		VcfFile vcf = dsl.beginLines()
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
					
					.set(gq, 48)
					.set(gt,"0|0")
					.set(hq, 51,51)
					.set(dp, 1)
					.done()
				.add("NA00002")
					.set(gt,"1|0")
					.set(hq, 51,51)
					.set(dp, 8)
					.set(gq, 48)
					.done()
				.add("NA00003")
					.set(gt,"1/1")
					.set(dp, 5)
					.set(gq, 43)
					
					.set(hq, null,null) // unset
					.done()
				.doneAllGenotypes()
				.done();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		vcf.writeVcf(out);
		String lastLine = getLastLine(out.toByteArray());
		assertEquals(EXPECTED_LINE, lastLine);
	}
	
	@Test
	public void multipleCallsForGenotypesSameCoord() throws IOException {
		VcfDSL dsl = new VcfDSL();
		
		IntVcfInfoDSL ns = dsl.intInfo(VcfInfo.RESERVED_NS);
		IntVcfInfoDSL dpInfo = dsl.intInfo(VcfInfo.RESERVED_DP);
		FloatVcfInfoDSL af = dsl.floatInfo(VcfInfo.RESERVED_AF);
		StringVcfInfoDSL aa = dsl.stringInfo(VcfInfo.RESERVED_AA);
		FlagVcfInfoDSL db = dsl.flagInfo(VcfInfo.RESERVED_DB);
		FlagVcfInfoDSL h2 = dsl.flagInfo(VcfInfo.RESERVED_H2);
		
		StringVcfFormatDSL gt = dsl.stringFormat(VcfFormat.RESERVED_GT);
		IntVcfFormatDSL gq = dsl.intFormat(VcfFormat.RESERVED_GQ);
		IntVcfFormatDSL dp = dsl.intFormat(VcfFormat.RESERVED_DP);
		IntVcfFormatDSL hq = dsl.intFormat(VcfFormat.RESERVED_HQ);
		
		LineModeVcfDSL beginLines = dsl.beginLines();
		 beginLines
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
					
					.set(gq, 48)
					.set(gt,"0|0")
					.set(hq, 51,51)
					.set(dp, 1)
					.done()
				.add("NA00002")
					.set(gt,"1|0")
					.set(hq, 51,51)
					.set(dp, 8)
					.set(gq, 48)
					.done()
				
				.doneAllGenotypes();
				
		beginLines.line("20", 14370, "rs6054257", "G", "A", 29)
							.passed()
							.genotypes(gt,gq,dp,hq)
							.add("NA00003")
							.set(gt,"1/1")
							.set(dp, 5)
							.set(gq, 43)
							
							.set(hq, null,null) // unset
							.done()
							.doneAllGenotypes();
			
		VcfFile vcf = beginLines.done();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		vcf.writeVcf(out);
		Set<String> infos = new HashSet<>();
		getLinesThatMatch(out.toByteArray(), s-> s.contains("\t29\t"), s->infos.add(s.split("\t")[7]));
//		System.out.println(new String(out.toByteArray()));
		assertEquals(Set.of("NS=3;DP=14;AF=0.5;DB;H2",  "."), infos);
	}
	
	@Test
	public void outOfOrderGenoTypes() throws IOException {
		VcfDSL dsl = new VcfDSL();
		
		IntVcfInfoDSL ns = dsl.intInfo(VcfInfo.RESERVED_NS);
		IntVcfInfoDSL dpInfo = dsl.intInfo(VcfInfo.RESERVED_DP);
		FloatVcfInfoDSL af = dsl.floatInfo(VcfInfo.RESERVED_AF);
		StringVcfInfoDSL aa = dsl.stringInfo(VcfInfo.RESERVED_AA);
		FlagVcfInfoDSL db = dsl.flagInfo(VcfInfo.RESERVED_DB);
		FlagVcfInfoDSL h2 = dsl.flagInfo(VcfInfo.RESERVED_H2);
		
		StringVcfFormatDSL gt = dsl.stringFormat(VcfFormat.RESERVED_GT);
		IntVcfFormatDSL gq = dsl.intFormat(VcfFormat.RESERVED_GQ);
		IntVcfFormatDSL dp = dsl.intFormat(VcfFormat.RESERVED_DP);
		IntVcfFormatDSL hq = dsl.intFormat(VcfFormat.RESERVED_HQ);
		
		VcfFile vcf = dsl
				.addGenotype("NA00001")
				.addGenotype("NA00002")
				.addGenotype("NA00003")
				.beginLines()
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
				
				.add("NA00003")
					.set(gt,"1/1")
					.set(gq, 43)
					.set(dp, 5)
					.set(hq, null,null) // unset
					.done()
				.add("NA00002")
					.set(gt,"1|0")
					.set(gq, 48)
					.set(dp, 8)
					.set(hq, 51,51)
					.done()
				.doneAllGenotypes()
				.done();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		vcf.writeVcf(out);
		String lastLine = getLastLine(out.toByteArray( ));
		assertEquals(EXPECTED_LINE, lastLine);
	}
	
	private static String getLastLine(byte[] bytes) throws IOException {
		String[] last = new String[1];
		
		getLinesThatMatch(bytes, s-> true, s-> last[0]=s);
		return last[0];
	}
	private static void getLinesThatMatch(byte[] bytes, Predicate<String> filter, Consumer<String> consumer) throws IOException {
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)))){
			String line;
			while( (line=reader.readLine()) !=null) {
				if(filter.test(line)) {
					consumer.accept(line);
				}
			}
		}
	}
}
