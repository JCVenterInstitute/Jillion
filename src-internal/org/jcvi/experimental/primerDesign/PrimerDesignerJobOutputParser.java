package org.jcvi.experimental.primerDesign;

import org.jcvi.common.core.io.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.UnflaggedOption;
import com.martiansoftware.jsap.JSAPException;
/**
 * User: aresnick
 * Date: Jul 26, 2010
 * Time: 2:21:00 PM
 * <p/>
 * $HeadURL$
 * $LastChangedRevision$
 * $LastChangedBy$
 * $LastChangedDate$
 * <p/>
 * Description:
 */
public class PrimerDesignerJobOutputParser {

    private static final String PROJECT_OPTION_NAME = "project";
    private static final String RESOURCE_OPTION_NAME = "resource";
    private static final String OUTPUT_DIR_OPTION_NAME = "outputDir";
    private static final String ERROR_DIR_OPTION_NAME = "errorDir";
    private static final String OTHER_ARGUMENTS_OPTION_NAME = "otherArgs";

    private static final String PRIMER_DESIGNER_SCRIPT_NAME = "runClsrPD.csh";

    private static final Pattern PRIMER_DESIGNER_GRID_JOB_PATTERN =
        Pattern.compile("qsub.*");
    private static final Pattern RESOURCE_ARCHITECTURE_PATTERN =
        Pattern.compile(".*arch='(.*)'.*");

    public static void parseGridJobsFile(File file, PrimerDesignerJobOutputVisitor visitor) throws Exception {
        InputStream inStream = null;
        try {
            inStream = new FileInputStream(file);
            parseGridJobsFile(inStream, visitor);
        } finally {
            IOUtil.closeAndIgnoreErrors(inStream);
        }
    }

    public static void parseGridJobsFile(InputStream inputStream, PrimerDesignerJobOutputVisitor visitor) throws Exception{
        visitor.visitFile();

        JSAP jsap = getCommandLineParser();

        Scanner scanner = new Scanner(inputStream);
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            visitor.visitLine(line);

            Matcher matcher = PRIMER_DESIGNER_GRID_JOB_PATTERN.matcher(line);
            if ( matcher.matches() ) {
                JSAPResult result = jsap.parse(line);

                String projectCode = result.getString(PROJECT_OPTION_NAME);
                String architecture = getArchitecture(result.getString(RESOURCE_OPTION_NAME));
                File outputDir = new File(result.getString(OUTPUT_DIR_OPTION_NAME));
                File errorDir = new File(result.getString(ERROR_DIR_OPTION_NAME));
                File primerDesignerScript = new File(getPrimerDesignerScript(result.getStringArray(OTHER_ARGUMENTS_OPTION_NAME)));

                visitor.visitGridJob(projectCode,architecture,outputDir,errorDir,primerDesignerScript);
            }
        }

        visitor.visitEndOfFile();
    }

    private static JSAP getCommandLineParser() throws JSAPException {
        JSAP jsap = new JSAP();

        FlaggedOption projectOption =
            new FlaggedOption(PROJECT_OPTION_NAME)
                .setRequired(false)
                .setShortFlag('P')
                .setLongFlag(JSAP.NO_LONGFLAG);

        FlaggedOption resourceOption =
            new FlaggedOption(RESOURCE_OPTION_NAME)
                .setRequired(false)
                .setShortFlag('l')
                .setLongFlag(JSAP.NO_LONGFLAG);

        FlaggedOption outputDirOption =
            new FlaggedOption(OUTPUT_DIR_OPTION_NAME)
                .setRequired(true)
                .setShortFlag('o')
                .setLongFlag(JSAP.NO_LONGFLAG);

        FlaggedOption errorDirOption =
            new FlaggedOption(ERROR_DIR_OPTION_NAME)
                .setRequired(true)
                .setShortFlag('e')
                .setLongFlag(JSAP.NO_LONGFLAG);

        UnflaggedOption otherArguments =
            new UnflaggedOption(OTHER_ARGUMENTS_OPTION_NAME)
                .setRequired(true)
                .setGreedy(true);

        jsap.registerParameter(projectOption);
        jsap.registerParameter(resourceOption);
        jsap.registerParameter(outputDirOption);
        jsap.registerParameter(errorDirOption);
        jsap.registerParameter(otherArguments);

        return jsap;
    }

    private static String getArchitecture(String resourceString) {
        String architecture = null;
        if ( resourceString != null ) {
            Matcher matcher = RESOURCE_ARCHITECTURE_PATTERN.matcher(resourceString);
            if ( matcher.matches() ) {
                architecture = matcher.group(1);
            }
        }
        return architecture;
    }

    private static String getPrimerDesignerScript(String[] otherArguments) {
        String primerDesignerScript = null;
        for ( String arg : otherArguments ) {
            if ( arg.endsWith(PRIMER_DESIGNER_SCRIPT_NAME) ) {
                primerDesignerScript = arg;
                break;
            }
        }
        return primerDesignerScript;
    }
}
