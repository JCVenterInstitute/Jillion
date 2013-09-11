package org.jcvi.jillion;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jcvi.jillion.trace.chromat.Chromatogram;
import org.jcvi.jillion.trace.chromat.ChromatogramFactory;
import org.jcvi.jillion.trace.chromat.ChromatogramWriter;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogram;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramBuilder;
import org.jcvi.jillion.trace.chromat.scf.ScfChromatogramWriterBuilder;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogram;
import org.jcvi.jillion.trace.chromat.ztr.ZtrChromatogramWriterBuilder;

public class ConvertAb12Ztr {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		File ab1 = new File("path/to/trace.ab1");

		//creates Abi chromatogram and sets name to file name without the file extension
		Chromatogram chromo = ChromatogramFactory.create(ab1);
		
		 //if you want to add additional comments you have 
		 //to make a new object using the Builder
		
		Map<String,String> myComments = new HashMap<String, String>(chromo.getComments());
		//now add more comments
		myComments.put("extraComment", "value");
		ScfChromatogram chromo2 = new ScfChromatogramBuilder(chromo)
										.comments(myComments)
										.build();
		
		System.out.println(chromo.getNucleotideSequence());
		
		File outputScf = new File(chromo.getId() + ".scf");
		
		 ChromatogramWriter scfWriter = new ScfChromatogramWriterBuilder(outputScf)		 									
 														.build();
		 
		 
		 try{
			 scfWriter.write(chromo);
		 }finally{
			 //required to close
			 scfWriter.close();
		 }
		 
		 ChromatogramWriter ztrWriter = new ZtrChromatogramWriterBuilder(new File("out.ztr"))
		 										.build();
		 
		 try{
			 ztrWriter.write(chromo);
		 }finally{
			 //required to close
			 ztrWriter.close();
		 }
		 
		 Chromatogram reparsed = ChromatogramFactory.create(new File("out.ztr"));
		 if(!(reparsed instanceof ZtrChromatogram)){
			 throw new IllegalStateException("not a ztr");
		 }
		 if(!reparsed.getChannelGroup().equals(chromo.getChannelGroup())){
			 throw new IllegalStateException("channels don't match");
		 }
	}

}
