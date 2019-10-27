package jmaker.parser;

import java.util.Objects;

public class Span {

	public final int startLine;
	public final int startColumn;
	private int endLine;
	private int endColumn;
	private boolean endSet;

	public Span(int startLine, int startColumn) {
		this.startLine = startLine;
		this.startColumn = startColumn;
	}

	public int getEndLine() {
		return endLine;
	}

	public int getEndColumn() {
		return endColumn;
	}

	public void setEnd(int endColumn) {
		setEnd(startLine, endColumn);
	}

	public void setEnd(int endLine, int endColumn) {
		if (endSet) {
			throw new RuntimeException("Attempted to modify Span!");
		}
		this.endLine = endLine;
		this.endColumn = endColumn;
	}

	@Override
	public String toString() {
		if (startLine == endLine) {
			if (startColumn == endColumn - 1) {
				return startLine + ":" + startColumn;
			} else {
				return startLine + ":" + startColumn + "-" + endColumn;
			}
		} else {
			return startLine + ":" + startColumn + " - " + endLine + ":" + endColumn;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(endColumn, endLine, endSet, startColumn, startLine);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Span)) {
			return false;
		}
		Span other = (Span) obj;
		return endColumn == other.endColumn && endLine == other.endLine && endSet == other.endSet && startColumn == other.startColumn && startLine == other.startLine;
	}
}
