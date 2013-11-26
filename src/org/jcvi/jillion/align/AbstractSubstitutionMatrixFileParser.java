/*******************************************************************************
 * Copyright (c) 2013 J. Craig Venter Institute.
 * 	This file is part of Jillion
 * 
 * 	 Jillion is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 	
 * 	 Jillion is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU General Public License
 * 	along with  Jillion.  If not, see http://www.gnu.org/licenses
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.align;

import java.io.InputStream;
import java.util.Scanner;

import org.jcvi.jillion.core.Sequence;
import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.Residue;
/**
 * {@code AbstractSubstitutionMatrixFileParser}
 * is a class that can parse
 * the  {@link SubstitutionMatrix} text files
 * provided by NCBI.
 * @author dkatzel
 * @see ftp://ftp.ncbi.nlm.nih.gov/blast/matrices
 */
abstract class AbstractSubstitutionMatrixFileParser<R extends Residue> implements SubstitutionMatrix<R> {

	private final float[][] matrix; 
	
	
	protected AbstractSubstitutionMatrixFileParser(InputStream in){
		Scanner scanner = null;
		try{
			scanner = new Scanner(in, IOUtil.UTF_8_NAME);
			
			//first column is amino acids in matrix
			String headerLine = parseColumns(scanner);
			//Sequence<R> header = new ProteinSequenceBuilder(headerLine).build();
			Sequence<R> header = parseColumns(headerLine);
			
			long headerLength = header.getLength();
			int n = getNumberOfValues();
			matrix = new float[n][n];
			while(scanner.hasNextLine()){
				String line = scanner.nextLine();
				Scanner lineScanner = null;
				try{
					lineScanner = new Scanner(line);
					while(lineScanner.hasNext()){
						String next = lineScanner.next();
						R aa = parse(next);
						int x = aa.getOrdinalAsByte();
						
						for(int i=0; i< headerLength; i++){
						
							float value =lineScanner.nextFloat();
							int y = header.get(i).getOrdinalAsByte();
							matrix[x][y]=value;
						}
					}
				}finally{
					if(lineScanner !=null){
						lineScanner.close();
					}
				}
			}
		}finally{
			if(scanner !=null){
				scanner.close();
			}
			
		}
		
	}

	/**
	 * Convert the given String (probably
	 * 1 character long) into a single 
	 * {@link Residue} object. 
	 * @param s
	 * @return
	 */
	protected abstract R parse(String s);

	/**
	 * Get the number of possible Residue values.
	 * For example, for {@link org.jcvi.jillion.core.residue.nt.Nucleotide}s,
	 * this method would return {@code Nucleotide.values().length}
	 * @return
	 */
	protected abstract int getNumberOfValues();

	
	private String parseColumns(Scanner scanner) {
		boolean done=false;
		String line;
		do{
			line = scanner.nextLine();
			//skip comments
			done = line.charAt(0) !='#';
		}while(!done);
		
		return line;
	}
	/**
	 * Convert the a line of residue characters
	 * separated by whitespace into a Sequence where
	 * {@code sequence.get(i)} is the ith column.
	 * @param columns
	 * @return
	 */
	protected abstract Sequence<R> parseColumns(String columns);
	
	@Override
	public float getValue(R a, R b) {
		return getValue(a.getOrdinalAsByte(), b.getOrdinalAsByte());
	}


	private float getValue(byte a, byte b) {
		return matrix[a][b];
	}

	
}
