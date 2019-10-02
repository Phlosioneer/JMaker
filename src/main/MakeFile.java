package main;

import java.io.File;
import java.util.ArrayList;
import parser.Lexer;
import parser.Token;

public class MakeFile {
	private Ruleset rules;

	public MakeFile(File buildFile) {
		ArrayList<Token> tokens = scanFile(buildFile);
		System.out.println(tokens);
	}

	public Ruleset getRules() {
		return rules;
	}

	private static ArrayList<Token> scanFile(File file) {
		String allLines = "TEST = 2;\n\n\"foo.class\": \"foo.java\"\n\t\"javac \" + deps[0] + \" -o \" + targets[0];";
		/*
		 * try {
		 * allLines = Files.readString(file.toPath());
		 * } catch (IOException e) {
		 * // TODO Auto-generated catch block
		 * throw new RuntimeException("IOException handler not yet written in scanFile of MakeFile.", e);
		 * }
		 */
		Lexer ret = new Lexer(allLines);
		return ret.scanAll();
	}
}
