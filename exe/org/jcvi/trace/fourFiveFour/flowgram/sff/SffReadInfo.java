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
/*
 * Created on Nov 4, 2009
 *
 * @author dkatzel
 */
package org.jcvi.trace.fourFiveFour.flowgram.sff;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jcvi.common.command.CommandLineOptionBuilder;
import org.jcvi.common.command.CommandLineUtils;
import org.jcvi.common.core.Range;
import org.jcvi.common.core.Range.CoordinateSystem;
import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.read.trace.pyro.sff.AbstractSffFileProcessor;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SffDecoderException;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SffReadHeader;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SffFileVisitor;
import org.jcvi.common.core.seq.read.trace.pyro.sff.SffFileParser;

public class SffReadInfo extends AbstractSffFileProcessor {

    private final PrintStream out;

    /**
     * @param out
     */
    public SffReadInfo(PrintStream out, SffFileVisitor parent) {
        super(parent);
        this.out = out;
    }
    public SffReadInfo(SffFileVisitor parent) {
        this(System.out,parent);
    }
    public SffReadInfo(){
        this(System.out,null);
    }
    @Override
    public void visitFile() {        
        out.println("name\t#bases\tclip_qual_left\tclip_qual_right\tclip_adapter_left\tclip_adapter_right\tUseable_length\t#bases_trimmed\t%_trimmed");
        super.visitFile();
    }

    @Override
    public boolean visitReadHeader(SffReadHeader readHeader) {
        final Range qualityClip = readHeader.getQualityClip();
        final Range adapterClip = readHeader.getAdapterClip();
        Range trimmedRange;
        if(adapterClip.equals(Range.create(CoordinateSystem.RESIDUE_BASED,0,0))){
            trimmedRange= qualityClip;
        }
        else{
            trimmedRange = qualityClip.intersection(adapterClip);
        }
        final long basesTrimmed = readHeader.getNumberOfBases()-trimmedRange.getLength();
        out.println(String.format(
                "%s\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%.2f%%",
                readHeader.getId(),
                readHeader.getNumberOfBases(),
                qualityClip.getBegin(CoordinateSystem.RESIDUE_BASED),
                qualityClip.getEnd(CoordinateSystem.RESIDUE_BASED),
                adapterClip.getBegin(CoordinateSystem.RESIDUE_BASED),
                adapterClip.getEnd(CoordinateSystem.RESIDUE_BASED),
                trimmedRange.getLength(),
                basesTrimmed,
                basesTrimmed/(double)readHeader.getNumberOfBases()*100
                ));
        return super.visitReadHeader(readHeader);
    }
    
    public static void main(String args[]) throws FileNotFoundException, SffDecoderException{
        Options options = new Options();
        options.addOption(new CommandLineOptionBuilder("sff", "sff file")
                        .isRequired(true)
                        .build());
        
        options.addOption(new CommandLineOptionBuilder("output", "output file (defaults to STDOUT)")
                        .build());
        InputStream in=null;
        FileOutputStream fileOut=null;
        try {
            CommandLine commandLine = CommandLineUtils.parseCommandLine(options, args);
            final SffReadInfo info;
            if(commandLine.hasOption("output")){
                fileOut = new FileOutputStream(commandLine.getOptionValue("output"));
                info = new SffReadInfo(new PrintStream(fileOut,true),null);
            }else{
                info = new SffReadInfo();
            }
            in = new FileInputStream(commandLine.getOptionValue("sff"));
            SffFileParser.parseSFF(in, info);
            
            
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "sffReadInfo -sff [-output]", options );
            System.exit(1);
        }
        finally{
            IOUtil.closeAndIgnoreErrors(in);
            IOUtil.closeAndIgnoreErrors(fileOut);
        }
    }
}
