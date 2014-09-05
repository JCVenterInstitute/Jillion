package org.jcvi.jillion.profile;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import org.jcvi.jillion.core.residue.nt.Nucleotide;
/**
 * {@code DisplayCountStrategy} tells the profile
 * how to display the values at each position
 * in a profile.
 * @author dkatzel
 *
 */
public enum DisplayCountStrategy{
	
	/**
	 * Display the actual counts
	 * which may be fractional if ambiguities
	 * exist at that position. The summation of all
	 * the counts at that position will equal the total
	 * coverage at that position.
	 */
	COUNTS{

		@Override
		void write(PrintWriter writer, Nucleotide mostFreq, double gap,
				double a, double c, double g, double t) throws IOException {
			writer.printf("%s\t%s\t%s\t%s\t%s\t%s%n", 
					mostFreq,
					DECIMAL_FORMAT.format(gap),
					DECIMAL_FORMAT.format(a),
					DECIMAL_FORMAT.format(c),
					DECIMAL_FORMAT.format(g),
					DECIMAL_FORMAT.format(t)
					);
			
		}
		
	},
	/**
	 * Display the percentage each base contributes
	 * at that position.  Values will be whole numbers between
	 * 0 and 100 any fractional values are rounded
	 * using {@link BigDecimal#ROUND_HALF_UP}. 
	 * The summation of all
	 * the counts at that position will equal 100
	 * if there is coverage or 0 if the position has no coverage.
	 */
	PERCENTAGES{
		@Override
		void write(PrintWriter writer, Nucleotide mostFreq, double gap,
				double a, double c, double g, double t) throws IOException {
			double total = gap+a+c+g+t;
			if(total==0D){
				//avoid divide by 0
				writer.printf("%s\t0\t0\t0\t0\t0%n", mostFreq);
			}else{
				writer.printf("%s\t%.0f\t%.0f\t%.0f\t%.0f\t%.0f%n", 
					mostFreq,
					gap/total *100,
					100* a/total,
					100* c/total,
					100* g/total,
					100* t/total
					);
			}
		}
	};
	
	/**
	 * Use {@link DecimalFormat} to format the counts
	 * of ACGT- since it will print the min number
	 * of digits to the screen for example
	 * <pre>
	 * A	C	G	T
	 * 0	1	2.5	3.25
	 * </pre>
	 * {@link String#format(String, Object...)}
	 * will always print the same number of digits
	 * include trailing 0s.
	 */
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.##");
	
	
	abstract void write(PrintWriter writer, Nucleotide mostFreq,
			double gap, double a, double c, double g, double t) throws IOException;
	
	
}