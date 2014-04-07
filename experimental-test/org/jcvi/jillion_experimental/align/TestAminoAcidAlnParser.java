/*******************************************************************************
 * Copyright (c) 2009 - 2014 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
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
public class TestAminoAcidAlnParser {

	@Test
	public void parse() throws IOException{
		ResourceHelper helper = new ResourceHelper(TestAminoAcidAlnParser.class);
		File alnFile = helper.getFile("files/pep.aln");
		
		Map<String, String> expected = new HashMap<String,String>();
		
		expected.put("EPI489629", 
				"MKTIIALSYILCLVFAQKLPGNDNSTATLCLGHHAVPNGTIVKTITNDRIEVTNATELVQ"
						+ "NSSIGEICDSPHQILDGENCTLIDALLGDPQCDGFQNKKWDLFVERSKAYSNCYPYDVPD"
						+ "YASLRSLVASSGTLEFNNESFNWAGVTQNGTSSACIRGSNSSFFSRLNWLTHLNFKYPAL"
						+ "NVTMPNNEQFDKLYIWGVHHPGTDKDQIFLYAQSSGRITVSTRRSQQAVIPNIGSRPRIR"
						+ "NIPSRISIYWTIVKPGDILLINSTGNLIAPRGYFKIRSGKSSIMRSDAPIGKCKSECITP"
						+ "NGSIPNDKPFQNVNRITYGACPRYVKQSTLKLATGMRNVPEKQTRGIFGAIAGFIENGWE"
						+ "GMMDGWYGFRHQNSEGRGQAADLKSTQAAIDQINGKLNRLIGKTNEKFHQIEKEFSEVEG"
						+ "RIQNLEKYVEDTKIDLWSYNAELLVALENQHTIDLTDSEMNKLFEKTKKQLRENAEDMGN"
						+ "GCFKIYHKCDNACIGSIRNGTYDHDVYRDEALNNRFQIKGVELKSGYKDWILWISFAISC"
						+ "FLLCVALLGFIMWACQKGNIRCNICI"
				);
		
		expected.put("H3N2",
						"MKTIIALSYILCLVFTQKLPGNDNSTATLCLGHHAVPNGTIVKTITNDQIEVTNATELVQ"
						+ "SSSTGEICDSPHQILDGENCTLIDALLGDPQCDGFQNKKWDLFVERSKAYSNCYPYDVPD"
						+ "YASLRSLVASSGTLEFNNESFNWTGVTQNGTSSACIRRSNNSFFSRLNWLTHLKFKYPAL"
						+ "NVTMPNNEKFDKLYIWGVHHPGTDNDQIFPYAQASGRITVSTKRSQQTVIPNIGSRPRVR"
						+ "NIPSRISIYWTIVKPGDILLINSTGNLIAPRGYFKIRSGKSSIMRSDAPIGKCNSECITP"
						+ "NGSIPNDKPFQNVNRITYGACPRYVKQNTLKLATGMRNVPEKQTR---------------"
						+ "------------------------------------------------------------"
						+ "------------------------------------------------------------"
						+ "------------------------------------------------------------"
						+ "--------------------------"
				);
		
		String conservationLine = 
						"***************:********************************:***********" +
						".** ********************************************************" +
						"***********************:************* **.************:******" +
						"********:***************:**** ***:********:****:**********:*" +
						"*****************************************************:******" +
						"***************************.*****************";
		
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
