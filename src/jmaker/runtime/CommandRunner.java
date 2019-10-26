package jmaker.runtime;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

public class CommandRunner {
	// This is the data for the CMD shell.
	private static final String badChars = "\n|&<>";
	private static final String badSingleQuoteChars = "";
	private static final String badDoubleQuoteChars = "\n*?";
	private static final String[] badFirstWords = new String[]{
		"assoc", "break", "call", "cd", "chcp", "chdir", "cls", "color", "copy",
		"ctty", "date", "del", "dir", "echo", "echo.", "endlocal", "erase",
		"exit", "for", "ftype", "goto", "if", "if", "md", "mkdir", "move",
		"path", "pause", "prompt", "rd", "rem", "ren", "rename", "rmdir",
		"set", "setlocal", "shift", "time", "title", "type", "ver", "verify",
		"vol"
	};
	private static final String commentStart = ":";
	private static final String shellPath = "C:\\Windows\\System32\\cmd.exe";

	private final String[] originalCommands;
	private Process process;
	private Date startTime;

	public CommandRunner(String[] commands) {
		assert (commands.length != 0);
		originalCommands = commands;
		process = null;
		startTime = null;
	}

	public void start() {
		Path tempFilePath;
		try {
			tempFilePath = Files.createTempFile(null, ".bat");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("IOException handler not yet written in run of CommandRunner.", e);
		}

		try (var file = new FileWriter(tempFilePath.toFile())) {
			for (var line : originalCommands) {
				file.append(line);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		var builder = new ProcessBuilder(shellPath, tempFilePath.toAbsolutePath().toString());
		builder.inheritIO();
		try {
			process = builder.start();
			startTime = new Date();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("IOException handler not yet written in run of CommandRunner.", e);
		}
	}

	public boolean checkIfFinished() {
		return process.isAlive();
	}

	public boolean wasSuccess() {
		if (!process.isAlive()) {
			throw new RuntimeException();
		}
		return process.exitValue() == 0;
	}

	/*public FastCommand[] tryParseCommands() {
		var ret = new FastCommand[originalCommands.length];
		for (int i = 0; i < originalCommands.length; i++) {
			var parsedCommand = tryParseCommand(originalCommands[i]);
			if (parsedCommand == null) {
				return null;
			}
			ret[i] = parsedCommand;
		}
	}
	
	public FastCommand tryParseCommand(String command) {
		var tokens = miniLexer(command);
		if (tokens == null) {
			return null;
		}
		// Resolve the argument into a path.
		var asFile = new File(tokens.get(0));
		return new FastCommand(asFile, tokens);
	}
	
	private static ArrayList<String> miniLexer(String text) {
		var ret = new ArrayList<String>();
		var currentString = new StringBuilder();
		boolean isInSingleQuoteString = false;
		boolean isInDoubleQuoteString = false;
		boolean nextMustBeWhiteSpace = false;
		boolean escapeNextChar = false;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (isInSingleQuoteString) {
				if (badSingleQuoteChars.indexOf(c) != -1) {
					return null;
				} else if (escapeNextChar) {
					currentString.append(c);
					escapeNextChar = false;
				} else if (c == '\'') {
					ret.add(currentString.toString());
					currentString.setLength(0);
					nextMustBeWhiteSpace = true;
					isInSingleQuoteString = false;
				} else {
					currentString.append(c);
				}
			} else if (isInDoubleQuoteString) {
				if (badDoubleQuoteChars.indexOf(c) != -1) {
					return null;
				} else if (escapeNextChar) {
					currentString.append(c);
				} else if (c == '"') {
					ret.add(currentString.toString());
					currentString.setLength(0);
					isInDoubleQuoteString = false;
					nextMustBeWhiteSpace = true;
				} else if (c == '\\') {
					escapeNextChar = true;
				} else {
					currentString.append(c);
				}
			} else {
				if (badChars.indexOf(c) != -1) {
					return null;
				} else if (escapeNextChar) {
					currentString.append(c);
				} else if (c == '"') {
					if (currentString.length() != 0) {
						// Quote immediately after something else is a red flag.
						return null;
					}
					isInDoubleQuoteString = true;
				} else if (c == '\'') {
					if (currentString.length() != 0) {
						// Quote immediately after something else is a red flag.
						return null;
					}
				} else if (Character.isWhitespace(c)) {
					nextMustBeWhiteSpace = false;
					if (currentString.length() != 0) {
						ret.add(currentString.toString());
						currentString.setLength(0);
					}
				} else if (nextMustBeWhiteSpace) {
					// Something else immediately after a quote is a red flag.
					return null;
				} else {
					currentString.append(c);
				}
			}
		}
	
		if (isInDoubleQuoteString || isInSingleQuoteString || escapeNextChar) {
			// Unmatched quotes...? Let the shell handle it.
			return null;
		}
	
		var firstWord = ret.get(0);
		if (firstWord.startsWith(commentStart)) {
			return null;
		}
	
		for (var word : badFirstWords) {
			if (ret.get(0).equalsIgnoreCase(word)) {
				return null;
			}
		}
	
		if (currentString.length() != 0) {
			ret.add(currentString.toString());
		}
		return ret;
	}
	
	public class FastCommand {
		public File executable;
		public ArrayList<String> args;
	
		public FastCommand(File executable, ArrayList<String> args) {
			this.executable = executable;
			this.args = args;
		}
	}*/
}
