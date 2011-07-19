package org.jcvi.primerDesign.results;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.jcvi.common.core.Range;
import org.jcvi.io.IOUtil;
import org.jcvi.primerDesign.domain.DefaultPrimerDesignTarget;
import org.jcvi.primerDesign.domain.PrimerDesignTarget;

import java.io.File;
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

    public static Map<PrimerDesignTarget,Collection<PrimerDesignResult>> retrievePrimerDesignResults(File rootDirectory) {
        Map<PrimerDesignTarget,Collection<PrimerDesignResult>> results =
            new Hashtable<PrimerDesignTarget,Collection<PrimerDesignResult>>();

        Collection files = FileUtils.listFiles(rootDirectory,
                                               new NameFileFilter("targets.simple"),
                                               DirectoryFileFilter.DIRECTORY);
        for ( Object o : files ) {
            File targetFile = (File) o;
            PrimerDesignTarget target =
                new DefaultPrimerDesignTarget(getParentId(targetFile),getTargetRange(targetFile));
            results.put(target,getPrimerDesigns(targetFile));
        }

        return results;
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
