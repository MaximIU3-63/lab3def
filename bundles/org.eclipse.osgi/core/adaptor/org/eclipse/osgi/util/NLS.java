/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.osgi.util;

import org.eclipse.osgi.framework.internal.core.MessageResourceBundle;

/**
 * Common superclass for all message bundle classes.  Provides convenience
 * methods for manipulating messages.
 * 
 * @since 3.1
 */
public abstract class NLS {
	/**
	 * @deprecated This was never intended to be API.  It will be
	 * removed prior to 3.1 M7.
	 */
	public static boolean DEBUG_MESSAGE_BUNDLES = false;

	/**
	 * Bind the given message's substitution locations with the given string values.
	 * 
	 * @param message the message to be manipulated
	 * @param binding the object to be inserted into the message
	 * @return the manipulated String
	 */
	public static String bind(String message, Object binding) {
		return bind(message, new Object[] {binding});
	}

	/**
	 * Bind the given message's substitution locations with the given string values.
	 * 
	 * @param message the message to be manipulated
	 * @param binding1 An object to be inserted into the message
	 * @param binding2 A second object to be inserted into the message
	 * @return the manipulated String
	 */
	public static String bind(String message, Object binding1, Object binding2) {
		return bind(message, new Object[] {binding1, binding2});
	}

	/**
	 * Bind the given message's substitution locations with the given string values.
	 * 
	 * @param message the message to be manipulated
	 * @param bindings An array of objects to be inserted into the message
	 * @return the manipulated String
	 */
	public static String bind(String message, Object[] bindings) {
		if (message == null)
			return "No message available"; //$NON-NLS-1$
		if (bindings == null)
			return message;
		return format(message, bindings);
	}

	/**
	 * Generates a formatted text string given a source string
	 * containing "argument markers" of the form "{argNum}"
	 * where each argNum must be in the range 0..9. The result
	 * is generated by inserting the toString of each argument
	 * into the position indicated in the string.
	 * <p>
	 * To insert the "{" character into the output, use a single
	 * backslash character to escape it (i.e. "\{"). The "}"
	 * character does not need to be escaped.
	 * @param		format String
	 *					the format to use when printing.
	 * @param		args Object[]
	 *					the arguments to use.
	 * @return		String
	 *					the formatted message.
	 */
	private static String format(String format, Object[] args) {
		StringBuffer answer = new StringBuffer();
		String[] argStrings = new String[args.length];

		for (int i = 0; i < args.length; ++i) {
			argStrings[i] = args[i] == null ? "<null>" : args[i].toString(); //$NON-NLS-1$
		}

		int lastI = 0;

		for (int i = format.indexOf('{', 0); i >= 0; i = format.indexOf('{', lastI)) {
			if (i != 0 && format.charAt(i - 1) == '\\') {
				// It's escaped, just print and loop.
				if (i != 1) {
					answer.append(format.substring(lastI, i - 1));
				}
				answer.append('{');
				lastI = i + 1;
			} else {
				// It's a format character.
				if (i > format.length() - 3) {
					// Bad format, just print and loop.
					answer.append(format.substring(lastI, format.length()));
					lastI = format.length();
				} else {
					int argnum = (byte) Character.digit(format.charAt(i + 1), 10);
					if (argnum < 0 || format.charAt(i + 2) != '}') {
						// Bad format, just print and loop.
						answer.append(format.substring(lastI, i + 1));
						lastI = i + 1;
					} else {
						// Got a good one!
						answer.append(format.substring(lastI, i));
						if (argnum >= argStrings.length) {
							answer.append("<missing argument>"); //$NON-NLS-1$
						} else {
							answer.append(argStrings[argnum]);
						}
						lastI = i + 3;
					}
				}
			}
		}

		if (lastI < format.length()) {
			answer.append(format.substring(lastI, format.length()));
		}

		return answer.toString();
	}

	/**
	 * Initialize the given class with the values from the specified message bundle.
	 * 
	 * @param bundleName fully qualified path of the class name
	 * @param clazz the class where the constants will exist
	 */
	public static void initializeMessages(String bundleName, Class clazz) {
		MessageResourceBundle.load(bundleName, clazz);
	}

	/**
	 * Creates a new NLS instance.
	 */
	protected NLS() {
		super();
	}
}