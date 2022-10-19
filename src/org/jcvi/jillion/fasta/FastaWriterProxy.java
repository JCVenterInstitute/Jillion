package org.jcvi.jillion.fasta;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.jcvi.jillion.core.Sequence;

final class FastaWriterProxy {

	private FastaWriterProxy() {
		//can not instantiate
	}
	@SuppressWarnings("unchecked")
	public static <S, T extends Sequence<S>, F extends FastaRecord<S, T>, W extends FastaWriter<S,T,F>> W createProxy(Class<W> interfaceClass, FastaWriter<S,T,F> writer){
		return (W) Proxy.newProxyInstance(writer.getClass().getClassLoader(), new Class[]{interfaceClass}, new InvocationHandlerImpl<>(writer) );
		
	}
	
	private static final class InvocationHandlerImpl<S, T extends Sequence<S>, F extends FastaRecord<S, T>, W extends FastaWriter<S,T,F>> implements InvocationHandler{

		private final FastaWriter<S,T,F> delegate;
		
		
		@SuppressWarnings("unchecked")
		@Override
		public synchronized Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			
			String name = method.getName();
			if("write".equals(name)){
				//special handling of write method calls
				//subclasses override with more specific subtypes
				//which causes reflection problems so need to do explicit casting
				
				if(args.length ==1){
					//write full record
					delegate.write((F) args[0]);
					return null;
				}else if(args.length ==2){
					delegate.write((String) args[0], (T) args[1]);
					return null;
				}else if(args.length ==3){
					delegate.write((String) args[0], (T) args[1], (String) args[2]);
					return null;
				}
			}
			
			return method.invoke(delegate, args);
			
		}

		public InvocationHandlerImpl(FastaWriter<S,T,F> delegate) {
			this.delegate = delegate;
		}
		
	}
}
