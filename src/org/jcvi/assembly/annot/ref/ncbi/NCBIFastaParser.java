/*
 * Created on Jan 28, 2009
 *
 * @author dkatzel
 */
package org.jcvi.assembly.annot.ref.ncbi;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jcvi.fasta.DefaultEncodedNucleotideFastaRecord;
import org.jcvi.io.IOUtil;

public class NCBIFastaParser {

    private static Pattern FASTA_PATTERN = Pattern.compile("<div\\s+class='recordbody'>>(\\S+)\\s+(.*?)([A,C,G,T,-,N,M,K,B,D,H,V,R,Y,S,W]+)\\s*</div>");
    
    public static DefaultEncodedNucleotideFastaRecord parseFastaFrom(InputStream ncbiFastaPage) throws IOException{
        String response = IOUtil.readStream(ncbiFastaPage);
        Matcher matcher = FASTA_PATTERN.matcher(response);
        if(matcher.find()){
            final String bases = matcher.group(3).replaceAll("\\s+", "");
            return new DefaultEncodedNucleotideFastaRecord(matcher.group(1),matcher.group(2),bases);
        }
        throw new RuntimeException("could not parse fasta data from ncbi");
    }
    
    public static void main(String[] args) throws MalformedURLException, IOException, InterruptedException{
        Thread.sleep(3000);
        System.out.println(NCBIFastaParser.parseFastaFrom(new URL("http://www.ncbi.nlm.nih.gov/entrez/viewer.fcgi?db=nuccore&id=206731571&dopt=fasta").openStream()).toString());
    }
}
