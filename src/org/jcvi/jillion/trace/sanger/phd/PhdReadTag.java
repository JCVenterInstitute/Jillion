package org.jcvi.jillion.trace.sanger.phd;

import java.util.Date;

import org.jcvi.jillion.core.Range;

public interface PhdReadTag {

	String getType();

	String getSource();

	Range getUngappedRange();

	Date getDate();

	String getComment();

	String getFreeFormData();

}