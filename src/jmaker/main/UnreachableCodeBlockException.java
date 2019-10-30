package jmaker.main;

public class UnreachableCodeBlockException extends AssertionError {

	public UnreachableCodeBlockException() {
		super("This exception should be unreachable. :'(");
	}
}
