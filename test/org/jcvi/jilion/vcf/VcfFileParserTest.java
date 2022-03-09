package org.jcvi.jilion.vcf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.ResourceHelper;
import org.jcvi.jillion.internal.core.util.Sneak;
import org.jcvi.jillion.vcf.AbtractVcfVisitor;
import org.jcvi.jillion.vcf.VcfFileParser;
import org.jcvi.jillion.vcf.VcfFileWriter;
import org.jcvi.jillion.vcf.VcfFilter;
import org.jcvi.jillion.vcf.VcfFormat;
import org.jcvi.jillion.vcf.VcfHeader;
import org.jcvi.jillion.vcf.VcfInfo;
import org.jcvi.jillion.vcf.VcfNumber;
import org.jcvi.jillion.vcf.VcfParser;
import org.jcvi.jillion.vcf.VcfValueType;
import org.jcvi.jillion.vcf.VcfVisitor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class VcfFileParserTest {
	private static final ResourceHelper RESOURCES = new ResourceHelper(VcfFileParserTest.class);

	@Rule
	public TemporaryFolder tmpDir = new TemporaryFolder();
	
	@Test
	public void parseFileCollectToHeaderObjectAndHalt() throws IOException{

		File f = RESOURCES.getFile("files/example.vcf");
		
		VcfParser parser = VcfFileParser.createParserFor(f);
		AtomicBoolean halted = new AtomicBoolean(false);
		
		
		VcfHeader expectedHeader = createExpectedHeader();
		
		parser.parse(new AbtractVcfVisitor() {

			@Override
			public void visitData(VcfVisitorCallback callback, String chromId, int position, String id, String refBase,
					String altBase, int quality, String filter, String info, String format, List<String> extraFields) {
				fail("should not get this far");
				
			}

			@Override
			protected void visitHeader(VcfVisitorCallback callback, VcfHeader actual) {
				
				assertEquals(expectedHeader, actual);
				callback.haltParsing();
			}

			@Override
			public void halted() {
				halted.set(true);  
			}
			
			@Override
			public void visitEnd() {
				fail("should call halted not end");
			}
			
			
		});
		
		assertTrue(halted.get());
		
	}
	private VcfHeader createExpectedHeader() {
		VcfHeader expectedHeader = VcfHeader.builder()
				.property("fileformat", "VCFv4.0")
				.property("fileDate", "20090805")
				.property( "source","myImputationProgramV3.1")
				.property("reference", "1000GenomesPilot-NCBI36")
				.property("phasing", "partial")
				
				.filter(new VcfFilter( "q10", "Quality below 10"))
				.filter(new VcfFilter( "s50", "Less than 50% of samples have data"))
				
				
				.info(VcfInfo.builder()
						.id("NS")
						.number(VcfNumber.valueOf(1))
						.type( VcfValueType.Integer)
						.description("Number of Samples With Data")
						.parameter("ID", "NS")
						.parameter("Number", "1")
						.parameter("Type", "Integer")
						.parameter("Description", "Number of Samples With Data")
						.build())
				
				.info(VcfInfo.builder()
						.id("DP")
						.number(VcfNumber.valueOf(1))
						.type( VcfValueType.Integer)
						.description("Total Depth")
						.parameter("ID", "DP")
						.parameter("Number", "1")
						.parameter("Type", "Integer")
						.parameter("Description", "Total Depth")
						.build())
				
				.info(VcfInfo.builder()
						.id("AF")
						.number(VcfNumber.DOT)
						.type( VcfValueType.Float)
						.description("Allele Frequency")
						.parameter("ID", "AF")
						.parameter("Number", ".")
						.parameter("Type", "Float")
						.parameter("Description", "Allele Frequency")
						.build())
				
				.info(VcfInfo.builder()
						.id("AA")
						.number(VcfNumber.valueOf(1))
						.type( VcfValueType.String)
						.description("Ancestral Allele")
						.parameter("ID", "AA")
						.parameter("Number", "1")
						.parameter("Type", "String")
						.parameter("Description", "Ancestral Allele")
						.build())
				
				.info(VcfInfo.builder()
						.id("DB")
						.number(VcfNumber.valueOf(0))
						.type( VcfValueType.Flag)
						.description("dbSNP membership, build 129")
						.parameter("ID", "DB")
						.parameter("Number", "0")
						.parameter("Type", "Flag")
						.parameter("Description", "dbSNP membership, build 129")
						
						.build())
				
				.info(VcfInfo.builder()
						.id("H2")
						.number(VcfNumber.valueOf(0))
						.type( VcfValueType.Flag)
						.description("HapMap2 membership")
						.parameter("ID", "H2")
						.parameter("Number", "0")
						.parameter("Type", "Flag")
						.parameter("Description", "HapMap2 membership")
						.build())
				
				.format( VcfFormat.builder()
						.id("GT")
						.number(VcfNumber.valueOf(1))
						.type(VcfValueType.String)
						.description("Genotype")
						
						.parameter("ID", "GT")
						.parameter("Number", "1")
						.parameter("Type", "String")
						.parameter("Description", "Genotype")
						.build())
				.format( VcfFormat.builder()
						.id("GQ")
						.number(VcfNumber.valueOf(1))
						.type(VcfValueType.Integer)
						.description("Genotype Quality")
						.parameter("ID", "GQ")
						.parameter("Number", "1")
						.parameter("Type", "Integer")
						.parameter("Description", "Genotype Quality")
						.build())
				
				.format( VcfFormat.builder()
						.id("DP")
						.number(VcfNumber.valueOf(1))
						.type(VcfValueType.Integer)
						.description("Read Depth")
						.parameter("ID", "DP")
						.parameter("Number", "1")
						.parameter("Type", "Integer")
						.parameter("Description", "Read Depth")
						.build())
				.format( VcfFormat.builder()
						.id("HQ")
						.number(VcfNumber.valueOf(2))
						.type(VcfValueType.Integer)
						.description("Haplotype Quality")
						.parameter("ID", "HQ")
						.parameter("Number", "2")
						.parameter("Type", "Integer")
						.parameter("Description", "Haplotype Quality")
						.build())
				
				.extraColumns(Arrays.asList("NA00001","NA00002","NA00003"))
				.build();
		return expectedHeader;
	}
	@Test
	public void parseFileCallVisitMethods() throws IOException{
		File f = RESOURCES.getFile("files/example.vcf");
		
		VcfParser parser = VcfFileParser.createParserFor(f);
		
		
		VcfVisitor visitor = addExpectedVisitCalls();
		
		
 		
		parser.parse(visitor);
		
		verify(visitor);
	}
	/**
	 * Parse vcf collect visit info to Jillion objects and write out that same data
	 * as a new vcf file.  then parse that new file and make sure we get identical visits.
	 * @throws IOException
	 */
	@Test
	public void writeIdenticalVcf() throws IOException{
		
		File outputVcf = tmpDir.newFile("identical.vcf");
		
		File f = RESOURCES.getFile("files/example.vcf");
		
		VcfParser parser = VcfFileParser.createParserFor(f);
		
		parser.parse(new AbtractVcfVisitor() {

			VcfFileWriter writer;
			@Override
			public void visitData(VcfVisitorCallback callback, String chromId, int position, String id, String refBase,
					String altBase, int quality, String filter, String info, String format, List<String> extraFields) {
				try {
					writer.writeData(chromId, position, id, refBase, altBase, quality, filter, info, format, extraFields);
				} catch (IOException e) {
					Sneak.sneakyThrow(e);
				}
				
			}

			@Override
			protected void visitHeader(VcfVisitorCallback callback, VcfHeader build) {
				try {
					writer = new VcfFileWriter(build, new PrintWriter(outputVcf));
				} catch (FileNotFoundException e) {
					Sneak.sneakyThrow(e);
				}
				
			}

			@Override
			public void visitEnd() {
				IOUtil.closeAndIgnoreErrors(writer);
			}
			
			
			
		});
		
		//make sure expected visits work on new file
		
		VcfVisitor visitor = addExpectedVisitCalls();
		
		
// 		Files.lines(outputVcf.toPath()).forEach(c -> System.out.println(c));
 		
		VcfFileParser.createParserFor(outputVcf).parse(visitor);
		
		verify(visitor);
		
	}
	
	private VcfVisitor addExpectedVisitCalls() {
		VcfVisitor visitor = createMock(VcfVisitor.class);
		
		expectVisitMetaInfo(visitor, "fileformat", "VCFv4.0");
		expectVisitMetaInfo(visitor, "fileDate", "20090805");
		expectVisitMetaInfo(visitor, "source","myImputationProgramV3.1");
		expectVisitMetaInfo(visitor, "reference", "1000GenomesPilot-NCBI36");
		expectVisitMetaInfo(visitor, "phasing", "partial");
		
		expectVisitInfo(visitor, "NS",VcfNumber.valueOf(1), VcfValueType.Integer, "Number of Samples With Data");
		expectVisitInfo(visitor, "DP",VcfNumber.valueOf(1), VcfValueType.Integer, "Total Depth");
		expectVisitInfo(visitor, "AF",VcfNumber.DOT, VcfValueType.Float, "Allele Frequency");
		expectVisitInfo(visitor, "AA",VcfNumber.valueOf(1), VcfValueType.String, "Ancestral Allele");
		expectVisitInfo(visitor, "DB",VcfNumber.valueOf(0), VcfValueType.Flag, "dbSNP membership, build 129");
		expectVisitInfo(visitor, "H2",VcfNumber.valueOf(0), VcfValueType.Flag, "HapMap2 membership");
		
	
		expectFilter(visitor, "q10", "Quality below 10");
		expectFilter(visitor, "s50", "Less than 50% of samples have data");
		
		expectVisitFormat(visitor, "GT", VcfNumber.valueOf(1), VcfValueType.String, "Genotype");
		expectVisitFormat(visitor, "GQ", VcfNumber.valueOf(1), VcfValueType.Integer, "Genotype Quality");
		expectVisitFormat(visitor, "DP", VcfNumber.valueOf(1), VcfValueType.Integer, "Read Depth");
		expectVisitFormat(visitor, "HQ", VcfNumber.valueOf(2), VcfValueType.Integer, "Haplotype Quality");
		
		expectVisitHeader(visitor, "NA00001","NA00002","NA00003");
		
		
		
		expectVisitData(visitor, "20", 14370, "rs6054257", "G", "A", 29, "PASS", "NS=3;DP=14;AF=0.5;DB;H2", "GT:GQ:DP:HQ", 
				"0|0:48:1:51,51",	"1|0:48:8:51,51",	"1/1:43:5:.,.");
		
		expectVisitData(visitor, "20", 17330, ".", "T", "A", 3, "q10", "NS=3;DP=11;AF=0.017","GT:GQ:DP:HQ", 
				"0|0:49:3:58,50",	"0|1:3:5:65,3",	"0/0:41:3");
		
		expectVisitData(visitor, "20", 1110696, "rs6040355", "A", "G,T", 67, "PASS", "NS=2;DP=10;AF=0.333,0.667;AA=T;DB","GT:GQ:DP:HQ", 
				"1|2:21:6:23,27",	"2|1:2:0:18,2",	"2/2:35:4");
		
		expectVisitData(visitor, "20", 1230237, ".", "T", ".", 47, "PASS", "NS=3;DP=13;AA=T",	"GT:GQ:DP:HQ", 
				"0|0:54:7:56,60",	"0|0:48:4:51,51",	"0/0:61:2");
		
		expectVisitData(visitor, "20", 1234567, "microsat1", "GTCT", "G,GTACT", 50, "PASS", "NS=3;DP=9;AA=G",	"GT:GQ:DP", 
				"0/1:35:4",	"0/2:17:2",	"1/1:40:3");
		

		visitor.visitEnd();
		replay(visitor);
		return visitor;
	}
	private void expectVisitData(VcfVisitor mock, String chomId, int pos, String id, String ref, String alt, int qual, 
			String filter, String info, String format, String...extraFields) {
		/*
		void visitData(VcfVisitorCallback callback, String chromId, int position, String id, String refBase, String altBase,
				int quality, String filter, String info, String format, List<String> extraFields);
				*/
		if(extraFields ==null) {
			mock.visitData(isA(VcfVisitor.VcfVisitorCallback.class),eq(chomId), eq(pos), eq(id), eq(ref), eq(alt), eq(qual),
					eq(filter), eq(info), eq(format), eq(Collections.emptyList()));
		}else {
			mock.visitData(isA(VcfVisitor.VcfVisitorCallback.class),eq(chomId), eq(pos), eq(id), eq(ref), eq(alt), eq(qual),
					eq(filter), eq(info), eq(format), eq(List.of(extraFields)));
		}
	}
	
	private void expectVisitMetaInfo(VcfVisitor mock, String key, String value) {
		mock.visitMetaInfo(isA(VcfVisitor.VcfVisitorCallback.class),
				eq(key), eq(value));          
	}
	//ID=NS,Number=1,Type=Integer,Description="Number of Samples With Data"
	@SuppressWarnings("unchecked")
	private void expectVisitInfo(VcfVisitor mock, String id, VcfNumber numberAndType, 
			VcfValueType type,  String description) {
		mock.visitInfo(isA(VcfVisitor.VcfVisitorCallback.class), eq(id), eq(type), eq(numberAndType), eq(description), (Map<String, String>) isA(Map.class));
	}
	
	@SuppressWarnings("unchecked")
	private void expectVisitFormat(VcfVisitor mock, String id, VcfNumber numberAndType, 
			VcfValueType type,  String description) {
		mock.visitFormat(isA(VcfVisitor.VcfVisitorCallback.class), eq(id), eq(type), eq(numberAndType), eq(description), (Map<String, String>) isA(Map.class));
	}
	
	private void expectFilter(VcfVisitor mock, String id, String description) {
		mock.visitFilter(isA(VcfVisitor.VcfVisitorCallback.class), eq(id), eq(description));
	}
	
	private void expectVisitHeader(VcfVisitor mock, String...extraFields) {
		if(extraFields ==null) {
			mock.visitHeader(isA(VcfVisitor.VcfVisitorCallback.class), eq(Collections.emptyList()));
		}else {
			mock.visitHeader(isA(VcfVisitor.VcfVisitorCallback.class), eq(List.of(extraFields)));
		}
	}
	@Test
	public void getmementoAtHeaderLineAndReparseFromThere() throws IOException{

		File f = RESOURCES.getFile("files/example.vcf");
		
		VcfParser parser = VcfFileParser.createParserFor(f);
		VcfVisitor.VcfMemento[] memento = new  VcfVisitor.VcfMemento[1];
		List<String> expectedExtraFields = new ArrayList<>();
		parser.parse(new AbtractVcfVisitor() {

			@Override
			public void visitData(VcfVisitorCallback callback, String chromId, int position, String id, String refBase,
					String altBase, int quality, String filter, String info, String format, List<String> extraFields) {
				fail("should not get a data line");
				
			}

			@Override
			protected void visitHeader(VcfVisitorCallback callback, VcfHeader header) {
				memento[0] = callback.createMemento();
				callback.haltParsing();
				expectedExtraFields.addAll(header.getExtraColumns());
				
			}
		
		});
		
		parser.parse(new VcfVisitor() {

			@Override
			public void visitEnd() {

				
			}

			@Override
			public void halted() {
				
			}

			@Override
			public void visitMetaInfo(VcfVisitorCallback callback, String key, String value) {
				
			}

			@Override
			public void visitFilter(VcfVisitorCallback callback, String key, String description) {
				
			}

			@Override
			public void visitInfo(VcfVisitorCallback callback, String id, VcfValueType type,
					VcfNumber number, String description, Map<String, String> parameters) {
				
			}

			@Override
			public void visitFormat(VcfVisitorCallback callback, String id, VcfValueType infoType,
					VcfNumber number, String description, Map<String, String> parameters) {
				
			}

			@Override
			public void visitContigInfo(VcfVisitorCallback callback, String contigId, Long length,
					Map<String, String> parameters) {
				
			}

			@Override
			public void visitHeader(VcfVisitorCallback callback, List<String> extraColumns) {
				assertEquals(expectedExtraFields, extraColumns);
				callback.haltParsing();
				
			}

			@Override
			public void visitData(VcfVisitorCallback callback, String chromId, int position, String id, String refBase,
					String altBase, int quality, String filter, String info, String format, List<String> extraFields) {
				
			}
			
		}, memento[0]);
		
	}
	
	
	@Test
	public void getmementoAtFirstDataLineAndReparseFromThere() throws IOException{

		File f = RESOURCES.getFile("files/example.vcf");
		
		VcfParser parser = VcfFileParser.createParserFor(f);
		
		
		
		VcfHeader expectedHeader = createExpectedHeader();
		
		VcfVisitor.VcfMemento[] memento = new  VcfVisitor.VcfMemento[1];
		
		VcfVisitor mockVisitor = createMock(VcfVisitor.class);
		
		parser.parse(new AbtractVcfVisitor() {
			boolean firstTime=true;
			@Override
			public void visitData(VcfVisitorCallback callback, String chromId, int position, String id, String refBase,
					String altBase, int quality, String filter, String info, String format, List<String> extraFields) {
				if(firstTime) {
					memento[0] = callback.createMemento();
					firstTime=false;
				}
				//our first pass through the data lines we record all visit calls 
				//so we can make sure when we use memento we get them all back
				mockVisitor.visitData(isA(VcfVisitorCallback.class), eq(chromId), eq(position), eq(id), 
						eq(refBase), eq(altBase), eq(quality), eq(filter), eq(info), eq(format), eq(extraFields));
				
			}

			@Override
			protected void visitHeader(VcfVisitorCallback callback, VcfHeader actual) {
				
				assertEquals(expectedHeader, actual);
				
				
			}

			
			
			@Override
			public void visitEnd() {
				mockVisitor.visitEnd();
				replay(mockVisitor);
			}
			
			
		});
		
		parser.parse(mockVisitor, memento[0]);
		verify(mockVisitor);
	}
}
