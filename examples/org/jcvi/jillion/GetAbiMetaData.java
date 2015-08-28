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
package org.jcvi.jillion;

import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
import org.jcvi.jillion.fasta.nt.NucleotideFastaWriter;
import org.jcvi.jillion.fasta.nt.NucleotideFastaWriterBuilder;
import org.jcvi.jillion.fasta.pos.PositionFastaWriter;
import org.jcvi.jillion.fasta.pos.PositionFastaWriterBuilder;
import org.jcvi.jillion.fasta.qual.QualityFastaWriter;
import org.jcvi.jillion.fasta.qual.QualityFastaWriterBuilder;
import org.jcvi.jillion.trace.chromat.Chromatogram;
import org.jcvi.jillion.trace.chromat.ChromatogramFactory;
import org.jcvi.jillion.trace.chromat.ChromatogramWriter;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogram;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogramBuilder;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogramWriterBuilder;
/**
 * Jillion version of JTC get_chromo_metadata PLUS this 
 * program also converts the ab1 files into ZTRs.
 * For each {@code .ab1} file in the given 
 * input directory, the seq,qual,pos and trace comments (metadata)
 * are written to output multifasta files and metadata file.
 * In addition, the RunDate, Date, Channel Signal and Channel Noise
 * comments are parsed and additonal new comments are added to the metadata
 * file and output ztrs which split the dates into start and end dates
 * and separate the signal and noise comments by channel, one extra 
 * comment per channel.
 * 
 * @author dkatzel
 *
 */
