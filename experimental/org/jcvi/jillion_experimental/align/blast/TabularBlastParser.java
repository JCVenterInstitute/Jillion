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
package org.jcvi.jillion_experimental.align.blast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.internal.core.io.OpenAwareInputStream;
import org.jcvi.jillion.internal.core.io.TextLineParser;

/**
 * {@code TabularBlastParser}
 * can parse the "tabular" encoded blast output
 * that is usually created using the "-m 8" or "-m 9" options in blast. 
 * 
 * @author dkatzel
 *
 *
 */
public abstract class TabularBlastParser implements BlastParser{

	private static final Pattern TYPE_PATTERN = Pattern.compile("^# (\\S*BLAST\\S+).*");
    private static final Pattern HIT_PATTERN = Pattern.compile(
       //AF178033 EMORG:AF031391  85.48             806     117          0       1       806         99       904     1e-179     644.8
            "(\\S+)\\s+(\\S+)\\s+(\\d+\\.?\\d*)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\S+)\\s+(\\d+\\.?\\d*)");
           
   
    private TabularBlastParser(){}
    
    public static BlastParser create(File file) throws IOException{
        return new FileBasedTabularBlastParser(file);
    }
    public static BlastParser create(InputStream in) throws IOException{
        return new InputStreamBasedTabularBlastParser(in);
    }
    protected void parse(InputStream tabularBlastOutput, BlastVisitor visitor) throws IOException{
        TextLineParser parser = new TextLineParser(tabularBlastOutput);
        boolean parsedHeader=false;
        String type=null;
        try{
        	 BlastHitImpl.Builder blastHitBuilder=null;
	           
	        while(parser.hasNextLine()){
	            String line = parser.nextLine();
	            if(!parsedHeader && type==null){
	            	Matcher headerMatcher = TYPE_PATTERN.matcher(line);
	            	if(headerMatcher.find()){
	            		type = headerMatcher.group(1);
	            		parsedHeader=true;
	            	}
	            }
	            Matcher matcher = HIT_PATTERN.matcher(line);
	            String prevQuery=null, prevSubject=null;
	            
	            if(matcher.find()){
	            	parsedHeader=true;
	                DirectedRange queryRange = DirectedRange.parse(matcher.group(7), matcher.group(8), CoordinateSystem.RESIDUE_BASED);
	                DirectedRange subjectRange = DirectedRange.parse(matcher.group(9), matcher.group(10), CoordinateSystem.RESIDUE_BASED);
	           
					HspBuilder<?, ?> hspBuilder;
					if(type!=null){
						hspBuilder= HspBuilder.forType(type);
					}else{
						//doesn't really matter since we don't have
						//sequences anyway ?
						hspBuilder= HspBuilder.forBlastN();
					}
					String queryId = matcher.group(1);
					String subjectId = matcher.group(2);
					
					Hsp<?,?> hsp =hspBuilder.query(queryId)
	                                .subject(subjectId)
	                                .percentIdentity(Double.parseDouble(matcher.group(3)))
	                                .alignmentLength(Integer.parseInt(matcher.group(4)))
	                                .numMismatches(Integer.parseInt(matcher.group(5)))
	                                .numGapOpenings(Integer.parseInt(matcher.group(6)))                                
	                                .eValue(new BigDecimal(matcher.group(11)))
	                                .bitScore(new BigDecimal(matcher.group(12)))              
	                                .queryRange(queryRange)
	                                .subjectRange(subjectRange)
	                                .build();
					//accumulate consecutive HSPs if the subject and query match previous?
					if(!queryId.equals(prevQuery) || !subjectId.equals(prevSubject)){
						if(blastHitBuilder !=null){
							visitor.visitHit(blastHitBuilder.build());
						}
						blastHitBuilder = new BlastHitImpl.Builder(queryId, subjectId);
					}
	                blastHitBuilder.addHsp(hsp);
	                prevQuery = queryId;
	                prevSubject = subjectId;
	                
	            }
	        }
	        if(blastHitBuilder !=null){
				visitor.visitHit(blastHitBuilder.build());
			}
	        visitor.visitEnd();
        }finally{
        	IOUtil.closeAndIgnoreErrors(parser);
        }
    }
    
    
   


	private static final class FileBasedTabularBlastParser extends TabularBlastParser{
    	private final File file;
    	
    	
		public FileBasedTabularBlastParser(File file) {
			this.file = file;
		}

		@Override
		public boolean canParse() {
			return true;
		}

		@Override
		public void parse(BlastVisitor visitor) throws IOException {
			InputStream in = new FileInputStream(file);
			parse(in, visitor);
		}
    	
    }
    
    private static final class InputStreamBasedTabularBlastParser extends TabularBlastParser{
    	private final OpenAwareInputStream in;
    	
    	
		public InputStreamBasedTabularBlastParser(InputStream in) {
			this.in = new OpenAwareInputStream(in);
		}

		@Override
		public boolean canParse() {
			return in.isOpen();
		}

		@Override
		public void parse(BlastVisitor visitor) throws IOException {
			if(!canParse()){
				throw new IllegalStateException("can not parse inputstream, already closed"	);
			}			
			parse(in, visitor);
		}
    	
    }
   
}
