package org.jcvi.common.core.assembly.asm.atac;


import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.common.core.DirectedRange;
import org.jcvi.common.core.Direction;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.assembly.asm.atac.AtacMatch.MatchType;
import org.jcvi.common.core.io.TextLineParser;

public final class AtacFileParser {

	private static final Pattern COMMENT_PATTERN = Pattern.compile("^#(.*)$");
	private static final Pattern MATCH_PATTERN = Pattern.compile("^M\\s+([cru])\\s+(\\S+)\\s+r(\\d+)\\s+(\\S+)\\s+(\\d+)\\s+(\\d+)\\s+1\\s+(\\S+)\\s+(\\d+)\\s+(\\d+)\\s+(-?1)");
	private AtacFileParser(){}
	
	public static void parse(InputStream atacStream, AtacFileVisitor visitor) throws IOException{
		TextLineParser parser = new TextLineParser(atacStream);
		visitor.visitFile();
		while(parser.hasNextLine()){
			String line = parser.nextLine();
			visitor.visitLine(line);
			Matcher matchPattern = MATCH_PATTERN.matcher(line);
			if(matchPattern.find()){
				String instanceIndex = matchPattern.group(2);
				long parentIndex = Long.parseLong(matchPattern.group(3));
				String firstAssemblyId = matchPattern.group(4);
				Range firstAssemblyRange = Range.createOfLength(
						Long.parseLong(matchPattern.group(5)), Long.parseLong(matchPattern.group(6)));
				String secondAssemblyId = matchPattern.group(7);
				Range secondAssemblyRange = Range.createOfLength(
						Long.parseLong(matchPattern.group(8)), Long.parseLong(matchPattern.group(9)));
				Direction direction = Integer.parseInt(matchPattern.group(10)) ==1?
						Direction.FORWARD : Direction.REVERSE;
				char matchChar = matchPattern.group(1).charAt(0);
				switch(matchChar){
				case 'u' : visitor.visitMatch(
						new DefaultAtacMatch(MatchType.UNGAPPED, instanceIndex, parentIndex, firstAssemblyId, firstAssemblyRange, secondAssemblyId, DirectedRange.create(secondAssemblyRange, direction)));
						
					break;
				case 'r' : 
					new DefaultAtacMatch(MatchType.GAPPED, instanceIndex, parentIndex, firstAssemblyId, firstAssemblyRange, secondAssemblyId, DirectedRange.create(secondAssemblyRange, direction));
					
				break;
				default:
					//ignore
				}
			}else{
				Matcher commentMatcher = COMMENT_PATTERN.matcher(line);
				if(commentMatcher.find()){
					visitor.visitComment(commentMatcher.group(1).trim());
				}
				
			}
		}
		visitor.visitEndOfFile();
	}
}
