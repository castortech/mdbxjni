package com.castortech.mdbxjni;

public class RamInfo {
	private final long pageSize;
	private final long totalPages;
	private final long availPages;

	public RamInfo(long pageSize, long totalPages, long availPages) {
		super();
		this.pageSize = pageSize;
		this.totalPages = totalPages;
		this.availPages = availPages;
	}

	public long getPageSize() {
		return pageSize;
	}

	public long getTotalPages() {
		return totalPages;
	}

	public long getAvailPages() {
		return availPages;
	}

	@SuppressWarnings("nls")
	@Override
	public String toString() {
		return "RamInfo [" +
				"pageSize=" + pageSize +
				", totalPages=" + totalPages +
				", availPages=" + availPages +
				"]";
	}
}