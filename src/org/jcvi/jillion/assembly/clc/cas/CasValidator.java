package org.jcvi.jillion.assembly.clc.cas;

import java.io.File;
import java.io.IOException;

import org.jcvi.jillion.core.datastore.DataStoreException;
/**
 * {@code CasValidator} is a {@link CasFileVisitor}
 * that will validate the reference and read files
 * to make sure they still contain the same number
 * of records and residues as the cas file thinks they do.
 * Since cas files just point to their input data
 * and reference all records by an int (ex: 1st record, 10th record)
 * and also reference positions of reads without 
 * stating the residue value, a cas can be easily corrupted
 * if the input files are modified after the cas is created.
 * 
 * This class tries to detect these changes.
 * @author dkatzel
 *
 */
class CasValidator extends AbstractCasFileVisitor{

	private final File workingDir;
	

	public CasValidator(File workingDir) {
		this.workingDir = workingDir;
	}

	@Override
	public void visitReadFileInfo(CasFileInfo readFileInfo) {
		try {
			CasFileInfoValidator.validateFileInfo(workingDir, readFileInfo);
		} catch (DataStoreException e) {
			throw new IllegalStateException("error parsing read file(s)", e);
		} catch (IOException e) {
			throw new IllegalStateException("input read file(s) do not exist", e);
		}
	}

	@Override
	public void visitReferenceFileInfo(CasFileInfo referenceFileInfo) {
		try {
			CasFileInfoValidator.validateFileInfo(workingDir, referenceFileInfo);
		} catch (DataStoreException e) {
			throw new IllegalStateException("error parsing reference file(s)", e);
		} catch (IOException e) {
			throw new IllegalStateException("input reference file(s) do not exist", e);
		}
	}

}
