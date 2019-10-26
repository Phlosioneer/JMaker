package jmaker.runtime;

public class ArgCountException extends RuntimeException {

	public ArgCountException(int expectedArgCount, int actualArgCount) {
		super(formatError(new int[]{
			expectedArgCount
		}, actualArgCount));
	}

	public ArgCountException(int[] expectedArgCounts, int actualArgCount) {
		super(formatError(expectedArgCounts, actualArgCount));
	}

	private static String formatError(int[] expectedArgCount, int actualArgCount) {
		assert (expectedArgCount != null);
		assert (expectedArgCount.length > 0);
		StringBuilder ret = new StringBuilder();
		ret.append("expected ");
		ret.append(expectedArgCount[0]);
		for (int i = 1; i < expectedArgCount.length - 1; i++) {
			ret.append(", ");
			ret.append(expectedArgCount[i]);
		}
		if (expectedArgCount.length > 1) {
			ret.append(" or ");
			ret.append(expectedArgCount[expectedArgCount.length - 1]);
		}

		ret.append(" arguments; found ");
		ret.append(actualArgCount);
		return ret.toString();
	}
}
