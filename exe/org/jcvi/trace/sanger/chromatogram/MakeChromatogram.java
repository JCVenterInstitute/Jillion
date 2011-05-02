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

package org.jcvi.trace.sanger.chromatogram;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.command.CommandLineOptionBuilder;
import org.jcvi.command.CommandLineUtils;
import org.jcvi.datastore.DataStoreException;
import org.jcvi.fastX.fasta.qual.DefaultQualityFastaFileDataStore;
import org.jcvi.fastX.fasta.qual.QualityFastaDataStore;
import org.jcvi.fastX.fasta.seq.LargeNucleotideFastaFileDataStore;
import org.jcvi.fastX.fasta.seq.NucleotideFastaDataStore;
import org.jcvi.fastX.fasta.seq.NucleotideSequenceFastaRecord;
import org.jcvi.glyph.encoder.RunLengthEncodedGlyphCodec;
import org.jcvi.glyph.nuc.NucleotideEncodedGlyphs;
import org.jcvi.glyph.phredQuality.DefaultQualityEncodedGlyphs;
import org.jcvi.glyph.phredQuality.PhredQuality;
import org.jcvi.glyph.phredQuality.QualityEncodedGlyphs;
import org.jcvi.io.IOUtil;
import org.jcvi.sequence.Peaks;
import org.jcvi.trace.sanger.chromatogram.scf.SCFCodecs;
import org.jcvi.trace.sanger.chromatogram.ztr.IOLibLikeZTRChromatogramWriter;

/**
 * @author dkatzel
 *
 *
 */
public class MakeChromatogram {

    /**
     * @param args
     * @throws DataStoreException 
     * @throws IOException 
     */
    public static void main(String[] args) throws DataStoreException, IOException {
        Options options = new Options();
        options.addOption(
            new CommandLineOptionBuilder(
                    "s",
                    "multi-fasta file",
                    "seq fasta file")
                    .isRequired(true)
                    .build()
        );
        OptionGroup qualityGroup = new OptionGroup();
            qualityGroup.addOption(
                new CommandLineOptionBuilder(
                        "q",
                        "multi-fasta file",
                        "quality fasta file")
                        .build()
            );
            qualityGroup.addOption(
                    new CommandLineOptionBuilder(
                            "default_quality",
                            "default quality value for every basecall " +
                            "required if not valid .quality file")
                            .build()
                );
            qualityGroup.setRequired(true);
        options.addOptionGroup(qualityGroup);
        options.addOption(
            new CommandLineOptionBuilder(
                    "-o",
                    "directory name",
                    "chromatogram file output directory (default is current working directory)")
                    .isRequired(false)
                    .build()
        );
        options.addOption(
                new CommandLineOptionBuilder(
                        "ztr",
                        "write out ztr records (default is SCF)" )
                    .isFlag(true)
                        .build()
            );

       options.addOption(CommandLineUtils.createHelpOption());
       
       if(CommandLineUtils.helpRequested(args)){
           printHelp(options);
           System.exit(0);
       }
       try {
        CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
        File outputDir =commandLine.hasOption("o")?
            new File(commandLine.getOptionValue("o")):
                new File(".");
        String suffix = "scf";
        ChromatogramWriter writer = commandLine.hasOption("ztr")?
                        IOLibLikeZTRChromatogramWriter.INSTANCE:
                            SCFCodecs.VERSION_3;
        if(commandLine.hasOption("ztr")){
            suffix = "ztr";
        }
        NucleotideFastaDataStore seqFasta = new LargeNucleotideFastaFileDataStore(new File(commandLine.getOptionValue("s")));
        QualityFastaDataStore qualDataStore = null;
        if(commandLine.hasOption("q")){
            qualDataStore = new DefaultQualityFastaFileDataStore(new File(commandLine.getOptionValue("q")));
        }
        PhredQuality defaultQuality = commandLine.hasOption("default_quality")?
            PhredQuality.valueOf(Integer.parseInt(commandLine.getOptionValue("default_quality"))):
                null;
        for(NucleotideSequenceFastaRecord fasta : seqFasta){
            String id = fasta.getId();
            NucleotideEncodedGlyphs basecalls = fasta.getValue();
            final QualityEncodedGlyphs qualities;
            if(qualDataStore !=null){
               qualities = qualDataStore.get(id).getValue();
            }else{
                byte[] buf = new byte[(int)basecalls.getLength()];
                Arrays.fill(buf, defaultQuality.getNumber());
                qualities = new DefaultQualityEncodedGlyphs(
                         RunLengthEncodedGlyphCodec.DEFAULT_INSTANCE, PhredQuality.valueOf(buf));
            }
            Chromatogram chromo = buildSyntheticChromatogram(id, basecalls, qualities);
            FileOutputStream out = new FileOutputStream(new File(outputDir, id +"."+suffix));
            
            writer.write(chromo, out);
            IOUtil.closeAndIgnoreErrors(out);
        }
        
    } catch (ParseException e) {
        e.printStackTrace();
        printHelp(options);
        System.exit(0);
    }

    }
    
    private static Chromatogram buildSyntheticChromatogram(String sequenceName,
            NucleotideEncodedGlyphs basecalls,
            QualityEncodedGlyphs qualities) {
            Peaks fakePeaks = ChromatogramUtil.buildFakePeaks((int)basecalls.getLength());
            ChannelGroup fakeChannelGroup =
            new ChromatogramUtil.FakeChannelGroupBuilder(basecalls,qualities, fakePeaks).build();
            
            // build properties form
            Map<String, String> properties = new HashMap<String, String>();
            properties.put("MODL","NONE");
            properties.put("MCHN","NONE");
            properties.put("NAME",sequenceName);
            
            properties.put("COMM",
            "WARNING: THIS IS A SYNTHETIC CHROMATOGRAM CONSTRUCTED FROM STORED SEQUENCE AND QUALITY VALUES");
            
            return new BasicChromatogram(basecalls,qualities,fakePeaks,fakeChannelGroup,properties);
}
    
    
    
    public static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(
            "makeChromatograms -s <fasta file> [OPTIONS]",
            "create fake chromatogram files based on sequence fasta" +
            " and optional quality fasta data",
            options,
            "Created by Danny Katzel");
    }

}
