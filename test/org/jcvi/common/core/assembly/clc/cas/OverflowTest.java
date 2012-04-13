package org.jcvi.common.core.assembly.clc.cas;

import org.jcvi.common.core.io.IOUtil;

public class OverflowTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		byte[] array = new byte[1];
		
		for(int i=0; i<Byte.MAX_VALUE;i++){
			array[0]++;
		}
		System.out.println(array[0]);
		array[0]++;
		System.out.println(array[0]);
		System.out.println(IOUtil.toUnsignedByte(array[0]));
	}

}
