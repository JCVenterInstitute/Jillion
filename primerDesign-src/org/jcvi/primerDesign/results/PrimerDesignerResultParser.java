package org.jcvi.primerDesign.results;

import org.jcvi.common.core.seq.fastx.fasta.FastaParser;
import org.jcvi.common.core.seq.fastx.fasta.nuc.DefaultNucleotideFastaFileDataStore;
import org.jcvi.common.core.seq.fastx.fasta.nuc.NucleotideSequenceFastaRecord;
import org.jcvi.common.core.seq.read.SequenceDirection;
import org.jcvi.Range;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;

/**
 * User: aresnick
 * Date: Jul 27, 2010
 * Time: 11:18:49 AM
 * <p/>
 * $HeadURL$
 * $LastChangedRevision$
 * $LastChangedBy$
 * $LastChangedDate$
 * <p/>
 * Description:
 */
public class PrimerDesignerResultParser {

    private static final Pattern PRIMER_PAIR_ID_PATTERN = Pattern.compile("(.+)\\.[lr]");

    public static List<PrimerDesignResult> parseResultsFile(File file) {
        // try to find primer parent sequence id based on the ids relative location
        // from the primer results file's file name (i.e. the sequence id is the name
        // of the primer result file's grandparent directory)
        String parentId;
        try {
            parentId = file.getParentFile().getParentFile().getName();
        } catch (Exception e) {
            throw new PrimerDesignerResultParseException(
                "Can't determine results' parent sequence id from input filename " + file, e
            );
        }

        return parseResultsFile(file,parentId);
    }

    static List<PrimerDesignResult> parseResultsFile(File file, String parentID) {
        try {
            List<PrimerDesignResult> results = new ArrayList<PrimerDesignResult>();

            DefaultNucleotideFastaFileDataStore dataStore = new DefaultNucleotideFastaFileDataStore();
            FastaParser.parseFasta(file,dataStore);
            for ( NucleotideSequenceFastaRecord primerFasta : dataStore ) {

                PrimerDesignResult.Builder builder = new PrimerDesignResult.Builder(file);

                builder.setParentID(parentID);
                builder.setPrimerSequence(primerFasta.getValue());

                String primerIdString = primerFasta.getId();
                Matcher idMatcher = PRIMER_PAIR_ID_PATTERN.matcher(primerIdString);
                if ( idMatcher.matches() ) {
                    builder.setDesignGroupID(idMatcher.group(1));
                } else {
                    builder.setDesignGroupID(primerIdString);
                }

                Map<String,String> elements = parseComments(primerFasta.getComment());

                SequenceDirection orientation =
                    "-1".equals(elements.get("orientation")) ? SequenceDirection.REVERSE : SequenceDirection.FORWARD;
                builder.setOrientation(orientation);

                builder.setRange(Range.parseRange(elements.get("begin")+","+elements.get("end")));

                results.add(builder.build());
            }

            return results;
        } catch (Exception e) {
            throw new PrimerDesignerResultParseException(
                "Unable to parse primer designer results in input file " + file, e
            );
        }
    }

    private static Map<String,String> parseComments(String comments) {
        Map<String,String> elements = new HashMap<String,String>();
        String[] nameValuePairs = comments.split("\\s*/");
        for ( String nameValuePair : nameValuePairs ) {
            String[] temp = nameValuePair.split("=");
            if ( temp.length == 2 ) {
                elements.put(temp[0],temp[1]);
            }
        }
        return elements;
    }
}