public class GetAbiMetaData {
	/**
	 * Convert a folder of ab1 files into
	 * ztr files AND write out new files for the metadata (comments),
	 * and multifasta of seq, qual and pos
	 * @param args argument list:
	 * <ol>
	 * <li> input directory path containing abi files.</li>
	 * <li>file prefix for seq,qual, pos and metadata files.  The output will
	 * be named $prefix.seq $prefix.qual etc</li>
	 * <li>optional output directory path, if not specfied, then the
	 * output directory will be the same as input directory.  This directory
	 * will be created if does not exist.</li>
	 * </ol>
	 * 
	 * Returns non-zero exit code if invalid arguments specified.
	 * @throws IOException
	 * 
	 */
	public static void main(String[] args) throws IOException {
		if(args.length !=2 && args.length !=3){
			System.err.println("must provide 2 or 3 arguments $inputDir, $prefix [$outputDir]");
			//to provide error code, otherwise JVM return 0!
			System.exit(1);
		}
		File inputDir = new File(args[0]);
		String prefix = args[1];
		
		final File outputdir;
		if(args.length ==3){
			outputdir = new File(args[2]);
		
			if(outputdir !=null && !outputdir.exists()){
				if(!outputdir.mkdirs()){
					throw new IOException("error creating output dir");
				}
			}
		}else{
			outputdir = inputDir;
		}
		
		PrintWriter metaDataWriter = new PrintWriter(new File(outputdir,prefix+".meta"));
		//numbersPerLine call in builders is to match legacy io_lib output exactly
		NucleotideFastaWriter seqWriter = new NucleotideFastaWriterBuilder(new File(outputdir,prefix+".seq"))
															.numberPerLine(60)
															.build();
		QualityFastaWriter qualWriter = new QualityFastaWriterBuilder(new File(outputdir, prefix+".qual"))
															.numberPerLine(20)
															.build();
		
		PositionFastaWriter posWriter = new PositionFastaWriterBuilder(new File(outputdir, prefix+".pos"))
															.numberPerLine(20)
															.build();
		
		try{
			for(File ab1File : inputDir.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					return pathname.getName().endsWith(".ab1");
				}
			})){
				//chromatogramFactory can parse ab1, scf, and ztr files
				//specifies id to use including extension to match legacy code.
				//by default id created from factory excludes file extensions (.ab1)
				Chromatogram chromo = ChromatogramFactory.create(ab1File.getName(), ab1File);
				
				String id = chromo.getId();
				seqWriter.write(id, chromo.getNucleotideSequence());
				qualWriter.write(id, chromo.getQualitySequence());
				posWriter.write(id, chromo.getPeakSequence());
				
				
				
				Map<String,String> updatedComments = new LinkedHashMap<String,String>();
		
				Map<Nucleotide,Float> noiseMap = new EnumMap<Nucleotide,Float>(Nucleotide.class);
				Map<Nucleotide,Integer> signalMap = new EnumMap<Nucleotide,Integer>(Nucleotide.class);
				
				//write metadata
				for(Entry<String, String> entry : chromo.getComments().entrySet()){
					
					String commentKey = entry.getKey();
					//add comment as is to updated comments
					updatedComments.put(commentKey, entry.getValue());
					
					
					//each line in file is $filename.$key=$value
					metaDataWriter.printf("%s.%s=%s%n",id,commentKey, entry.getValue());
					//this code matches legacy JTC get_chromo_metadata.c code 
					//to parse specific comments to add additional comments
					if(commentKey.equals("RUND")){
						String[] dates =entry.getValue().split(" - ");
						metaDataWriter.printf("%s.RUNDStart=%s%n",id, dates[0]);
						metaDataWriter.printf("%s.RUNDEnd=%s%n", id,dates[1]);
						//add new comments
						updatedComments.put("RUNDStart", dates[0]);
						updatedComments.put("RUNDEnd", dates[1]);
						
					}else if(commentKey.equals("DATE")){
						String[] dates = entry.getValue().split(" to ");
						metaDataWriter.printf("%s.DATEStart=%s%n",id, dates[0]);
						metaDataWriter.printf("%s.DATEEnd=%s%n", id,dates[1]);
						//add new comments
						updatedComments.put("DATEStart", dates[0]);
						updatedComments.put("DATEEnd", dates[1]);
					}else if(commentKey.equals("SIGN")  || commentKey.equals("NOIS")){
						for(String channelData : entry.getValue().split(",")){
							//channelData in format $channel:$value
							//write out new line one per channel in the form 
							//example
							//		SIGN=A:327,C:229...
							//becomes:
							//		SIGNA=327
							//		SIGNC=229
							Nucleotide channel = Nucleotide.parse(channelData.charAt(0));
							if(commentKey.equals("SIGN")){
								signalMap.put(channel, Integer.parseInt(channelData.substring(2)));
							}else{
								noiseMap.put(channel, Float.parseFloat(channelData.substring(2)));
							}
							metaDataWriter.printf("%s.%s%s=%s%n", id,commentKey,
									channelData.charAt(0), channelData.substring(2) );
							
							//add new comments
							updatedComments.put(String.format("%s%s", commentKey, channelData.charAt(0) ) ,
													channelData.substring(2));
						}						
					}
				}
				//add signaltoNoise comments
				for(Nucleotide channel : Arrays.asList(Nucleotide.Adenine, Nucleotide.Cytosine, Nucleotide.Guanine, Nucleotide.Thymine)){
					if(noiseMap.containsKey(channel)){
						int signal = signalMap.containsKey(channel) ? signalMap.get(channel) : 0;
						String value = String.format("%.2f", signal/noiseMap.get(channel).floatValue());
						String key = "SignaltoNoise"+channel;
						updatedComments.put(key, value);
					
						metaDataWriter.printf("%s.%s=%s%n", id,key,value );
					}
				}
				//convert to ZTR
				String ztrFileName = new StringBuilder(id)
											.replace(id.length()-4, id.length(), ".ztr")
											.toString();

				ChromatogramWriter ztrWriter = new ZtrChromatogramWriterBuilder(new File(outputdir, ztrFileName))
													.build();
				
				ZtrChromatogram ztr = new ZtrChromatogramBuilder(chromo)
															.comments(updatedComments)
															.build();
				ztrWriter.write(ztr);
				ztrWriter.close();
			}
		}finally{
			//if using Java 7, this can be done 
			//in a try-with-resource
			closeQuietly(metaDataWriter);
			closeQuietly(seqWriter);
			closeQuietly(qualWriter);
			closeQuietly(posWriter);
		}
	}
		
	private static void closeQuietly(Closeable c){
		try{
			c.close();
		}catch(IOException ignore){
			//ignore
		}
		
	}

}
