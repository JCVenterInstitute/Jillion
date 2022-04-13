package org.jcvi.jillion.align;

import java.util.List;

import org.jcvi.jillion.align.IndelDetector.Indel;
import org.jcvi.jillion.align.IndelDetector.IndelType;
import org.jcvi.jillion.core.Range;
import org.jcvi.jillion.core.residue.ResidueSequence;
import org.jcvi.jillion.core.residue.nt.NucleotideSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.*;
import static java.util.Collections.emptyList;
@RunWith(Parameterized.class)
public abstract class AbstractIndelDetectorTest<T extends ResidueSequence> {

	
	
	private String a, b;
	private List<Indel> expected;
	
	protected abstract IndelDetector<T> getDetectorInstance();
	
	@Parameters(name="{0}")
	public static List<Object[]> data(){
		return List.of(
				new Object[] {"empty", "","", emptyList()},
				new Object[] {"no gaps should have no indels", "ACGT","ACGT", emptyList()},
				new Object[] {"one query gap should have one deletion", "ACGT","AC-T", List.of(deletion(2))},
				new Object[] {"two separate query gaps should have two deletions", "ACGTAT","AC-T-T", List.of(deletion(2), deletion(4))},
				new Object[] {"one subject gap should have one insertion", "AC-T","ACGT", List.of(insertion(2))},
				new Object[] {"two separate subject gaps should have two insertions", "AC-T-T","ACGTAT", List.of(insertion(2), insertion(4))},
				
				new Object[] {"one long query gap should have one deletion", "ACGT","A--T", List.of(deletion(1,2))},
				new Object[] {"one long subject gap should have one insertion", "A--T","ACGT", List.of(insertion(1, 2))},
				
				new Object[] {"query starts with a gap should have one deletion", "ACGT","-CGT", List.of(deletion(0))},
				new Object[] {"query starts with long gap should have one deletion", "ACGT","---T", List.of(deletion(0,2))},
				
				new Object[] {"subject stats with gap should have one insertion", "-CGT","ACGT", List.of(insertion(0))},
				new Object[] {"subject starts with long gap should have one insertion", "---T","ACGT", List.of(insertion(0,2))},
				
				new Object[] {"query ends with a gap should have one deletion", "ACGT","ACG-", List.of(deletion(3))},
				new Object[] {"query ends with long gap should have one deletion", "ACGT","AC--", List.of(deletion(2,3))},
				
				//complex case
				new Object[] {"query and subject have same gap ignore it", "AC-T","AC-T", emptyList()},
				
				new Object[] {"query and subject have same gap ignore it and other different query gap", "AC-TAT","AC-T-T", List.of(deletion(4))},
				new Object[] {"query and subject have same gap ignore it and other different suject gap", "AC-T-T","AC-TAT", List.of(insertion(4))},
				
				new Object[] {"query and subject have same gap ignore it and other different long query gap", "AC-TAAT","AC-T--T", List.of(deletion(4,5))},
				new Object[] {"query and subject have same gap ignore it and other different suject gap", "AC-T--T","AC-TAAT", List.of(insertion(4,5))}
				);
	}
	public static Indel deletion( int...coords) {
		return indel(IndelType.DELETION, coords);
	}
	public static Indel insertion( int...coords) {
		return indel(IndelType.INSERTION, coords);
	}
	public static Indel indel(IndelType type, int...coords) {
		if(coords.length==1) {
			return new Indel(type, Range.of(coords[0]));
		}
		return new Indel(type, Range.of(coords[0], coords[1]));
	}
	public AbstractIndelDetectorTest(String ignored, String a, String b, List<Indel> expected) {
		this.a = a;
		this.b = b;
		this.expected = expected;
	}

	@Test
	public void findExpectedIndels() {
		List<Indel> actual = findIndels(a,b);
		assertEquals(expected, actual);
	}
	
	List<Indel> findIndels(String a, String b){
		return getDetectorInstance().findIndels(toSequence(a), toSequence(b));
	}
	protected abstract T toSequence(String s);
}
