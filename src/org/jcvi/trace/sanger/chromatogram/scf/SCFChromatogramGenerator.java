package org.jcvi.trace.sanger.chromatogram.scf;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

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

    public static final void main(String[] args) throws Exception {
        if ( args.length != 3 ) {
            System.err.println(
                "SCFChromatogramGenerator called with invalid number of arguments\n"+
                "Proper usage is\n" +
                "\tSCFChromatogramGenerator <seqeuence fasta file> <quality fasta file> <chromatrogram output dir>"
            );
            System.exit(1);
        }

        try {
            File sequenceFasta = new File(args[0]);
            File qualityFasta = new File(args[1]);
            File outputDir = new File(args[2]);

            SCFChromatogramGenerator generator = new SCFChromatogramGenerator(new Version3SCFCodec());

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

    public SCFChromatogramGenerator(SCFCodec codec) {
        this.codec = codec;
    }

    public void createSCFChromatogram(File outputFile,
                                      String sequenceName,
                                      NucleotideEncodedGlyphs basecalls,
                                      EncodedGlyphs<PhredQuality> qualities) {

        OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(outputFile,false));
            codec.encode(new SimpleSCFChromatogram(sequenceName,basecalls,qualities) ,outputStream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create scf chromatogram file " + outputFile, e);
        } finally {
            IOUtil.closeAndIgnoreErrors(outputStream);
        }

    }

    private static class SimpleSCFChromatogram implements SCFChromatogram {

        private final short[] FALSE_WAVEFORM = { 21, 74, 202, 441, 769, 1073, 1200, 1073, 769, 441, 202, 74, 21 };

        SCFChromatogramImpl impl;

        private SimpleSCFChromatogram(String sequenceName,
                                      NucleotideEncodedGlyphs basecalls,
                                      EncodedGlyphs<PhredQuality> qualities) {
            List<NucleotideGlyph> bases = basecalls.decodeUngapped();
            if ( bases.size() != qualities.getLength() ) {
                throw new RuntimeException("Number of basecalls " + basecalls.decodeUngapped().size()
                    + " does not match number of qualities " + qualities.getLength());
            }

            // build bogus peaks
            Peaks peaks = buildPeaks(bases.size());

            // build bogus Channels/ChannelGroup
            ChannelGroup channelGroup = buildChannelGroup(bases,qualities,peaks);

            // build properties form
            Properties properties = new Properties();
            properties.put("MODL","NONE");
            properties.put("MCHN","NONE");
            if ( sequenceName != null ) {
                properties.put("NAME",sequenceName);
            }
            properties.put("COMM",
                "WARNING: THIS IS A SYNTHETIC CHROMATOGRAM CONSTRUCTED FROM STORED SEQUENCE AND QUALITY VALUES");

            impl = new SCFChromatogramImpl(
                new BasicChromatogram(basecalls,qualities,peaks,channelGroup,properties)
            );
        }

        private Peaks buildPeaks(int tracePositions) {
            short[] fakePeaks = new short[tracePositions];
            for ( int i = 0; i < tracePositions; i++ ) {
                fakePeaks[i] = (short)((FALSE_WAVEFORM.length-1)*(i+1));
            }
            return new Peaks(fakePeaks);
        }

        private ChannelGroup buildChannelGroup(List<NucleotideGlyph> bases,
                                               EncodedGlyphs<PhredQuality> qualities,
                                               Peaks peaks) {
            // build bogus Channels/ChannelGroup

            // build bogus confidence arrays
            byte[] aConfidence = new byte[bases.size()];
            byte[] cConfidence = new byte[bases.size()];
            byte[] gConfidence = new byte[bases.size()];
            byte[] tConfidence = new byte[bases.size()];

            // build bogus signal waveforms
            short[] aSignal = new short[(FALSE_WAVEFORM.length-1)*(bases.size()+1)];
            short[] cSignal = new short[(FALSE_WAVEFORM.length-1)*(bases.size()+1)];
            short[] tSignal = new short[(FALSE_WAVEFORM.length-1)*(bases.size()+1)];
            short[] gSignal = new short[(FALSE_WAVEFORM.length-1)*(bases.size()+1)];

            short[] peakLocations = ShortGlyph.toArray(peaks.getData().decode());
            for ( int i = 0; i < bases.size(); i++ ) {
                short peakLocation = peakLocations[i];
                NucleotideGlyph glyph = bases.get(i);
                switch (glyph) {
                    case Adenine:
                        // set confidence to base confidence value
                        aConfidence[i] = qualities.get(i).getNumber();
                        cConfidence[i] = 0;
                        gConfidence[i] = 0;
                        tConfidence[i] = 0;

                        // add base waveform for base call
                        buildWaveform(aSignal,peakLocation);

                        break;
                    case Cytosine:
                        // set confidence to base confidence value
                        aConfidence[i] = 0;
                        cConfidence[i] = qualities.get(i).getNumber();
                        gConfidence[i] = 0;
                        tConfidence[i] = 0;

                        // add base waveform for base call
                        buildWaveform(cSignal,peakLocation);

                        break;
                    case Guanine:
                        // set confidence to base confidence value
                        aConfidence[i] = 0;
                        cConfidence[i] = 0;
                        gConfidence[i] = qualities.get(i).getNumber();
                        tConfidence[i] = 0;

                        // add base waveform for base call
                        buildWaveform(gSignal,peakLocation);

                        break;
                    case Thymine:
                        // set confidence to base confidence value
                        aConfidence[i] = 0;
                        cConfidence[i] = 0;
                        gConfidence[i] = 0;
                        tConfidence[i] = qualities.get(i).getNumber();

                        // add base waveform for base call
                        buildWaveform(tSignal,peakLocation);

                        break;
                    default:
                        aConfidence[i] = 0;
                        cConfidence[i] = 0;
                        gConfidence[i] = 0;
                        tConfidence[i] = 0;

                }
            }

            return new DefaultChannelGroup(
                    new Channel(aConfidence,aSignal),
                    new Channel(cConfidence,cSignal),
                    new Channel(gConfidence,gSignal),
                    new Channel(tConfidence,tSignal));

        }

        private void buildWaveform(short[] signal, short peakLocation) {
            int waveStart = peakLocation - (FALSE_WAVEFORM.length-1)/2;
            for ( int i = 0; i < FALSE_WAVEFORM.length; i++ ) {
                signal[waveStart+i] += FALSE_WAVEFORM[i];
            }
        }

        @Override
        public PrivateData getPrivateData() {
            return impl.getPrivateData();  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Confidence getSubstitutionConfidence() {
            return impl.getSubstitutionConfidence();  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Confidence getInsertionConfidence() {
            return impl.getInsertionConfidence();  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Confidence getDeletionConfidence() {
            return impl.getDeletionConfidence();  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public ChannelGroup getChannelGroup() {
            return impl.getChannelGroup();  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Properties getProperties() {
            return impl.getProperties();  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Peaks getPeaks() {
            return impl.getPeaks();  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public int getNumberOfTracePositions() {
            return impl.getNumberOfTracePositions();  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public NucleotideEncodedGlyphs getBasecalls() {
            return impl.getBasecalls();  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public EncodedGlyphs<PhredQuality> getQualities() {
            return impl.getQualities();  //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
