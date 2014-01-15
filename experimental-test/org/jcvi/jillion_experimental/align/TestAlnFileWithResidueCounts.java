package org.jcvi.jillion_experimental.align;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jcvi.jillion.internal.ResourceHelper;
import org.junit.Test;

public class TestAlnFileWithResidueCounts {
	@Test
	public void parse() throws IOException{
		ResourceHelper helper = new ResourceHelper(TestAminoAcidAlnParser.class);
		File alnFile = helper.getFile("files/pepWithCumulativeCounts.aln");
		
		Map<String, String> expected = new HashMap<String,String>();
		
		expected.put("FOSB_MOUSE", 
						"MFQAFPGDYDSGSRCSSSPSAESQYLSSVDSFGSPPTAAASQECAGLGEMPGSFVPTVTA"
						+ "ITTSQDLQWLVQPTLISSMAQSQGQPLASQPPAVDPYDMPGTSYSTPGLSAYSTGGASGS"
						+ "GGPSTSTTTSGPVSARPARARPRRPREETLTPEEEEKRRVRRERNKLAAAKCRNRRRELT"
						+ "DRLQAETDQLEEEKAELESEIAELQKEKERLEFVLVAHKPGCKIPYEEGPGPGPLAEVRD"
						+ "LPGSTSAKEDGFGWLLPPPPPPPLPFQSSRDAPPNLTASLFTHSEVQVLGDPFPVVSPSY"
						+ "TSSFVLTCPEVSAFAGAQRTSGSEQPSDPLNSPSLLAL"
				);
		
		expected.put("FOSB_HUMAN",
						"MFQAFPGDYDSGSRCSSSPSAESQYLSSVDSFGSPPTAAASQECAGLGEMPGSFVPTVTA"
						+ "ITTSQDLQWLVQPTLISSMAQSQGQPLASQPPVVDPYDMPGTSYSTPGMSGYSSGGASGS"
						+ "GGPSTSGTTSGPGPARPARARPRRPREETLTPEEEEKRRVRRERNKLAAAKCRNRRRELT"
						+ "DRLQAETDQLEEEKAELESEIAELQKEKERLEFVLVAHKPGCKIPYEEGPGPGPLAEVRD"
						+ "LPGSAPAKEDGFSWLLPPPPPPPLPFQTSQDAPPNLTASLFTHSEVQVLGDPFPVVNPSY"
						+ "TSSFVLTCPEVSAFAGAQRTSGSDQPSDPLNSPSLLAL"
				);
		
		String conservationLine = 
							"************************************************************"
							+ "********************************.***************:*.**:******"
							+ "****** ***** .**********************************************"
							+ "************************************************************"
							+ "****:.******.**************:*:**************************.***"
							+ "***********************:**************";
		
		final Map<String,StringBuilder> actual = new HashMap<String, StringBuilder>();
		final StringBuilder actualConservation = new StringBuilder();
		AlnVisitor visitor = new AlnVisitor() {
			
			@Override
			public AlnGroupVisitor visitGroup(Set<String> ids,
					AlnVisitorCallback callback) {
				return new AlnGroupVisitor() {
					
					@Override
					public void visitEndGroup() {
						//no-op
						
					}
					
					@Override
					public void visitConservationInfo(List<ConservationInfo> conservationInfos) {
						for(ConservationInfo info: conservationInfos){
							actualConservation.append(info.asChar());
						}
					
						
					}
					
					@Override
					public void visitAlignedSegment(String id, String gappedAlignment) {
						if(!actual.containsKey(id)){
							actual.put(id, new StringBuilder());
						}
						actual.get(id).append(gappedAlignment);
						
					}
				};
			}
			
			@Override
			public void visitEnd() {
				//no-op
				
			}
			
			@Override
			public void halted() {
				//no-op
				
			}

			@Override
			public void visitHeader(String header) {
				//no-op
				
			}
		};
		
		AlnFileParser.create(alnFile).parse(visitor);
		
		for(Entry<String, String> entry : expected.entrySet()){
			String id = entry.getKey();
			
			assertEquals(id, entry.getValue(), actual.get(id).toString());
		}
		assertEquals(conservationLine, actualConservation.toString().trim());		
				
		
	}
}
