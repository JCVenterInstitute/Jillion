package org.jcvi.trace.sanger.chromatogram.scf;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.jcvi.command.CommandLineOptionBuilder;
import org.jcvi.command.CommandLineUtils;
import org.jcvi.fasta.*;
import org.jcvi.glyph.EncodedGlyphs;
import org.jcvi.glyph.nuc.NucleotideGlyph;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.num.ShortGlyph;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.io.IOUtil;
import org.jcvi.sequence.Confidence;
import org.jcvi.sequence.Peaks;
import org.jcvi.trace.sanger.chromatogram.*;
/**
 * Created by IntelliJ IDEA.
 * User: aresnick
 * Date: Sep 2, 2010
 * Time: 8:21:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class SCFChromatogramGenerator {

    private SCFCodec codec;

    private static final String SEQUENCE_FILE_OPTION_NAME = "sequenceFile";
    private static final String QUALITY_FILE_OPTION_NAME = "qualityFile";
    private static final String OUTPUT_DIRECTORY_OPTION_NAME = "outputDir";
    private static final String SCF_VERSION_OPTION_NAME = "scfVersion";

    public static final void main(String[] args) throws Exception {
        File sequenceFasta = null;
        File qualityFasta = null;
        File outputDir = new File(System.getProperty("user.dir"));
        SCFCodec codec = new Version3SCFCodec();

        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(buildOptions(),args);

            sequenceFasta = new File(commandLine.getOptionValue(SEQUENCE_FILE_OPTION_NAME));
            qualityFasta = new File(commandLine.getOptionValue(QUALITY_FILE_OPTION_NAME));

            String outDir = commandLine.getOptionValue(OUTPUT_DIRECTORY_OPTION_NAME);
            if ( outDir != null ) {
                outputDir = new File(outDir);
            }
            if ( !outputDir.exists() ) {
                if ( !outputDir.mkdirs() ) {
                    throw new RuntimeException("Can't create target output dir " + outputDir);
                }
            } else {
                if ( !(outputDir.canWrite() && outputDir.isDirectory()) ) {
                    throw new IllegalArgumentException("Target output dir " + outputDir
                        + " is not a writable directory");
                }
            }

            String versionName = commandLine.getOptionValue(SCF_VERSION_OPTION_NAME);
            if ( versionName != null ) {
                if ( "2".equals(versionName) ) {
                    codec = new Version2SCFCodec();
                } else if ( "3".equals(versionName) ) {
                    codec = new Version3SCFCodec();
                } else {
                    throw new IllegalArgumentException(versionName + " is not a valid scf version identifier");
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
            printHelp();
            System.exit(1);
        }


        try {
            SCFChromatogramGenerator generator = new SCFChromatogramGenerator(codec);

            DefaultNucleotideFastaFileDataStore sequences = new DefaultNucleotideFastaFileDataStore(sequenceFasta);
            DefaultQualityFastaFileDataStore qualities = new DefaultQualityFastaFileDataStore(qualityFasta);

            for ( Iterator<String> ids = sequences.getIds(); ids.hasNext(); ) {
                String id = ids.next();
                System.out.println("Creating sequence " + id + " scf file");
                if ( !qualities.contains(id) ) {
                    System.err.println("ERROR: Can't create scf file for sequence " + id +
                        ", can't find sequence quality values");
                } else {
                    try {
                        File scfFile = new File(outputDir,id+".scf");
                        generator.createSCFChromatogram(scfFile,
                                                        id,
                                                        sequences.get(id).getValues(),
                                                        qualities.get(id).getValues());
                    } catch (Exception e) {
                        System.err.println("ERROR: Unexpected error creating scf file for sequence " + id);
                        e.printStackTrace();
                    }
                }
            }
        } catch ( Throwable e) {
            System.err.println("Unexpected error encountered during scf file creation");
            e.printStackTrace();
            System.exit(2);
        }
    }

    private static Options buildOptions() {
        Options options = new Options();
        options.addOption(
            new CommandLineOptionBuilder(
                    SEQUENCE_FILE_OPTION_NAME,
                    "multi-fasta file",
                    "source sequence data")
                    .isRequired(true)
                    .build()
        );
        options.addOption(
            new CommandLineOptionBuilder(
                    QUALITY_FILE_OPTION_NAME,
                    "multi-fasta file",
                    "source quality data")
                    .isRequired(true)
                    .build()
        );
        options.addOption(
            new CommandLineOptionBuilder(
                    OUTPUT_DIRECTORY_OPTION_NAME,
                    "directory name",
                    "chromatogram file output directory (default is current working directory)")
                    .isRequired(false)
                    .build()
        );
        options.addOption(
            new CommandLineOptionBuilder(
                    SCF_VERSION_OPTION_NAME,
                    "2|3",
                    "scf file format version (default is version 3)")
                    .isRequired(false)
                    .build()
        );
        return options;
    }

    public static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(
            "SCFChromatogramGenerator"
                + " -" + SEQUENCE_FILE_OPTION_NAME + " <filename>"
                + " -" + QUALITY_FILE_OPTION_NAME + " <filename>"
                + " [-" + OUTPUT_DIRECTORY_OPTION_NAME + " <dirname>]"
                + " [-" + SCF_VERSION_OPTION_NAME + " <2|3>]"
            ,
            "\nWrite \"synthetic\" scf chromatograms based on input sequence and quality files",
            buildOptions(),
            "\nCreated by Adam Resnick");
    }

    public SCFChromatogramGenerator(SCFCodec codec) {
        this.codec = codec;
    }

    public void createSCFChromatogram(File outputFile,
                                      String sequenceName,
                                      NucleotideEncodedGlyphs basecalls,
                                      EncodedGlyphs<PhredQuality> qualities) {
        OutputStream outputStream = null;
        try {
            SCFChromatogram syntheticChromatogram =
               new SCFChromatogramImpl(
                   buildSyntheticChromatogram(sequenceName,basecalls,qualities)
                );
            outputStream = new BufferedOutputStream(new FileOutputStream(outputFile,false));
            codec.encode(syntheticChromatogram,outputStream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create scf chromatogram file " + outputFile, e);
        } finally {
            IOUtil.closeAndIgnoreErrors(outputStream);
        }

    }

    private Chromatogram buildSyntheticChromatogram(String sequenceName,
                                                    NucleotideEncodedGlyphs basecalls,
                                                    EncodedGlyphs<PhredQuality> qualities) {
        Peaks fakePeaks = ChromatogramUtil.buildFakePeaks((int)basecalls.getLength());
        ChannelGroup fakeChannelGroup =
            new ChromatogramUtil.FakeChannelGroupBuilder(basecalls,qualities, fakePeaks).build();

        // build properties form
        Properties properties = new Properties();
        properties.put("MODL","NONE");
        properties.put("MCHN","NONE");
        properties.put("NAME",sequenceName);

        properties.put("COMM",
            "WARNING: THIS IS A SYNTHETIC CHROMATOGRAM CONSTRUCTED FROM STORED SEQUENCE AND QUALITY VALUES");

        return new BasicChromatogram(basecalls,qualities,fakePeaks,fakeChannelGroup,properties);
    }
}
