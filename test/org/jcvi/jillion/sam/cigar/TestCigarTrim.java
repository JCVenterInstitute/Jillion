package org.jcvi.jillion.sam.cigar;

import java.util.List;

import org.jcvi.jillion.core.Range;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class TestCigarTrim {

	private String initialCigar;
	private Range validRange;
	private String expectedCigar;
	
	@Parameters(name = "{0}")
	public static List<Object[]> data(){
		return List.of(
				//without previous clips
				new Object[] {"no trim", "8M2I43M", Range.ofLength(8+2+50+3), "8M2I43M"},
				new Object[] {"no trim with deletion", "8M2I40M1D3M", Range.ofLength(8+2+50+3), "8M2I40M1D3M"},
				new Object[] {"no trim with padding", "8M2I40M1P3M", Range.ofLength(8+2+50+3), "8M2I40M1P3M"},
				
				new Object[] {"validRange exactly on elements multiple elements deep", "8M2I40M3M", new Range.Builder(40).shift(10).build(), "10S40M3S"},
				new Object[] {"validRange exactly on elements single element deep", "8M2I40M3M", new Range.Builder(42).shift(8).build(), "8S2I40M3S"},
				new Object[] {"validRange partial element", "8M2I40M1D3M", new Range.Builder(46).shift(4).build(), "4S4M2I40M3S"},
				new Object[] {"validRange partial elements both sides", "8M2I40M1D3M", new Range.Builder(44).shift(4).build(), "4S4M2I38M5S"},
				
				new Object[] {"validRange exactly on elements multiple elements deep with deletion downstream", "8M2I40M1D3M", new Range.Builder(40).shift(10).build(), "10S40M3S"},
				new Object[] {"validRange exactly on elements single element deep with deletion downstream", "8M2I40M1D3M", new Range.Builder(42).shift(8).build(), "8S2I40M3S"},
				new Object[] {"validRange exactly on elements single element deep ends in deletion", "8M2I40M1D3M", new Range.Builder(43).shift(8).build(), "8S2I40M1D1M2S"},
				
				new Object[] {"validRange partial element padding downstream", "8M2I40M1P3M", new Range.Builder(44).shift(4).build(), "4S4M2I38M5S"},
				
				new Object[] {"validRange exactly on elements multiple elements deep with padding downstream", "8M2I40M1P3M", new Range.Builder(40).shift(10).build(), "10S40M3S"},
				new Object[] {"validRange exactly on elements single element deep with padding downstream", "8M2I40M1P3M", new Range.Builder(42).shift(8).build(), "8S2I40M3S"},
				new Object[] {"validRange exactly on elements single element deep ends in padding", "8M2I40M1P3M", new Range.Builder(43).shift(8).build(), "8S2I40M1P1M2S"},
				
				new Object[] {"validRange partial element deletion downstream", "8M2I40M1D3M", new Range.Builder(44).shift(4).build(), "4S4M2I38M5S"},
				
				
				new Object[] {"completly trimmed", "8M2I43M", Range.EMPTY_RANGE, "53S"},
				new Object[] {"completly trimmed with deletion should ignore deletion", "8M2I40M1D3M", Range.EMPTY_RANGE, "53S"},
				new Object[] {"completly with padd padding is ignored", "8M2I40M1P3M", Range.EMPTY_RANGE, "53S"},
				new Object[] {"completly trimmed soft clips both sides", "2S8M2I43M2S", Range.EMPTY_RANGE, "57S"},
				new Object[] {"completly trimmed soft clips left side only", "2S8M2I43M", Range.EMPTY_RANGE, "55S"},
				new Object[] {"completly trimmed soft clips right side only", "8M2I43M2S", Range.EMPTY_RANGE, "55S"},
				new Object[] {"completly trimmed hard clips both sides", "2H8M2I43M2H", Range.EMPTY_RANGE, "2H53S2H"},
				new Object[] {"completly trimmed hard clips left side only", "2H8M2I43M", Range.EMPTY_RANGE, "2H53S"},
				new Object[] {"completly trimmed hard clips right side only", "8M2I43M2H", Range.EMPTY_RANGE, "53S2H"},
				
				new Object[] {"completly trimmed hard and soft clips both sides", "2H2S8M2I43M2S2H", Range.EMPTY_RANGE, "2H57S2H"},
				new Object[] {"completly trimmed hard and soft clips left side only", "2H2S8M2I43M", Range.EMPTY_RANGE, "2H55S"},
				new Object[] {"completly trimmed hard and soft clips right side only", "8M2I43M2S2H", Range.EMPTY_RANGE, "55S2H"},
				new Object[] {"left trimOnly", "8M2I40M1D3M", new Range.Builder(44).shift(10).build(), "10S40M1D3M"},
				new Object[] {"right trimOnly downstream deletion", "8M2I40M1D3M", Range.ofLength(50), "8M2I40M3S"},
				new Object[] {"right trimOnly", "8M2I40M3M", Range.ofLength(50), "8M2I40M3S"},
				
				//with previous clips
				
				new Object[] {"hard clips no more trim", "5H1M3H", new Range.Builder(1).shift(5).build(), "5H1M3H"},
				new Object[] {"soft clips no more trim", "5S1M3S", new Range.Builder(1).shift(5).build(), "5S1M3S"},
				new Object[] {"hard and soft clips no more trim", "3H2S1M1S2H", new Range.Builder(1).shift(5).build(), "3H2S1M1S2H"},
				// "8H3S40M"
				new Object[] {"hard clips trim", "8H43M", new Range.Builder(40).shift(11).build(), "8H3S40M"},
				new Object[] {"hard clips trim other side", "8H43M", new Range.Builder(40).shift(8).build(), "8H40M3S"},
				
				new Object[] {"soft clips trim and merge", "8S43M", new Range.Builder(40).shift(11).build(), "11S40M"},
				new Object[] {"soft clips trim and merge other side", "8S43M", new Range.Builder(40).shift(8).build(), "8S40M3S"},
				new Object[] {"soft clips trim and merge other side trim", "8S43M1S", new Range.Builder(40).shift(8).build(), "8S40M4S"},
				
				//real world examples
				new Object[] {"clipped insertion and deletions cancel out", "15M3I3M3D122M", new Range.Builder(93).shift(23).build(), "23S93M27S"},
				new Object[] {"clipped insertion and deletions cancel out2", "5M1D5M1I138M", new Range.Builder(127).shift(22).build(), "22S127M"},
				new Object[] {"clipped insertion with deletion in valid range", "11M1I27M1D104M", new Range.Builder(103).shift(18).build(), "18S21M1D82M22S"},
				
				new Object[] {"deletion at start of new valid range", "20M2D126M", new Range.Builder(103).shift(20).build(), "20S103M23S"}
				);
	}
	public TestCigarTrim(String ignored, String initialCigar, Range validRange, String expectedCigar) {
		this.initialCigar = initialCigar;
		this.validRange = validRange;
		this.expectedCigar = expectedCigar;
	}
	
	@Test
	public void trim() {
		Cigar input = Cigar.parse(initialCigar);
		Cigar sut = input.toBuilder()
					.trim(validRange)
					.build();
		assertEquals(expectedCigar, sut.toCigarString());
	}
}
