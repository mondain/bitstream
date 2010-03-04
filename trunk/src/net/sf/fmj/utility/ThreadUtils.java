package net.sf.fmj.utility;

import java.io.PrintStream;

/**
 * Utility functions for dumping stack traces.
 * 
 * @author Ken Larson
 * 
 */
public class ThreadUtils {

	public static void printStackTrace(final StackTraceElement[] stack) {
		printStackTrace(stack, System.out); // not to System.err because it is
											// probably not an exception stack
											// trace
	}

	public static void printStackTrace(final StackTraceElement[] stack,
			PrintStream out) {
		final StringBuffer b = new StringBuffer();
		printStackTrace(stack, b);
		out.print(b.toString());
	}

	public static void printStackTrace(final StackTraceElement[] stack,
			StringBuffer b) {
		for (int i = 0; i < stack.length; ++i) {
			StackTraceElement e = stack[i];
			b.append("\t at ");
			b.append(e.getClassName());
			b.append(".");
			b.append(e.getMethodName());
			b.append("(");
			b.append(e.getFileName());
			b.append(":");
			b.append(e.getLineNumber());
			b.append(")\n");

		}
		// b.append("\n");
	}
}
