package org.jcvi.jillion.assembly;

import java.io.IOException;

/**
 * {@code AssemblyTransformationService}
 * is an interface that will parse some
 * kind of Assembly data into messages
 * that an {@link AssemblyTransformer}
 * can use. 
 * @author dkatzel
 *
 */
public interface AssemblyTransformationService {
	/**
	 * Parse the Assembly and call the appropriate
	 * method calls on the given {@link AssemblyTransformer}.
	 * @param transformer the {@link AssemblyTransformer}
	 * to use; can not be null.
	 * @throws IOException if there is a problem parsing 
	 * the assembly or converting it into 
	 * something the {@link AssemblyTransformer}
	 * can use.
	 * @throws NullPointerException if transformer is null.
	 */
	void transform(AssemblyTransformer transformer) throws IOException;
}
