package org.jcvi.jillion.vcf;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jcvi.jillion.core.io.InputStreamSupplier;
import org.jcvi.jillion.internal.core.io.TextLineParser;
import org.jcvi.jillion.vcf.VcfVisitor.InfoNumberType;
import org.jcvi.jillion.vcf.VcfVisitor.InfoNumberTypeAndValue;
import org.jcvi.jillion.vcf.VcfVisitor.InfoType;
import org.jcvi.jillion.vcf.VcfVisitor.VcfVisitorCallback;

public class VcfFileParser implements VcfParser{

	private static final Pattern META_PATTERN = Pattern.compile("^##(\\S+)=(\\S+)$"); 
	
	private static final Pattern FILTER_PATTERN = Pattern.compile("^##FILTER=<ID=(\\S+),Description=\"(.+)\">$"); 
	
	private static final Pattern FORMAT_PATTERN = Pattern.compile("^##FORMAT=<(.+)>$"); 
	private static final Pattern INFO_PATTERN = Pattern.compile("^##INFO=<(.+)>$"); 
	private static final Pattern CONTIG_PATTERN = Pattern.compile("^##contig=<(.+)>$"); 
	
	private static final  Pattern DESCRIPTION_PATTERN = Pattern.compile("Description=\"(.+?)\"");
	//This isn't actually good enough because a description might have a comma so we have to check for that
	//but it's a very complicated step
	private static final Pattern INNER_PATTERN = Pattern.compile("(\\s*,\\s*)?(.+?)=([^,]+)");
	
	
	private static final Pattern TAB_PATTERN = Pattern.compile("\t");
	
	
	private final InputStreamSupplier inputStreamSupplier;
	
	private static final String DESCRIPTION = "Description";
	
	private VcfFileParser(InputStreamSupplier inputStreamSupplier) {
		this.inputStreamSupplier = Objects.requireNonNull(inputStreamSupplier);
	}
	
	public static VcfParser createParserFor(File f) throws IOException{
		return createParserFor(InputStreamSupplier.forFile(f));
	}

	public static VcfParser createParserFor(InputStreamSupplier inputStreamSupplier) {
		return new VcfFileParser(inputStreamSupplier);
	}
	
	private class ParserCallback implements VcfVisitorCallback{

		private volatile boolean halt=false;
		
		@Override
		public void haltParsing() {
			halt=true;
			
		}
		
	}

	
	private static Map<String, String> handleTypedDataLine(String betweenAngleBrackets) {
		
		Map<String, String> map = new HashMap<>();
		
		Matcher descriptionMatcher = DESCRIPTION_PATTERN.matcher(betweenAngleBrackets);
		String restOfInput = betweenAngleBrackets;
		if(descriptionMatcher.find()) {
			//found a description
			String description = descriptionMatcher.group(1);
			restOfInput = new StringBuilder(betweenAngleBrackets)
								.replace(descriptionMatcher.start(), descriptionMatcher.end(), "")
								.toString();
			map.put(DESCRIPTION, description);
		}
		Matcher m = INNER_PATTERN.matcher(restOfInput);
		while(m.find()) {
			//group 1 will be the comma we are skipping over
			String key = m.group(2);
			String value = m.group(3);
			map.put(key, value);
		}
		return map;
	}
	@Override
	public void parse(VcfVisitor visitor) throws IOException {
		Objects.requireNonNull(visitor, "visitor can not be null");
		ParserCallback callback = new ParserCallback();
		
		try(TextLineParser parser = new TextLineParser(inputStreamSupplier.get())) {
			String line;
			boolean hasExtraFields=false;
			while( (line =parser.nextLine()) !=null) {
				//I think we still want trim() here not strip() ? do we have unicode spaces in vcf ?
				String trimmed = line.trim();
				
				//TODO move to state machine 
				if(trimmed.startsWith("##")) {
					if(trimmed.startsWith("##INFO=")) {
						Matcher m = INFO_PATTERN.matcher(trimmed);
						if(m.find()) {
							Map<String, String> map = handleTypedDataLine(m.group(1));
							InfoType type = InfoType.valueOf(map.get("Type"));
							
							visitor.visitInfo(callback,  map.get("ID"), type,
									InfoNumberTypeAndValue.parse(map.get("Number")), 
									map.get(DESCRIPTION), map);
						}
					}else if(trimmed.startsWith("##FILTER=")) {
						Matcher m = FILTER_PATTERN.matcher(trimmed);
						if(m.matches()) {
							visitor.visitFilter(callback, m.group(1), m.group(2));
						}else {
							throw new IOException("invalid FILTER LINE '" + trimmed + "'");
						}
					}else if(trimmed.startsWith("##FORMAT=")) {
						Matcher m = FORMAT_PATTERN.matcher(trimmed);
						if(m.find()) {
							Map<String, String> map = handleTypedDataLine(m.group(1));
							
							InfoType type = InfoType.valueOf(map.get("Type"));
							

							visitor.visitFormat(callback, map.get("ID"),
									type , InfoNumberTypeAndValue.parse(map.get("Number")), map.get(DESCRIPTION), map);
						}
					}else if(trimmed.startsWith("##contig=")) {
						Matcher m = CONTIG_PATTERN.matcher(trimmed);
						if(m.find()) {
							Map<String, String> map = handleTypedDataLine(m.group(1));
							Long length=null;
							try {
								length = Long.parseLong(map.get("length"));
							}catch(NumberFormatException e) {
								//ignore?
							}
							visitor.visitContigInfo(callback, map.get("ID"), length, map);
						}
					}else {
						//handle meta
						Matcher m = META_PATTERN.matcher(trimmed);
						if(m.matches()) {
							visitor.visitMetaInfo(callback, m.group(1), m.group(2));
						}
					}
				}else if(trimmed.startsWith("#CHROM")) {
					//header line
					String[] fields = TAB_PATTERN.split(trimmed);
					//there are 9 mandatory fields but there might be others
					
					List<String> extraFields = Arrays.stream(fields).skip(9).collect(Collectors.toList());
					hasExtraFields = !extraFields.isEmpty();
					visitor.visitHeader(callback, extraFields);
				}else {
					String[] fields = TAB_PATTERN.split(trimmed);
					//there are 9 mandatory fields but there might be others
					
					
					if(hasExtraFields) {
						List<String> extraFields = Arrays.stream(fields).skip(9).collect(Collectors.toList());
						
						visitor.visitData(callback, 
								fields[0], Integer.parseInt(fields[1]), 
								fields[2], fields[3], fields[4],
								Integer.parseInt(fields[5]),
								fields[6], fields[7], fields[8], extraFields);
					}else {
						visitor.visitData(callback, 
								fields[0], Integer.parseInt(fields[1]), 
								fields[2], fields[3], fields[4],
								Integer.parseInt(fields[5]),
								fields[6], fields[7], fields[8], Collections.emptyList());
					}
				}
				
				if(callback.halt) {
					break;
				}
			}
			if(callback.halt) {
				visitor.halted();
			}else {
				visitor.visitEnd();
			}
		}
		
	}

	
}
