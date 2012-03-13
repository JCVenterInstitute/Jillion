package org.jcvi.common.core.assembly.asm.atac;

import org.jcvi.common.core.io.TextFileVisitor;

public interface AtacFileVisitor extends TextFileVisitor{

	void visitMatch(AtacMatch match);

	void visitComment(String comment);
}
