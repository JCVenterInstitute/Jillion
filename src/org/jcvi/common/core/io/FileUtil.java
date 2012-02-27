package org.jcvi.common.core.io;

import java.io.File;
import java.io.IOException;

import org.jcvi.common.core.util.LIFOQueue;

public class FileUtil {
	/**
     * The Unix separator character.
     */
    private static final char UNIX_SEPARATOR = '/';

    /**
     * The Windows separator character.
     */
    private static final char WINDOWS_SEPARATOR = '\\';
    
	public static String createRelavitePathFrom(File root, File otherFile) throws IOException{
		return createRelavitePathFrom(root, otherFile, File.separatorChar);
	}
	/**
	 * Creates a String of the relative path from the given root to the other given file.
	 * @param root the File to start from.
	 * @param otherFile the File to get to.
	 * @param pathSeparator the path separator for this File system.
	 * @return a String of containing the relative file path required
	 * to traverse the file system to get from one file to the other.
	 * @throws IOException if there is a problem finding the location of either
	 * file on the file system.
	 */
	public static String createRelavitePathFrom(File root, File otherFile, char pathSeparator) throws IOException{
		LIFOQueue<String> rootStack = getCanonicalStackFor(root);
		LIFOQueue<String> otherStack = getCanonicalStackFor(otherFile);
		
		//find point where elements on stack no longer equal
		while(!rootStack.isEmpty() && !otherStack.isEmpty() && rootStack.peek().equals(otherStack.peek())){
			rootStack.remove();
			otherStack.remove();
		}
		StringBuilder relativePath= new StringBuilder();
		while(!rootStack.isEmpty()){
			relativePath.append("..").append(pathSeparator);
			rootStack.remove();
		}
		while(!otherStack.isEmpty()){
			relativePath.append(otherStack.remove()).append(pathSeparator);
		}
		if(relativePath.length()==0){
			return "";
		}
		//chop off last separator
		return relativePath.substring(0, relativePath.length()-1);
		
	}
	
	private static LIFOQueue<String> getCanonicalStackFor(File f) throws IOException{
			LIFOQueue<String> stack = new LIFOQueue<String>();
			File currentCanonicalPath = f.getCanonicalFile();
			while(currentCanonicalPath != null) {
				stack.add(currentCanonicalPath.getName());
				currentCanonicalPath = currentCanonicalPath.getParentFile();
			}
			
		return stack;
	}
	
	private static int indexOfLastSeparator(String filename) {
        int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
        int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
        return Math.max(lastUnixPos, lastWindowsPos);
    }
	private static int indexOfExtension(String filename) {
        int extensionPos = filename.lastIndexOf('.');
        int lastSeparator = indexOfLastSeparator(filename);
        return (lastSeparator > extensionPos ? -1 : extensionPos);
    }
	public static String getExtension(File file){
		if(file ==null){
			return null;
		}
		String filename = file.getName();
		return getExtension(filename);
	}
	public static String getExtension(String filename) {
		if(filename ==null){
			return null;
		}
		int extensionIndex = indexOfExtension(filename);
		if (extensionIndex == -1) {
            return "";
        } else {
            return filename.substring(extensionIndex + 1);
        }
	}
	
	public static String getBaseName(File file){
		if(file ==null){
			return null;
		}
		return getBaseName(file.getName());
	}
	public static String getBaseName(String filename) {
		if(filename ==null){
			return null;
		}
		int lastSeparatorIndex = indexOfLastSeparator(filename);		
		int extensionIndex = indexOfExtension(filename);
		
		if (extensionIndex == -1) {
            return filename.substring(lastSeparatorIndex+1);
        } else {
            return filename.substring(lastSeparatorIndex+1,extensionIndex);
        }
	}
}
