package jmaker.main;

import java.io.File;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import codegen.JMakerLexer;
import codegen.JMakerParser;
import jmaker.parser.VisitorManager;

public class MakeFile {
	private Ruleset rules;

	public MakeFile(File buildFile) {
		String allLines = "TEST = 2;\n\n\"foo.class\": \"foo.java\" {\n\t> \"javac \" + deps[0] + \" -o \" + targets[0];\n}";
		var chars = CharStreams.fromString(allLines);
		var lexer = new JMakerLexer(chars);
		var tokens = new CommonTokenStream(lexer);
		var parser = new JMakerParser(tokens);

		// Get our top-level rule.
		var rootBlock = new VisitorManager().visitAll(parser);

		// Interpret the file.
		//var interpreter = new Interpreter(file);
		//interpreter.run();
	}

	public Ruleset getRules() {
		return rules;
	}
}
