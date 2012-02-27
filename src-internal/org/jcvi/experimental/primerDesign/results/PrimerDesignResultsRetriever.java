package org.jcvi.experimental.primerDesign.results;

import org.jcvi.common.core.Range;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.experimental.primerDesign.domain.DefaultPrimerDesignTarget;
import org.jcvi.experimental.primerDesign.domain.PrimerDesignTarget;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by IntelliJ IDEA.
 * User: aresnick
 * Date: Aug 18, 2010
 * Time: 6:27:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class PrimerDesignResultsRetriever {
	//dkatzel - changed to use standard JDK filter instead of 
	//apache commons io code to remove apache dependencies.
	
    public static Map<PrimerDesignTarget,Collection<PrimerDesignResult>> retrievePrimerDesignResults(File rootDirectory) {
        Map<PrimerDesignTarget,Collection<PrimerDesignResult>> results =
            new Hashtable<PrimerDesignTarget,Collection<PrimerDesignResult>>();
        
        Collection<File> files = getMatchingFilesRecursively(rootDirectory);
        for ( File targetFile: files ) {
            PrimerDesignTarget target =
                new DefaultPrimerDesignTarget(getParentId(targetFile),getTargetRange(targetFile));
            results.put(target,getPrimerDesigns(targetFile));
        }

        return results;
    }
    
	private static Collection<File> getMatchingFilesRecursively(File root){
		Collection<File> matchingFiles = new ArrayList<File>();
		for(File f :root.listFiles()){
			if(f.isDirectory()){
				matchingFiles.addAll(getMatchingFilesRecursively(f));
			}else{
				if(f.getName().equals("targets.simple")){
					matchingFiles.add(f);
				}
			}
			
		}
		return matchingFiles;
	}
    private static String getParentId(File targetFile) {
        try {
            return targetFile.getParentFile().getParentFile().getName();
        } catch (Exception e) {
            throw new PrimerDesignerResultParseException(
                "Can't determine results' parent target id from input filename " + targetFile, e
            );
        }
    }

    private static Range getTargetRange(File targetFile) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(targetFile);
            long start = scanner.nextLong();
            long end = scanner.nextLong();
            return Range.buildRange(start,end);
        } catch (Exception e) {
            throw new PrimerDesignerResultParseException(
                "Can't determine results' parent target range from input filename " + targetFile, e
            );
        } finally {
            IOUtil.closeAndIgnoreErrors(scanner);
        }
    }

    private static List<PrimerDesignResult> getPrimerDesigns(File targetFile) {
            File primerDesignResultsFile = new File(targetFile.getParentFile(),"primers.fasta");
            if ( primerDesignResultsFile.exists() ) {
                return PrimerDesignerResultParser.parseResultsFile(primerDesignResultsFile);
            } 
            return Collections.emptyList();
    }
}
