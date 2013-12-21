/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */
package com.github.melin.common;

/**
 * Create on @2013-12-18 @下午3:54:55
 * 
 * @author bsli@ustcinfo.com
 */
public class ArrayUtil {

	/**
	 * Returns an array size >= minTargetSize, generally over-allocating
	 * exponentially to achieve amortized linear-time cost as the array grows.
	 * 
	 * NOTE: this was originally borrowed from Python 2.4.2 listobject.c sources
	 * (attribution in LICENSE.txt), but has now been substantially changed
	 * based on discussions from java-dev thread with subject "Dynamic array
	 * reallocation algorithms", started on Jan 12 2010.
	 * 
	 * @param minTargetSize
	 *            Minimum required value to be returned.
	 * @param bytesPerElement
	 *            Bytes used by each element of the array. See constants in
	 *            {@link RamUsageEstimator}.
	 * 
	 * @lucene.internal
	 */

	public static int oversize(int minTargetSize, int bytesPerElement) {

		if (minTargetSize < 0) {
			// catch usage that accidentally overflows int
			throw new IllegalArgumentException("invalid array size "
					+ minTargetSize);
		}

		if (minTargetSize == 0) {
			// wait until at least one element is requested
			return 0;
		}

		// asymptotic exponential growth by 1/8th, favors
		// spending a bit more CPU to not tie up too much wasted
		// RAM:
		int extra = minTargetSize >> 3;

		if (extra < 3) {
			// for very small arrays, where constant overhead of
			// realloc is presumably relatively high, we grow
			// faster
			extra = 3;
		}

		int newSize = minTargetSize + extra;

		// add 7 to allow for worst case byte alignment addition below:
		if (newSize + 7 < 0) {
			// int overflowed -- return max allowed array size
			return Integer.MAX_VALUE;
		}

		if (Constants.JRE_IS_64BIT) {
			// round up to 8 byte alignment in 64bit env
			switch (bytesPerElement) {
			case 4:
				// round up to multiple of 2
				return (newSize + 1) & 0x7ffffffe;
			case 2:
				// round up to multiple of 4
				return (newSize + 3) & 0x7ffffffc;
			case 1:
				// round up to multiple of 8
				return (newSize + 7) & 0x7ffffff8;
			case 8:
				// no rounding
			default:
				// odd (invalid?) size
				return newSize;
			}
		} else {
			// round up to 4 byte alignment in 64bit env
			switch (bytesPerElement) {
			case 2:
				// round up to multiple of 2
				return (newSize + 1) & 0x7ffffffe;
			case 1:
				// round up to multiple of 4
				return (newSize + 3) & 0x7ffffffc;
			case 4:
			case 8:
				// no rounding
			default:
				// odd (invalid?) size
				return newSize;
			}
		}
	}

	public static char[] grow(char[] array, int minSize) {
		assert minSize >= 0 : "size must be positive (got " + minSize
				+ "): likely integer overflow?";
		if (array.length < minSize) {
			char[] newArray = new char[oversize(minSize, 2)];
			System.arraycopy(array, 0, newArray, 0, array.length);
			return newArray;
		} else
			return array;
	}

	public static int[] grow(int[] array, int minSize) {
		assert minSize >= 0 : "size must be positive (got " + minSize
				+ "): likely integer overflow?";
		if (array.length < minSize) {
			int[] newArray = new int[oversize(minSize, 4)];
			System.arraycopy(array, 0, newArray, 0, array.length);
			return newArray;
		} else
			return array;
	}

	public static byte[] grow(byte[] array, int minSize) {
		assert minSize >= 0 : "size must be positive (got " + minSize
				+ "): likely integer overflow?";
		if (array.length < minSize) {
			byte[] newArray = new byte[oversize(minSize, 1)];
			System.arraycopy(array, 0, newArray, 0, array.length);
			return newArray;
		} else
			return array;
	}
}
