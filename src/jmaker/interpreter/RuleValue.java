package jmaker.interpreter;

public class RuleValue {
	public final String[] targets;
	public final String[] dependencies;
	public final String[] commands;

	public RuleValue(String[] targets, String[] dependencies, String[] commands) {
		this.targets = targets;
		this.dependencies = dependencies;
		this.commands = commands;
	}
}
