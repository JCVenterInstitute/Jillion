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
 * 	along with  Jillion.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Danny Katzel - initial API and implementation
 ******************************************************************************/
package org.jcvi.jillion.align.pairwise;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import org.jcvi.jillion.core.io.IOUtil;
import org.jcvi.jillion.core.residue.aa.AminoAcid;
import org.jcvi.jillion.core.residue.aa.AminoAcids;

public class PropertyFileAminoAcidScoringMatrix implements AminoAcidScoringMatrix {

	private final float[][] matrix; 
	
	
	public PropertyFileAminoAcidScoringMatrix(InputStream in){
		
		Scanner scanner = new Scanner(in, IOUtil.UTF_8_NAME);
		//first column is amino acids in matrix
		List<AminoAcid> header = AminoAcids.parse(
				scanner.nextLine().replaceAll("\\s+", ""));
		int n = AminoAcid.values().length;
		matrix = new float[n][n];
		while(scanner.hasNextLine()){
			String line = scanner.nextLine();
			Scanner lineScanner = new Scanner(line);
			while(lineScanner.hasNext()){
				AminoAcid aa = AminoAcid.parse(lineScanner.next());
				int x = aa.ordinal();
				for(int i=0; i< header.size(); i++){
					
					float value =lineScanner.nextFloat();
					int y = header.get(i).ordinal();
					matrix[x][y]=value;
				}
			}
		}
		scanner.close();
	}

	
	@Override
	public float getScore(AminoAcid a, AminoAcid b) {
		return getScore(a.getOrdinalAsByte(), b.getOrdinalAsByte());
	}


	private float getScore(byte a, byte b) {
		return matrix[a][b];
	}

	
}
