package org.jcvi.jillion_experimental.align.blast.btab;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jcvi.jillion.core.DirectedRange;
import org.jcvi.jillion.core.Direction;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.Range.CoordinateSystem;
import org.jcvi.jillion.core.util.JoinedStringBuilder;
import org.jcvi.jillion_experimental.align.blast.BlastHit;
import org.jcvi.jillion_experimental.align.blast.Hsp;

public class TigrBtabWriter implements BtabWriter{

	private final PrintWriter out;
	private final String formattedRunDate;
	
	private final NumberFormat scientificNotationFormatter;
	
	private TigrBtabWriter(Builder builder) throws IOException{
		if(builder.outputFile ==null){
			out = new PrintWriter(builder.outStream);
		}else{
			out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(builder.outputFile)));
		}
		//create new date formatter each time to avoid
		//thread related problems.  
		//this constructor should only be called in frequently
		//so it won't be too much of a performance hit.
		formattedRunDate = new SimpleDateFormat("MMM dd yyyy").format(builder.runDate);
		
		scientificNotationFormatter = new DecimalFormat("0.0E0");
		scientificNotationFormatter.setRoundingMode(RoundingMode.HALF_UP);
		scientificNotationFormatter.setMinimumFractionDigits(5);
	}
	@Override
	public void close() throws IOException {
		out.close();
		
	}

	@Override
	public void write(BlastHit hit) throws IOException {
			
			
			for(Hsp<?,?> hsp : hit.getHsps()){

				List<String> fields = new ArrayList<String>(21);
				fields.add(hsp.getQueryId());
				fields.add(formattedRunDate); //date? current date?
				fields.add(hsp.getQueryLength()==null?"":hsp.getQueryLength().toString());
				fields.add(hit.getBlastProgramName());
				fields.add(hit.getBlastDbName());
				fields.add(hsp.getSubjectId());
				DirectedRange queryDirectedRange = hsp.getQueryRange();
				DirectedRange subjectDirectedRange = hsp.getSubjectRange();
				
				
				appendRangeCoordinates(queryDirectedRange, fields);
				appendRangeCoordinates(subjectDirectedRange, fields);
				
				
				//double alignmentLength = hsp.getNumberOfMismatches() + hsp.getNumberOfPositiveMatches()+hsp.getNumberOfGapOpenings();
				double alignmentLength = hsp.getAlignmentLength();
				fields.add(String.format("%.2f", hsp.getNumberOfIdentitcalMatches()/alignmentLength*100D));
				
				fields.add(String.format("%.2f", hsp.getNumberOfPositiveMatches()/alignmentLength*100D));
				
				fields.add(hsp.getHspScore()==null?"0.0" : hsp.getHspScore().toString());
				
				//add extra tab separator to match vics output
				fields.add(hsp.getBitScore().toString()+"\t");
			
				//hit def?
				fields.add(hsp.getSubjectDefinition() ==null?"":hsp.getSubjectDefinition());
				//frame?
				//this will make frame 1,2,3,-1,-2,-3
				fields.add(hsp.getHitFrame() ==null?"": Integer.toString(hsp.getHitFrame()) );
				fields.add(queryDirectedRange.getDirection()==Direction.FORWARD? "Plus" : "Minus");
				fields.add(Integer.toString(hsp.getSubjectLength()));
				//add extra trailing tab to match vics output
				fields.add(scientificNotationFormatter.format(hsp.getEvalue()) + "\t");
				
				
				
				out.println(new JoinedStringBuilder(fields).glue("\t").build());
			}
		
		
	}
	/**
	 * following TIGR standards flip
	 * begin and end coords if hit is reversed
	 */	
	private void appendRangeCoordinates(DirectedRange directedRange,
			List<String> fields) {
		Range range =directedRange.getRange();
		if(directedRange.getDirection() == Direction.FORWARD){		
			fields.add(Long.toString(range.getBegin(CoordinateSystem.RESIDUE_BASED)));
			fields.add(Long.toString(range.getEnd(CoordinateSystem.RESIDUE_BASED)));					
		}else{
			fields.add(Long.toString(range.getEnd(CoordinateSystem.RESIDUE_BASED)));	
			fields.add(Long.toString(range.getBegin(CoordinateSystem.RESIDUE_BASED)));					
		}
	}
	
	
	public static final class Builder{
		private final File outputFile;
		private final OutputStream outStream;

		private Date runDate = new Date();
		
		public Builder(File outputFile) {
			if(outputFile ==null){
				throw new NullPointerException();
			}
			this.outputFile = outputFile;
			this.outStream = null;
		}
		public Builder(OutputStream out) {
			if(out ==null){
				throw new NullPointerException();
			}
			this.outputFile = null;
			this.outStream = out;
		}
		
		
		public Builder setRunDate(Date date){
			if(date ==null){
				throw new NullPointerException();
			}
			this.runDate = date;
			return this;
		}

		public TigrBtabWriter build() throws IOException{
			return new TigrBtabWriter(this);
		}
	}

}
