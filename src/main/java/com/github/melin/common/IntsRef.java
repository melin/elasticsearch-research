/**        
 * Copyright (c) 2013 by 苏州科大国创信息技术有限公司.    
 */
package com.github.melin.common;

/**
 * Create on @2013-12-18 @下午4:08:43
 * 
 * @author bsli@ustcinfo.com
 */
public final class IntsRef implements Comparable<IntsRef>, Cloneable {
	/** An empty integer array for convenience */
	public static final int[] EMPTY_INTS = new int[0];

	/** The contents of the IntsRef. Should never be {@code null}. */
	public int[] ints;
	/** Offset of first valid integer. */
	public int offset;
	/** Length of used ints. */
	public int length;

	/** Create a IntsRef with {@link #EMPTY_INTS} */
	public IntsRef() {
		ints = EMPTY_INTS;
	}

	/**
	 * Create a IntsRef pointing to a new array of size <code>capacity</code>.
	 * Offset and length will both be zero.
	 */
	public IntsRef(int capacity) {
		ints = new int[capacity];
	}

	/**
	 * This instance will directly reference ints w/o making a copy. ints should
	 * not be null.
	 */
	public IntsRef(int[] ints, int offset, int length) {
		this.ints = ints;
		this.offset = offset;
		this.length = length;
		assert isValid();
	}

	/**
	 * Returns a shallow clone of this instance (the underlying ints are
	 * <b>not</b> copied and will be shared by both the returned object and this
	 * object.
	 * 
	 * @see #deepCopyOf
	 */
	@Override
	public IntsRef clone() {
		return new IntsRef(ints, offset, length);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 0;
		final int end = offset + length;
		for (int i = offset; i < end; i++) {
			result = prime * result + ints[i];
		}
		return result;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (other instanceof IntsRef) {
			return this.intsEquals((IntsRef) other);
		}
		return false;
	}

	public boolean intsEquals(IntsRef other) {
		if (length == other.length) {
			int otherUpto = other.offset;
			final int[] otherInts = other.ints;
			final int end = offset + length;
			for (int upto = offset; upto < end; upto++, otherUpto++) {
				if (ints[upto] != otherInts[otherUpto]) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	/** Signed int order comparison */
	@Override
	public int compareTo(IntsRef other) {
		if (this == other)
			return 0;

		final int[] aInts = this.ints;
		int aUpto = this.offset;
		final int[] bInts = other.ints;
		int bUpto = other.offset;

		final int aStop = aUpto + Math.min(this.length, other.length);

		while (aUpto < aStop) {
			int aInt = aInts[aUpto++];
			int bInt = bInts[bUpto++];
			if (aInt > bInt) {
				return 1;
			} else if (aInt < bInt) {
				return -1;
			}
		}

		// One is a prefix of the other, or, they are equal:
		return this.length - other.length;
	}

	public void copyInts(IntsRef other) {
		if (ints.length - offset < other.length) {
			ints = new int[other.length];
			offset = 0;
		}
		System.arraycopy(other.ints, other.offset, ints, offset, other.length);
		length = other.length;
	}

	/**
	 * Used to grow the reference array.
	 * 
	 * In general this should not be used as it does not take the offset into
	 * account.
	 * 
	 * @lucene.internal
	 */
	public void grow(int newLength) {
		assert offset == 0;
		if (ints.length < newLength) {
			ints = ArrayUtil.grow(ints, newLength);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		final int end = offset + length;
		for (int i = offset; i < end; i++) {
			if (i > offset) {
				sb.append(' ');
			}
			sb.append(Integer.toHexString(ints[i]));
		}
		sb.append(']');
		return sb.toString();
	}

	/**
	 * Creates a new IntsRef that points to a copy of the ints from
	 * <code>other</code>
	 * <p>
	 * The returned IntsRef will have a length of other.length and an offset of
	 * zero.
	 */
	public static IntsRef deepCopyOf(IntsRef other) {
		IntsRef clone = new IntsRef();
		clone.copyInts(other);
		return clone;
	}

	/**
	 * Performs internal consistency checks. Always returns true (or throws
	 * IllegalStateException)
	 */
	public boolean isValid() {
		if (ints == null) {
			throw new IllegalStateException("ints is null");
		}
		if (length < 0) {
			throw new IllegalStateException("length is negative: " + length);
		}
		if (length > ints.length) {
			throw new IllegalStateException("length is out of bounds: "
					+ length + ",ints.length=" + ints.length);
		}
		if (offset < 0) {
			throw new IllegalStateException("offset is negative: " + offset);
		}
		if (offset > ints.length) {
			throw new IllegalStateException("offset out of bounds: " + offset
					+ ",ints.length=" + ints.length);
		}
		if (offset + length < 0) {
			throw new IllegalStateException(
					"offset+length is negative: offset=" + offset + ",length="
							+ length);
		}
		if (offset + length > ints.length) {
			throw new IllegalStateException(
					"offset+length out of bounds: offset=" + offset
							+ ",length=" + length + ",ints.length="
							+ ints.length);
		}
		return true;
	}
}
