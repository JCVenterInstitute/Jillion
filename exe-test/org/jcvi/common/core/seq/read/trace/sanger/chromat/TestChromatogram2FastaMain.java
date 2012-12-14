package org.jcvi.common.core.seq.read.trace.sanger.chromat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.jcvi.common.core.io.IOUtil;
import org.jcvi.common.core.seq.trace.TraceDecoderException;
import org.jcvi.common.core.testUtil.TestUtil;
import org.jcvi.common.core.testUtil.TestUtil.TriedToExitException;
import org.jcvi.trace.sanger.chromatogram.Chromatogram2fasta;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;
public class TestChromatogram2FastaMain {
	private static SecurityManager previousManager=null;
	private static PrintStream OLD_STDOUT=null;
	private static PrintStream OLD_STDERR=null;
	
	private ByteArrayOutputStream stdOutBytes;
	private ByteArrayOutputStream stdErrBytes;
	@Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();
	
	@BeforeClass
	public static void turnOffSystemExitAndRedirectStdOutAndErr(){
		previousManager = System.getSecurityManager();
		System.setSecurityManager(TestUtil.NON_EXITABLE_MANAGER);
		
		OLD_STDOUT =System.out;
		OLD_STDERR =System.err;
	}
	
	@AfterClass
	public static void restoreSecurityManagerStdOutAndErr(){
		System.setSecurityManager(previousManager);
		System.setOut(OLD_STDOUT);
		System.setErr(OLD_STDERR);
	}
	
	@Before
	public void setup(){
		stdOutBytes = new ByteArrayOutputStream();
		stdErrBytes = new ByteArrayOutputStream();
		System.setOut(new PrintStream(stdOutBytes));
		System.setErr(new PrintStream(stdErrBytes));
	}
	
	@Test
	public void askForHelpShouldPrintHelpAndExit() throws TraceDecoderException, IOException{
		try{
		Chromatogram2fasta.main(new String[]{"-help"});
		}catch(TriedToExitException expected){
			assertEquals(0, expected.getExitCode());
		}
		//check that help was printed to stderr
		String helpMessage = new String(stdOutBytes.toByteArray(), IOUtil.UTF_8);
		assertTrue(helpMessage.contains("--help"));
		//nothing written to stderr
		assertEquals(0, stdErrBytes.size());
	}
	    
}
