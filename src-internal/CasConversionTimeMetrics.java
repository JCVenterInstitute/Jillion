import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.Range;
import org.jcvi.assembly.ace.AceContig;
import org.jcvi.assembly.ace.AceFileParser;
import org.jcvi.assembly.ace.AceFileVisitor;
import org.jcvi.assembly.ace.AcePlacedRead;
import org.jcvi.datastore.MemoryMappedAceFileDataStore;
import org.jcvi.io.IOUtil;
import org.jcvi.io.idReader.DefaultFileIdReader;
import org.jcvi.io.idReader.IdParser;
import org.jcvi.io.idReader.IdReader;
import org.jcvi.io.idReader.StringIdParser;
import org.jcvi.sequence.SequenceDirection;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;

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

/**
 * @author dkatzel
 *
 *
 */
public class CasConversionTimeMetrics {
 
    private static final Pattern TIME_PATTERN = Pattern.compile("^took PT((\\d+)H)?((\\d+)M)?(\\d+)\\..*");

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        File pathtoFileList = new File("/home/dkatzel/other_conversionFiles.txt");
        IdReader<String> aceFiles = new DefaultFileIdReader<String>(pathtoFileList,new StringIdParser());
        File root = new File("/usr/local/projects/VHTNGS/sample_data/giv3/");
        for(String aceFilePath : aceFiles){
            final File aceFile = new File(root,aceFilePath);
            Integer numSeconds = getNumberOfHoursToConvert(aceFile);
            if(numSeconds ==null){
                continue;
            }
            MemoryMappedAceFileDataStore aceDataStore = new MemoryMappedAceFileDataStore(aceFile);
            AceFileParser.parseAceFile(aceFile, aceDataStore);
            long consensusTotal=0;
            long numBases=0;
            long numReads=0;
            for(AceContig contig : aceDataStore){
                consensusTotal+=contig.getConsensus().getLength();
                final Set<AcePlacedRead> placedReads = contig.getPlacedReads();
                numReads+= placedReads.size();
                for(AcePlacedRead read: placedReads){
                    numBases+=read.getEncodedGlyphs().getLength();
                }
            }
            
            System.out.printf("%s,%s,%d,%d,%d%n",aceFilePath,numSeconds,numReads,consensusTotal,numBases);
            
            
        }
    }
    private static Integer getNumberOfHoursToConvert(File aceFile) throws FileNotFoundException {
        File logFile  = new File(new File(aceFile.getParent()).getAbsolutePath()+"/../cas2consed.log");           
        
        if(!logFile.exists()){
            return null;
        }
        Integer numHours = numberOfHoursToConvert(logFile);
        return numHours;
    }
    private static Integer numberOfHoursToConvert(File logFile) throws FileNotFoundException{
        Scanner scanner=null;
            try{
                scanner= new Scanner(logFile);
                //read last line
                String line=null;
                while(scanner.hasNextLine()){
                    line = scanner.nextLine();
                }
              
                Matcher matcher = TIME_PATTERN.matcher(line);
                if(!matcher.find()){
                    return null;
                }
                String hours = matcher.group(2);
                String min = matcher.group(4);
                String sec = matcher.group(5);
                int numSeconds = Integer.parseInt(sec)+
                                (min==null?0: (60*Integer.parseInt(min))) +
                                        (hours==null?0: (60*60*Integer.parseInt(hours)));
                          
                                        
               
               // int numSeconds = period.getSeconds()+(60*period.getMinutes())+ (60*60*period.getHours());
                return numSeconds;
            }
            finally{
                IOUtil.closeAndIgnoreErrors(scanner);
            }
        }

   
}
