package main;

import java.io.File;
import java.util.ArrayList;

public abstract class JMaker {
	private static final String DEFAULT_FILE_NAME = "build.jmaker";
	private static final String DEFAULT_TARGET = "all";

	public static void main(String[] args) {
		// First, open the default jmaker file.
		File buildFile = new File(DEFAULT_FILE_NAME);

		// Parse the file into rules.
		MakeFile parsedFile = new MakeFile(buildFile);
		Ruleset rules = parsedFile.getRules();

		// Parse commandline args for target name(s).
		ArrayList<String> targets = new ArrayList<>();
		if (args.length == 1) {
			targets.add(DEFAULT_TARGET);
		} else {
			targets.add(args[1]);
		}

		// Recursively build dependency graph.
		DependGraph graph = new DependGraph();
		for (String target : targets) {
			rules.buildGraph(target, graph);
		}

		// Execute rules / check dependency timestamps.
		graph.execute();
	}
}
