/*******************************************************************************
 * Jillion development code
 * 
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 *  see:
 * 
 *          http://www.gnu.org/copyleft/lesser.html
 * 
 * 
 * Copyright for this code is held jointly by the individual authors.  These should be listed in the @author doc comments.
 * 
 * Information about Jillion can be found on its homepage
 * 
 *         http://jillion.sourceforge.net
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.align.exonerate;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.jcvi.jillion.align.exonerate.vulgar.VulgarElement;
import org.jcvi.jillion.align.exonerate.vulgar.VulgarOperation;
import org.jcvi.jillion.align.exonerate.vulgar.VulgarProtein2Genome2;
import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.io.InputStreamSupplier;
import org.jcvi.jillion.core.io.PushBackBufferedReader;
import org.jcvi.jillion.core.residue.Frame;

public class Exonerate2 {

    public static List<VulgarProtein2Genome2> parseVulgarOutput(File vulgarOutput) throws IOException{
        return parseVulgarOutput(InputStreamSupplier.forFile(vulgarOutput));
    }
    
    private static String BLOCK_START = "C4 Alignment:";
    
    public static List<VulgarProtein2Genome2> parseVulgarOutput(InputStreamSupplier vulgarOutput) throws IOException{
        List<VulgarProtein2Genome2> list = new ArrayList<>();
        try(PushBackBufferedReader reader = new PushBackBufferedReader(new BufferedReader(new InputStreamReader(vulgarOutput.get())))){
            
            String currentBlock = readNextBlock(reader);
            while(currentBlock !=null){

                VulgarProtein2Genome2 v = handleBlock(currentBlock);
                if(v!=null){
                    list.add(v);
                }
                currentBlock = readNextBlock(reader);
            }
        }
        return list;
    }
    
    private static VulgarProtein2Genome2 handleBlock(String block) throws IOException{
        Pattern splitPattern = Pattern.compile("\\s+");
        try(BufferedReader reader = new BufferedReader(new StringReader(block))){
            String line;
            while( (line = reader.readLine()) !=null){
                if(line.startsWith("vulgar:")){
                    String[] fields = splitPattern.split(line);
                    //fields[0] is vulgar:
                    /*
                     * 

    query_id
        Query identifier
    query_start
        Query position at alignment start
    query_end
        Query position alignment end
    query_strand
        Strand of query matched
    target_id
        |
    target_start
        | the same 4 fields
    target_end
        | for the target sequence
    target_strand
        |
    score
        The raw alignment score


                     */
                    String queryId = fields[1];
                    DirectedRange queryRange = DirectedRange.parse(Long.parseLong(fields[2]), Long.parseLong(fields[3]),
                            CoordinateSystem.SPACE_BASED);
                    String queryStrand = fields[4];
                    
                    String targetId = fields[5];
                    DirectedRange targetRange = DirectedRange.parse(Long.parseLong(fields[6]), Long.parseLong(fields[7]),
                            CoordinateSystem.SPACE_BASED);
                    String targetStrand = fields[8];
                    
                    float score = Float.parseFloat(fields[9]);
                    
                    List<VulgarElement> vulgarElements = new ArrayList<>((fields.length -10)/3);
                    for(int i= 10; i < fields.length; i+=3){
                        vulgarElements.add(new VulgarElement(VulgarOperation.getByCode(fields[i]),
                                Integer.parseInt(fields[i+1]), 
                                Integer.parseInt(fields[i+2])));
                    }
                    

                    return new VulgarProtein2Genome2(queryId, targetId, vulgarElements, score,
                            queryStrand, queryRange, targetStrand, targetRange);
                }
            }
        }
        return null;
    }

    private static String readNextBlock(PushBackBufferedReader reader) throws IOException {
        String line;
        while( (line = reader.readLine()) !=null){
            if(BLOCK_START.equals(line)){
                //found beginning
                break;
            }
        }
        if(line ==null){
            return null;
        }
        
        
        StringBuilder builder = new StringBuilder(5000);
        builder.append(line).append('\n');
        while( (line = reader.readLine()) !=null){
            if(BLOCK_START.equals(line)){
                //found end
                reader.pushBack(line);
                break;
            }else{
                builder.append(line).append('\n');
            }
        }
        return builder.toString();
    }
    
   
}
