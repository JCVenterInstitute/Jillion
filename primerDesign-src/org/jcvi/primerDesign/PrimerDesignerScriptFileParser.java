package org.jcvi.primerDesign;

import com.martiansoftware.jsap.*;
import com.martiansoftware.jsap.stringparsers.FileStringParser;

import org.jcvi.common.core.io.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class PrimerDesignerScriptFileParser {

    private static final String COMMAND_FILENAME = "command";
    private static final String CONFIG_FILENAME = "config";
    private static final String GFF_FILENAME = "gff";
    private static final String ORIG_FILENAME = "orig";
    private static final String TARGET_FILENAME = "target";
    private static final String PRIMER_FILENAME = "fasta";

    private static final Pattern PRIMER_DESIGNER_COMMAND_PATTERN =
        Pattern.compile(".+PrimerDesigner\\.pl\\s+.+");
    private static final Pattern GFF_CONVERSION_COMMAND_PATTERN =
        Pattern.compile(".+GFF_to_PS\\.pl.+");
    private static final Pattern PS_RENAME_COMMAND_PATTERN =
        Pattern.compile("\\s*mv\\s+.+");
    private static final Pattern PRIMER_CRITIQUOR_COMMAND_PATTERN =
        Pattern.compile(".+PrimerCritiquor\\.pl\\s+.+");

    public static void parsePrimerDesignerScriptFile(File file,
                                                     PrimerDesignerScriptFileVisitor visitor) throws Exception {
        InputStream inStream = null;
        try {
            inStream = new FileInputStream(file);
            parsePrimerDesignerScriptFile(inStream, visitor);
        } finally {
            IOUtil.closeAndIgnoreErrors(inStream);
        }
    }

    public static void parsePrimerDesignerScriptFile(InputStream inputStream,
                                                     PrimerDesignerScriptFileVisitor visitor) throws Exception{
        visitor.visitFile();

        Scanner scanner = new Scanner(inputStream);
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            visitor.visitLine(line);

            Matcher matcher = PRIMER_DESIGNER_COMMAND_PATTERN.matcher(line);
            if ( matcher.matches() ) {
                JSAP jsap = getPrimerDesignerCommandLineParser();
                JSAPResult result = jsap.parse(line);
                visitor.visitPrimerDesignerCommand(result.getFile(COMMAND_FILENAME),
                                                   result.getFile(CONFIG_FILENAME));
                // skip to next line
                continue;
            }

            matcher = GFF_CONVERSION_COMMAND_PATTERN.matcher(line);
            if ( matcher.matches() ) {
                JSAP jsap = getGffConversionCommandLineParser();
                JSAPResult result = jsap.parse(line);
                visitor.visitPdfConversionCommand(result.getFile(COMMAND_FILENAME),
                                                  result.getFile(GFF_FILENAME));

                // skip to next line
                continue;
            }

            matcher = PS_RENAME_COMMAND_PATTERN.matcher(line);
            if ( matcher.matches() ) {
                JSAP jsap = getPSRenameCommandLineParser();
                JSAPResult result = jsap.parse(line);
                visitor.visitPdfRenameCommand(result.getFile(ORIG_FILENAME),
                                              result.getFile(TARGET_FILENAME));

                // skip to next line
                continue;
            }

            matcher = PRIMER_CRITIQUOR_COMMAND_PATTERN.matcher(line);
            if ( matcher.matches() ) {
                JSAP jsap = getPrimerCritiquorCommandLineParser();
                JSAPResult result = jsap.parse(line);
                visitor.visitPrimerCritiquorCommand(result.getFile(COMMAND_FILENAME),
                                                    result.getFile(PRIMER_FILENAME),
                                                    result.getFile(CONFIG_FILENAME));
                // skip to next line
                continue;
            }

            // doesn't fit any command patterns, so ignore
        }

        visitor.visitEndOfFile();
    }

    private static JSAP getPrimerDesignerCommandLineParser() throws JSAPException {
        JSAP jsap = new JSAP();

        jsap.registerParameter(
            new UnflaggedOption(COMMAND_FILENAME)
                .setRequired(true)
                .setStringParser(FileStringParser.getParser())
        );

        jsap.registerParameter(
            new FlaggedOption(CONFIG_FILENAME)
                .setRequired(true)
                .setShortFlag('p')
                .setLongFlag(JSAP.NO_LONGFLAG)
                .setStringParser(FileStringParser.getParser())
        );


        return jsap;
    }

    private static JSAP getGffConversionCommandLineParser() throws JSAPException {
        JSAP jsap = new JSAP();

        jsap.registerParameter(
            new UnflaggedOption(COMMAND_FILENAME)
                .setRequired(true)
                .setStringParser(FileStringParser.getParser())
        );

        jsap.registerParameter(
            new UnflaggedOption(GFF_FILENAME)
                .setRequired(true)
                .setStringParser(FileStringParser.getParser())
        );

        return jsap;
    }

    private static JSAP getPSRenameCommandLineParser() throws JSAPException {
        JSAP jsap = new JSAP();

        jsap.registerParameter(
            new UnflaggedOption(COMMAND_FILENAME)
                .setRequired(true)
                .setStringParser(FileStringParser.getParser())
        );

        jsap.registerParameter(
            new UnflaggedOption(ORIG_FILENAME)
                .setRequired(true)
                .setStringParser(FileStringParser.getParser())
        );

        jsap.registerParameter(
            new UnflaggedOption(TARGET_FILENAME)
                .setRequired(true)
                .setStringParser(FileStringParser.getParser())
        );

        return jsap;
    }

    private static JSAP getPrimerCritiquorCommandLineParser() throws JSAPException {
        JSAP jsap = new JSAP();

        jsap.registerParameter(
            new UnflaggedOption(COMMAND_FILENAME)
                .setRequired(true)
                .setStringParser(FileStringParser.getParser())
        );

        jsap.registerParameter(
            new FlaggedOption(PRIMER_FILENAME)
                .setRequired(true)
                .setShortFlag('f')
                .setLongFlag(JSAP.NO_LONGFLAG)
                .setStringParser(FileStringParser.getParser())
        );

        jsap.registerParameter(
            new FlaggedOption(CONFIG_FILENAME)
                .setRequired(true)
                .setShortFlag('p')
                .setLongFlag(JSAP.NO_LONGFLAG)
                .setStringParser(FileStringParser.getParser())
        );

        return jsap;
    }
}