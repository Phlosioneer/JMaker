package jmaker.interpreter;

import java.util.ArrayList;
import java.util.HashMap;

public class Memory {
	private ArrayList<HashMap<String, ExpressionValue>> symbols;

	public Memory() {
		symbols = new ArrayList<>();
		pushScope();
	}

	public void pushScope() {
		symbols.add(new HashMap<>());
	}

	public void popScope() {
		symbols.remove(symbols.size() - 1);
	}

	public void set(String symbolName, ExpressionValue value) {
		// Look for a symbol to overwrite first, from most recent scope to least.
		for (int i = symbols.size() - 1; i >= 0; i--) {
			var current = symbols.get(i);
			if (current.containsKey(symbolName)) {
				current.put(symbolName, value);
				return;
			}
		}

		getCurrentScope().put(symbolName, value);
	}

	private HashMap<String, ExpressionValue> getCurrentScope() {
		return symbols.get(symbols.size() - 1);
	}

	public ExpressionValue get(String symbolName) {
		for (int i = symbols.size() - 1; i >= 0; i--) {
			var current = symbols.get(i);
			var value = current.get(symbolName);
			if (value != null) {
				return value;
			}
		}
		return null;
	}
}
