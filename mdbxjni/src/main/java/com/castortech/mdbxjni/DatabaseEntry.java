package com.castortech.mdbxjni;

public class DatabaseEntry {
	/* package */ byte[] data;
	/* package */ int offset = 0;
	/* package */ int size = 0;

	/*
	 * IGNORE is used to avoid returning data that is not needed. It may not be used as the key DBT in a put
	 * since the PARTIAL flag is not allowed; use UNUSED for that instead.
	 */

	/* package */static final DatabaseEntry IGNORE = new DatabaseEntry();
	/* package */static final DatabaseEntry UNUSED = new DatabaseEntry();
	/* package */ static final int INT32SZ = 4;

	public DatabaseEntry() {
	}

	public DatabaseEntry(final byte[] data) {
		this.data = data;
		if (data != null) {
			this.size = data.length;
		}
	}

	public DatabaseEntry(final byte[] data, final int offset, final int size) {
		this.data = data;
		this.offset = offset;
		this.size = size;
	}

	public byte[] getData() {
		// if (data == null)
		// return null;
		//
		// System.arraycopy(data, offset, data, 0, size);
		return data;
	}

	public void setData(final byte[] data, final int offset, final int size) {
		this.data = data;
		this.offset = offset;
		this.size = size;
	}

	public void setData(final byte[] data) {
		setData(data, 0, (data == null) ? 0 : data.length);
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(final int offset) {
		this.offset = offset;
	}

	public int getSize() {
		return size;
	}

	public void setSize(final int size) {
		this.size = size;
	}

	public boolean equals(Object o) {
		if (!(o instanceof DatabaseEntry)) {
			return false;
		}
		DatabaseEntry e = (DatabaseEntry)o;
		if (data == null && e.data == null) {
			return true;
		}
		if (data == null || e.data == null) {
			return false;
		}
		if (size != e.size) {
			return false;
		}
		for (int i = 0; i < size; i += 1) {
			if (data[offset + i] != e.data[e.offset + i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns a hash code based on the data value.
	 */
	public int hashCode() {
		int hash = 0;
		if (data != null) {
			for (int i = 0; i < size; i += 1) {
				hash += data[offset + i];
			}
		}
		return hash;
	}
}
