/*******************************************************************************
 * Copyright 2010 J. Craig Venter Institute
 * 
 *  This file is part of JCVI Java Common
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
package org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1.tag.ByteArrayTaggedDataRecord;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1.tag.DateTaggedDataRecord;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1.tag.FloatArrayTaggedDataRecord;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1.tag.IntArrayTaggedDataRecord;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1.tag.ShortArrayTaggedDataRecord;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1.tag.StringTaggedDataRecord;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1.tag.TimeTaggedDataRecord;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1.tag.UserDefinedTaggedDataRecord;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1.tag.rate.ScanRate;
import org.jcvi.common.core.seq.read.trace.sanger.chromat.ab1.tag.rate.ScanRateTaggedDataType;
import org.jcvi.common.core.symbol.residue.nuc.NucleotideGlyph;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class AbiChromatogramFilePrinter implements AbiChromatogramFileVisitor{

	private final PrintStream out;
	
	AbiChromatogramFilePrinter(){
		this(System.out);
	}
	
	public AbiChromatogramFilePrinter(PrintStream out) {
		super();
		this.out = out;
	}

	@Override
	public void visitChannelOrder(List<NucleotideGlyph> order) {
		out.printf("channel order = %s%n",order);
		
	}

	@Override
	public void visitOriginalBasecalls(String originalBasecalls) {
		out.printf("original basecalls = %s%n",originalBasecalls);
		
	}

	@Override
	public void visitAConfidence(byte[] confidence) {
		out.println("visited A confidence " + confidence.length);
		out.println(Arrays.toString(confidence));
		
	}

	@Override
	public void visitAPositions(short[] positions) {
		out.println("visited A pos " + positions.length);
		out.println(Arrays.toString(positions));
		
	}

	@Override
	public void visitBasecalls(String basecalls) {
		out.printf("current basecalls = %s%n",basecalls);
		
	}

	@Override
	public void visitCConfidence(byte[] confidence) {
		out.println("visited C confidence " + confidence.length);
		out.println(Arrays.toString(confidence));
		
	}

	@Override
	public void visitCPositions(short[] positions) {
		out.println("visited C pos " + positions.length);
		out.println(Arrays.toString(positions));
		
	}

	@Override
	public void visitComments(Map<String,String> comments) {
	    System.out.println("generated comments");
		out.println(comments);
		
	}

	@Override
	public void visitGConfidence(byte[] confidence) {
		out.println("visited G confidence " + confidence.length); 
		out.println(Arrays.toString(confidence));
		
	}

	@Override
	public void visitGPositions(short[] positions) {
		out.println("visited G pos " + positions.length);
		out.println(Arrays.toString(positions));
		
	}

	@Override
	public void visitPeaks(short[] peaks) {
		out.println("visited peaks " + peaks.length);
		out.println(Arrays.toString(peaks));
		
	}

	@Override
	public void visitTConfidence(byte[] confidence) {
		out.println("visited T confidence " + confidence.length);
		out.println(Arrays.toString(confidence));
		
		
	}

	@Override
	public void visitTPositions(short[] positions) {
		out.println("visited T pos " + positions.length);
		out.println(Arrays.toString(positions));
		
	}

	@Override
	public void visitEndOfFile() {
		out.println("end parsing");
		
	}

	@Override
	public void visitFile() {
		out.println("starting parsing");
		
	}


	@Override
	public void visitElectrophoreticPower(short[] electrophoreticPowerData) {
		out.println("visited elctroPower" + "  length ="+ electrophoreticPowerData.length);
		out.println(Arrays.toString(electrophoreticPowerData));
	}

	@Override
	public void visitGelCurrentData(short[] gelCurrent) {
		out.println("visited gelCurrent" + "  length ="+ gelCurrent.length);
		out.println(Arrays.toString(gelCurrent));
		
	}

	@Override
	public void visitGelTemperatureData(short[] gelTemp) {
		out.println("visited gelTemp" + "  length ="+ gelTemp.length);
		out.println(Arrays.toString(gelTemp));
		
	}

	@Override
	public void visitGelVoltageData(short[] gelVoltage) {
		out.println("visited gelVoltage" + "  length ="+ gelVoltage.length);
		out.println(Arrays.toString(gelVoltage));
		
	}

	@Override
	public void visitPhotometricData(short[] rawTraceData, int opticalFilterId) {
		out.println("visited photometric data for optical #" + opticalFilterId + "  length ="+ rawTraceData.length);
		out.println(Arrays.toString(rawTraceData));
	}

	@Override
	public void visitOriginalPeaks(short[] originalPeaks) {
		out.println("visited ORIGINAL peaks " + originalPeaks.length);
		out.println(Arrays.toString(originalPeaks));
	}

	@Override
	public void visitOriginalAConfidence(byte[] originalConfidence) {
		out.println("visited ORIGINAL A confidence " + originalConfidence.length);
		out.println(Arrays.toString(originalConfidence));
		
	}

	@Override
	public void visitOriginalCConfidence(byte[] originalConfidence) {
		out.println("visited ORIGINAL C confidence " + originalConfidence.length);
		out.println(Arrays.toString(originalConfidence));
		
	}

	@Override
	public void visitOriginalGConfidence(byte[] originalConfidence) {
		out.println("visited ORIGINAL G confidence " + originalConfidence.length);
		out.println(Arrays.toString(originalConfidence));
		
	}

	@Override
	public void visitOriginalTConfidence(byte[] originalConfidence) {
		out.println("visited ORIGINAL T confidence " + originalConfidence.length);
		out.println(Arrays.toString(originalConfidence));
		
	}

	@Override
	public void visitScaleFactors(short aScale, short cScale, short gScale,
			short tScale) {
		out.printf("visiting scale factor A:%d C:%d G:%d T:%d%n", aScale, cScale,gScale,tScale);
		
	}

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitTaggedDataRecord(
            ByteArrayTaggedDataRecord record, byte[] data) {
        out.println("byte array Record = "+ record);
        out.println(Arrays.toString(data));
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitTaggedDataRecord(
            ShortArrayTaggedDataRecord record, short[] data) {
        out.println("short array Record = "+ record);
        out.println(Arrays.toString(data));
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitTaggedDataRecord(IntArrayTaggedDataRecord record,
            int[] data) {
        out.println("int array Record = "+ record);
        out.println(Arrays.toString(data));
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitTaggedDataRecord(
            FloatArrayTaggedDataRecord record, float[] data) {
        out.println("float array Record = "+ record);
        out.println(Arrays.toString(data));
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitTaggedDataRecord(StringTaggedDataRecord record,
            String data) {
        out.println("String Record = "+ record);
        out.println(data);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitTaggedDataRecord(TimeTaggedDataRecord record,
            LocalTime time) {
        out.println("Time Record = "+ record);
        out.println(time);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitTaggedDataRecord(DateTaggedDataRecord record, LocalDate date) {
        out.println("Date Record = "+ record);
        out.println(date);
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitTaggedDataRecord(UserDefinedTaggedDataRecord record,
            byte[] data) {
        out.println("user defined Record = "+ record);
        out.println(Arrays.toString(data));
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitTaggedDataRecord(ScanRateTaggedDataType record,
            ScanRate scanRate) {
        out.println("scan rate Record = "+ record);
        out.println(scanRate);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitNewTrace() {
        out.println("visiting new trace");
        
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void visitEndOfTrace() {
        out.println("end of trace");
        
    }

}
