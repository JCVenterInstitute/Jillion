package org.jcvi.jillion.core.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.jcvi.jillion.spi.io.InputStreamSupplierFactory;

public final class InputStreamSupplierRegistery {

	private List<InputStreamSupplierFactory> factories;
	private AtomicInteger magicNumberSize = new AtomicInteger();
	
	
	private static List<InputStreamSupplierFactory> DEFAULTS = List.of(
			new ZipInputStreamSupplierFactory(),
			new GZipInputStreamSupplierFactory(),
			new XZInputStreamSupplierFactory(),
			new TarInputStreamSupplierFactory());
	
	private static InputStreamSupplierRegistery instance = new InputStreamSupplierRegistery();
	
	public static InputStreamSupplierRegistery getInstance() {
		return instance;
	}
	
	private InputStreamSupplierRegistery(){
		//singleton
		
		factories = new CopyOnWriteArrayList<InputStreamSupplierFactory>(DEFAULTS);
		updateMagicNumberSize();
	}
	
	private void updateMagicNumberSize() {
		magicNumberSize.set(factories.stream()
					.mapToInt(InputStreamSupplierFactory::getMagicNumberLength)
					.max().getAsInt());
	}
	
	public void register(InputStreamSupplierFactory factory) {
		this.factories.add(Objects.requireNonNull(factory));
		updateMagicNumberSize();
	}
	
	public InputStreamSupplier createInputStreamSupplierFor(File f) throws IOException {
		 IOUtil.verifyIsReadable(f);
	       
       //check that file isn't empty
       //if the file is empty then there's no magic number
       if(f.length() ==0){
    	   return new RawFileInputStreamSupplier(f);
       }
       
       byte[] magicNumber = new byte[magicNumberSize.get()];
       int numBytes;
       try(InputStream in = new BufferedInputStream(new FileInputStream(f))){
    	   numBytes = IOUtil.tryBlockingRead(in, magicNumber);
       }
       if(numBytes < magicNumber.length) {
    	   for(InputStreamSupplierFactory factory: factories) {
	    	   if(factory.getMagicNumberLength() <= numBytes && factory.supports(magicNumber)) {
	    		   return factory.createFromFile(f);
	    	   }
	       }
       }else {
	       for(InputStreamSupplierFactory factory: factories) {
	    	   if(factory.supports(magicNumber)) {
	    		   return factory.createFromFile(f);
	    	   }
	       }
       }
       return new RawFileInputStreamSupplier(f);
	}
	
	public InputStream decodeInputStream(InputStream in) throws IOException {
	    byte[] magicNumber = new byte[magicNumberSize.get()];
		PushbackInputStream pushbackInputStream = new PushbackInputStream(in, magicNumber.length);
		int bytesRead = IOUtil.tryBlockingRead(pushbackInputStream, magicNumber);
		pushbackInputStream.unread(magicNumber, 0, bytesRead);
		
		InputStream found=null;
		if(bytesRead == magicNumber.length) {
			 for(InputStreamSupplierFactory factory: factories) {
			   	   if(factory.supports(magicNumber)) {
			   		   found = factory.create(pushbackInputStream);
			   		   break;
			   	   }
		      }
		}else {
			 for(InputStreamSupplierFactory factory: factories) {
			   	   if(factory.getMagicNumberLength() <= bytesRead && factory.supports(magicNumber)) {
			   		   found = factory.create(pushbackInputStream);
			   		   break;
			   	   }
		      }
		}
     
      if(found==null) {
    	  return pushbackInputStream;
      }
      return decodeInputStream(found);
	}
}
