package jmaker.interpreter;

import java.util.Objects;
import jmaker.parser.Expression;

public class PathValue implements ExpressionValue, Expression {
	public final String path;

	public PathValue(String path) {
		this.path = path;
	}

	@Override
	public DataType getType() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("getType of ExpressionValue not yet implemented.");
	}

	@Override
	public int hashCode() {
		return Objects.hash(path);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof PathValue)) {
			return false;
		}
		PathValue other = (PathValue) obj;
		return Objects.equals(path, other.path);
	}
}
