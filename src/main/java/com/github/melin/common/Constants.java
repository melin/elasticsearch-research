/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */
package com.github.melin.common;

import java.lang.reflect.Field;
import java.util.Collections;

/**
 * Create on @2013-12-18 @下午3:56:43
 * 
 * @author bsli@ustcinfo.com
 */
public class Constants {
	public static final String OS_ARCH = System.getProperty("os.arch");

	public static final boolean JRE_IS_MINIMUM_JAVA7;
	public static final boolean JRE_IS_MINIMUM_JAVA8;

	/** True iff running on a 64bit JVM */
	public static final boolean JRE_IS_64BIT;

	static {
		boolean is64Bit = false;
		try {
			final Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
			final Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
			unsafeField.setAccessible(true);
			final Object unsafe = unsafeField.get(null);
			final int addressSize = ((Number) unsafeClass.getMethod(
					"addressSize").invoke(unsafe)).intValue();
			// System.out.println("Address size: " + addressSize);
			is64Bit = addressSize >= 8;
		} catch (Exception e) {
			final String x = System.getProperty("sun.arch.data.model");
			if (x != null) {
				is64Bit = x.indexOf("64") != -1;
			} else {
				if (OS_ARCH != null && OS_ARCH.indexOf("64") != -1) {
					is64Bit = true;
				} else {
					is64Bit = false;
				}
			}
		}
		JRE_IS_64BIT = is64Bit;

		// this method only exists in Java 7:
		boolean v7 = true;
		try {
			Throwable.class.getMethod("getSuppressed");
		} catch (NoSuchMethodException nsme) {
			v7 = false;
		}
		JRE_IS_MINIMUM_JAVA7 = v7;

		if (JRE_IS_MINIMUM_JAVA7) {
			// this method only exists in Java 8:
			boolean v8 = true;
			try {
				Collections.class.getMethod("emptySortedSet");
			} catch (NoSuchMethodException nsme) {
				v8 = false;
			}
			JRE_IS_MINIMUM_JAVA8 = v8;
		} else {
			JRE_IS_MINIMUM_JAVA8 = false;
		}
	}
}
