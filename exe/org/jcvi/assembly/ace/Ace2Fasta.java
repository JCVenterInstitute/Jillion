/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 * 	This file is part of JCVI Java Common
 * 
 *     JCVI Java Common is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     JCVI Java Common is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with JCVI Java Common.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Created on Dec 3, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.ace;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.core.assembly.ace.AceContigDataStore;
import org.jcvi.common.core.assembly.ace.AceContigDataStoreBuilder;
import org.jcvi.common.core.assembly.ace.AceFileParser;
import org.jcvi.common.core.assembly.ace.AceFileVisitor;
import org.jcvi.common.core.assembly.ace.AceTags;
import org.jcvi.common.core.assembly.ace.ConsensusAceTag;
import org.jcvi.common.core.assembly.ace.DefaultAceTagsFromAceFile;
import org.jcvi.common.core.assembly.ace.DefaultAceTagsFromAceFile.AceTagsFromFileBuilder;
import org.jcvi.common.core.assembly.ace.IndexedAceFileDataStore;
import org.jcvi.common.core.assembly.ace.consed.ConsedUtil;
import org.jcvi.common.core.datastore.DataStoreException;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.fastx.ExcludeFastXIdFilter;
import org.jcvi.common.core.seq.fastx.FastXFilter;
import org.jcvi.common.core.seq.fastx.IncludeFastXIdFilter;
import org.jcvi.common.core.seq.fastx.AcceptingFastXFilter;
import org.jcvi.common.core.seq.fastx.fasta.nuc.DefaultNucleotideSequenceFastaRecord;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideSequenceBuilder;
import org.jcvi.common.core.util.MultipleWrapper;
import org.jcvi.common.io.idReader.DefaultFileIdReader;
import org.jcvi.common.io.idReader.IdReader;
import org.jcvi.common.io.idReader.IdReaderException;
import org.jcvi.common.io.idReader.StringIdParser;

public class Ace2Fasta {


    public static void main(String[] args) throws IOException, DataStoreException{
        Options options = new Options();
        
        options.addOption(new CommandLineOptionBuilder("ace", "path to ace file")
                            .isRequired(true)
                            .build());
        options.addOption(new CommandLineOptionBuilder("out", "output path to fasta file to write")
                            .isRequired(true)
                            .build());
        options.addOption(new CommandLineOptionBuilder("g", "print gapped consensus (default is ungapped)")
                            .longName("gapped")
                            .isFlag(true)
                            .build());
        options.addOption(CommandLineUtils.createHelpOption());
        
        OptionGroup group = new OptionGroup();
        
        group.addOption(new CommandLineOptionBuilder("i", "include file of ids to include")
                            .build());
        group.addOption(new CommandLineOptionBuilder("e", "exclude file of ids to exclude")
                            .build());
        options.addOptionGroup(group);
        if(CommandLineUtils.helpRequested(args)){
            printHelp(options);
            System.exit(0);
        }
        OutputStream fastaOut=null;
        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
          
            final File aceIn = new File(commandLine.getOptionValue("ace"));
    
            fastaOut = new FileOutputStream(commandLine.getOptionValue("out"));
            final boolean gapped = commandLine.hasOption("g");
            
            final File idFile;
            final FastXFilter filter;
            if(commandLine.hasOption("i")){
                idFile =new File(commandLine.getOptionValue("i"));
                Set<String> includeList=parseIdsFrom(idFile);
                if(commandLine.hasOption("e")){
                    Set<String> excludeList=parseIdsFrom(new File(commandLine.getOptionValue("e")));
                    includeList.removeAll(excludeList);
                }
                filter = new IncludeFastXIdFilter(includeList);
                
            }else if(commandLine.hasOption("e")){
                idFile =new File(commandLine.getOptionValue("e"));
                filter = new ExcludeFastXIdFilter(parseIdsFrom(idFile));
            }else{
                filter = AcceptingFastXFilter.INSTANCE;
            }
           
           
            AceAssembly aceAssembly = new AceAssembly(aceIn);
            AceContigDataStore contigDataStore = aceAssembly.getContigDataStore();
            //consed allows users to rename contigs, but instead of changing
            //the CO record in the ace, the new name is stored as a comment at
            //the end of the file.  contigIdMap parses those comments 
            //to get the most current names
            //for those the contigs, any contig without those comments
            //will not exist in the map
            final Map<String, String> contigIdMap = getContigIdMap(aceAssembly.getAceTags());
            Iterator<String> ids = contigDataStore.getIds();
            while(ids.hasNext()){
            	String contigId = ids.next();
            	if(!filter.accept(contigId)){
            		continue;
            	}
            	NucleotideSequenceBuilder consensusBuilder = new NucleotideSequenceBuilder(contigDataStore.get(contigId).getConsensus());
               
                if(!gapped){
                	consensusBuilder.ungap();
                }
                String id = contigIdMap.containsKey(contigId)?
                                    contigIdMap.get(contigId)
                                    : contigId;
               
                String comment = aceIn.getName()+" (whole contig)";
                DefaultNucleotideSequenceFastaRecord fasta = new DefaultNucleotideSequenceFastaRecord(
                                                                id,
                                                                comment,
                                                                consensusBuilder.toString());
                    fastaOut.write(fasta.toString().getBytes("UTF-8"));
                
            }
          
           
        } catch (ParseException e1) {
            printHelp(options);
            System.exit(1);
        }finally{
        	 IOUtil.closeAndIgnoreErrors(fastaOut);
        }
    }

    private static Map<String, String> getContigIdMap(AceTags aceTags)
            throws IOException {
        final Map<String, String> contigIdMap = new HashMap<String, String>();
        for(ConsensusAceTag consensusTag: aceTags.getConsensusTags()){
            String originalId = consensusTag.getId();
            if(ConsedUtil.isContigRename(consensusTag)){
                contigIdMap.put(originalId, ConsedUtil.getRenamedContigId(consensusTag));
            }
        }
        return contigIdMap;
    }
    
    private static class AceAssembly {
    	private final AceContigDataStore datastore;
    	private final AceTags aceTags;
    	
    	AceAssembly(File aceFile) throws IOException{
    		AceContigDataStoreBuilder dataStoreBuilder = IndexedAceFileDataStore.createBuilder(aceFile);
    		AceTagsFromFileBuilder tagBuilder = DefaultAceTagsFromAceFile.createBuilder();
    		
    		AceFileVisitor visitor = MultipleWrapper.createMultipleWrapper(
    				AceFileVisitor.class, dataStoreBuilder, tagBuilder);
    		
    		AceFileParser.parseAceFile(aceFile, visitor);
    		datastore = dataStoreBuilder.build();
    		aceTags = tagBuilder.build();
    		
    	}
    	
    	AceContigDataStore getContigDataStore(){
    		return datastore;
    	}
    	
    	AceTags getAceTags(){
    		return aceTags;
    	}
    	
    }
    
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "ace2Fasta -ace <ace file> -out <output fasta>", 
                
                "create a fasta file from the contig consensus(es) of the given ace file."+
                "  The Ids used in the fasta are usually the contig ids except when "+
                "a user added a consed 'contigName' comment (renamed a contig inside consed), then the rename is used",
                options,
                "Created by Danny Katzel");
    }
    
    private static Set<String> parseIdsFrom(final File idFile)   throws IdReaderException {
        IdReader<String> idReader = new DefaultFileIdReader<String>(idFile,new StringIdParser());
        Set<String> ids = new HashSet<String>();
        Iterator<String> iter =idReader.getIds();
        while(iter.hasNext()){
            ids.add(iter.next());
        }
        return ids;
    }
}
