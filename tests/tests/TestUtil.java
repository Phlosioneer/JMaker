package tests;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public abstract class TestUtil {

	public static String readFile(String filename) {
		var stream = TestUtil.class.getResourceAsStream(filename);
		var reader = new BufferedReader(new InputStreamReader(stream));
		var buffer = new StringBuilder();
		reader.lines().forEach(line->buffer.append(line + '\n'));
		var ret = buffer.toString();
		// Remove last newline.
		if (ret.length() == 0) {
			return "";
		}
		return ret.substring(0, ret.length() - 1);
	}

	public static <T> String arrayToString(T[] array) {
		StringBuilder ret = new StringBuilder();
		ret.append('[');
		if (array.length > 0) {
			ret.append(array[0].toString());
			for (int i = 1; i < array.length; i++) {
				ret.append(", ");
				ret.append(array[i].toString());
			}
		}
		ret.append(']');
		return ret.toString();
	}
}
