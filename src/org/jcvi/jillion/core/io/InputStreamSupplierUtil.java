package org.jcvi.jillion.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
/**
 * Utility class with helper functions for working with InputStreamSuppliers.
 * @author dkatzel
 *
 */
final class InputStreamSupplierUtil {
	
	private InputStreamSupplierUtil() {
		//can not instantiate
	}

	static InputStream getInputStreamForFirstEntryThatMatches(ArchiveInputStream in, Predicate<String> predicate)
			throws IOException {
		ArchiveEntry entry;
		do {
			entry = in.getNextEntry();
		}while(entry!=null && !predicate.test(entry.getName()));
		return in;
	}
}
