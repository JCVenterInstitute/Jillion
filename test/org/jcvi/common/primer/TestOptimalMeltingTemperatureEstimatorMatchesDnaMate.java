package org.jcvi.common.primer;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.jcvi.common.core.symbol.residue.nt.NucleotideSequence;
import org.jcvi.common.core.symbol.residue.nt.NucleotideSequenceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
@RunWith(Parameterized.class)
public class TestOptimalMeltingTemperatureEstimatorMatchesDnaMate {
	private NucleotideSequence sequence;
	private int oligoConcentration;
	private int saltConcentration;
	private double breslaur,santaLucia,sugimoto;
	
	
	
	@Parameters
	public static Collection<?> data(){
		//sequence oligo salt breslaur santaLucia sugimoto
		return Arrays.asList(new Object[][]{
                {"ACGCGTGAATTCTGGCCAA", 6000, 115, 74.0D, 61.8D, 64.4D},
                {"AATATATGAATTCTAATTAA", 5700, 50, 41.6D, 33.8D, 39.8D},
                {"ATATATATAGCTATATATAT", 500, 50, 30.2D, 27.5D, 33.9D},
                {"AAAAAAAAAAAAAAATTTTTTTTTT", 450, 200, 62.3D, 47.6D, 52.9D},
        });
	}
	
	
	public TestOptimalMeltingTemperatureEstimatorMatchesDnaMate(
			String sequence, int oligoConcentration,
			int saltConcentration, double breslaur, double santaLucia,
			double sugimoto) {
		this.sequence = new NucleotideSequenceBuilder(sequence).build();
		this.oligoConcentration = oligoConcentration;
		this.saltConcentration = saltConcentration;
		this.breslaur = breslaur;
		this.santaLucia = santaLucia;
		this.sugimoto = sugimoto;
	}


	@Test
	public void breslaurWithSaltAdjustment(){
		double noSalt =OptimalMeltingTemperatureEstimator.BRESLAUR.estimateTm(sequence, oligoConcentration);
		assertEquals(sequence.toString(), breslaur, 
				SaltCorrectionStrategy.SCHILDKRAUT_LIFSON.adjustTemperature(noSalt, saltConcentration),
				0.1D);
	}
	
	@Test
	public void santaLuciaAllawiWithSaltAdjustment(){
		double noSalt =OptimalMeltingTemperatureEstimator.ALLAWI_SANTALUCIA.estimateTm(sequence, oligoConcentration);
		assertEquals(sequence.toString(), santaLucia, 
				SaltCorrectionStrategy.SCHILDKRAUT_LIFSON.adjustTemperature(noSalt, saltConcentration),
				0.1D);
	}
	@Test
	public void sugimotoWithSaltAdjustment(){
		double noSalt =OptimalMeltingTemperatureEstimator.SUGIMOTO.estimateTm(sequence, oligoConcentration);
		assertEquals(sequence.toString(), sugimoto, 
				SaltCorrectionStrategy.SCHILDKRAUT_LIFSON.adjustTemperature(noSalt, saltConcentration),
				0.1D);
	}
}
