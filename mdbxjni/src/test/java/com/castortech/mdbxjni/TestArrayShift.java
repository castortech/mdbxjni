package com.castortech.mdbxjni;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

@SuppressWarnings("nls")
public class TestArrayShift {
	boolean silent = false;
	int padSize = 2;

	@Test
	public void testArrayDeltas() throws IOException {
		List<Integer> baseList = new ArrayList<>();
		List<Integer> adjustedList = new ArrayList<>();

		int removedCnt = 0;
		baseList.addAll(Stream.iterate(0, n -> n + 1).limit(10).collect(Collectors.toList()));
		adjustedList.addAll(baseList);

		dumpLists(baseList, adjustedList);

		int pos = add(baseList, adjustedList, adjustedList.size());

		pos = remove(baseList, adjustedList, 5);
		removedCnt++;

		pos = remove(baseList, adjustedList, 7);
		removedCnt++;

		pos = add(baseList, adjustedList, adjustedList.size());

		pos = remove(baseList, adjustedList, 2);
		removedCnt++;

		pos = remove(baseList, adjustedList, 2);
		removedCnt++;

		pos = add(baseList, adjustedList, adjustedList.size());

		pos = add(baseList, adjustedList, adjustedList.size());

		pos = add(baseList, adjustedList, adjustedList.size());

		pos = add(baseList, adjustedList, adjustedList.size());

		pos = add(baseList, adjustedList, adjustedList.size());

		pos = remove(baseList, adjustedList, 6);
		removedCnt++;

		pos = remove(baseList, adjustedList, 1);
		removedCnt++;

		pos = remove(baseList, adjustedList, 8);
		removedCnt++;

		if (silent) {
			System.out.println("");
			System.out.println("Final Results");
			dumpLists(baseList, adjustedList);
		}
	}

	private void dumpLists(List<Integer> baseList, List<Integer> adjustedList) {
		String baseDump = baseList.stream()
				.map(val -> ctString.pad(val.toString(), padSize, true))
				.collect(Collectors.joining(", "));

		StringBuilder sb = new StringBuilder();
		int totOffset = 0;
		for (int i = 0; i < adjustedList.size(); i++) {
			if (i > 0) {
				sb.append(", ");
			}

			Integer idxInBase = adjustedList.get(i);
			int offset = idxInBase - totOffset - i;
			while (offset > 0) {
				sb.append(ctString.pad("", padSize, true));
				sb.append(", ");
				offset--;
				totOffset++;
			}
			sb.append(ctString.pad(idxInBase.toString(), padSize, true));
		}

		System.out.println("BaseList    [" + ctString.pad(Integer.toString(baseList.size()), padSize, true) + "]:" + baseDump);
		System.out.println("AdjustedList[" + ctString.pad(Integer.toString(adjustedList.size()), padSize, true) + "]:" + sb.toString());
		dumpRealAdjusted(adjustedList);
	}

	private void dumpRealAdjusted(List<Integer> adjustedList) {
		String baseDump = adjustedList.stream()
				.map(val -> ctString.pad(val.toString(), padSize, true))
				.collect(Collectors.joining(", "));

		System.out.println("RealAdjusted[" + ctString.pad(Integer.toString(adjustedList.size()), padSize, true) + "]:" + baseDump);
	}

	private int add(List<Integer> baseList, List<Integer> adjustedList, int index) {
		checkState(index == adjustedList.size());
		int addIdx = baseList.size();
		baseList.add(addIdx);
		adjustedList.add(addIdx);
		int pos = baseList.size() - 1;

		if (!silent) {
			System.out.println("");
			System.out.println("Added " + index + " which is at index: " + pos + " in BaseList");
			dumpLists(baseList, adjustedList);
		}
		return pos;
	}

	private int remove(List<Integer> baseList, List<Integer> adjustedList, int index) {
		checkState(index <= adjustedList.size());

		int removedIdx = adjustedList.remove(index);
		if (!silent) {
			System.out.println("");
			System.out.println("Removed " + index + " which is at index: " + removedIdx + " in BaseList");
			dumpLists(baseList, adjustedList);
		}
		return removedIdx;
	}
}