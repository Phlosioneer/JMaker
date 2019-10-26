package jmaker.runtime;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import jmaker.interpreter.ArrayValue;
import jmaker.interpreter.BooleanValue;
import jmaker.interpreter.DataType;
import jmaker.interpreter.ExpressionValue;
import jmaker.interpreter.Memory;
import jmaker.interpreter.StringValue;

public class FileSystemFunctions {
	private static char pathSeparator;
	private static boolean pathSeparatorInitialized = false;

	private static NativeFunction[] functions = {
		new NativeFunction("isValidPath", FileSystemFunctions::isValidPath),
		new NativeFunction("isFile", FileSystemFunctions::isFile),
		new NativeFunction("isDirectory", FileSystemFunctions::isDirectory),
		new NativeFunction("getExtension", FileSystemFunctions::getExtension),
		new NativeFunction("splitPath", FileSystemFunctions::splitPath),
		new NativeFunction("getParentDir", FileSystemFunctions::getParentDir),
		new NativeFunction("getAbsolutePath", FileSystemFunctions::getAbsolutePath),
		new NativeFunction("getChildren", FileSystemFunctions::getChildren),
		new NativeFunction("canRead", FileSystemFunctions::canRead),
		new NativeFunction("canWrite", FileSystemFunctions::canWrite),
		new NativeFunction("getCurrentWorkingDir", FileSystemFunctions::getCurrentWorkingDir),
		new NativeFunction("getPathSeparator", FileSystemFunctions::getPathSeparator),
		new NativeFunction("joinPath", FileSystemFunctions::joinPath)
	};

	private static char getPathSeparatorFromRuntime() {
		if (pathSeparatorInitialized) {
			return pathSeparator;
		}
		pathSeparator = System.getProperty("file.separator").charAt(0);
		pathSeparatorInitialized = true;
		return pathSeparator;
	}

	public static void registerAll(Memory memory) {
		for (var func : functions) {
			memory.set(func.symbolName, func);
		}
	}

	public static ExpressionValue isValidPath(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		var pathExpr = args[0];
		if (pathExpr.getType() != DataType.String) {
			throw new ArgTypeException(args);
		}

		var path = stringToPath(pathExpr.asString());
		return new BooleanValue(path != null);
	}

	public static ExpressionValue isFile(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		var pathExpr = args[0];
		if (pathExpr.getType() != DataType.String) {
			throw new ArgTypeException(args);
		}

		var path = stringToPath(pathExpr.asString());
		return new BooleanValue(path.toFile().isFile());
	}

	public static ExpressionValue isDirectory(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		var pathExpr = args[0];
		if (pathExpr.getType() != DataType.String) {
			throw new ArgTypeException(args);
		}

		var path = stringToPath(pathExpr.asString());
		return new BooleanValue(path.toFile().isDirectory());
	}

	public static ExpressionValue getExtension(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		var pathExpr = args[0];
		if (pathExpr.getType() != DataType.String) {
			throw new ArgTypeException(args);
		}

		var path = stringToPath(pathExpr.asString());
		var filename = path.getFileName().toString();
		var extensionIndex = filename.indexOf('.');
		if (extensionIndex < 0) {
			return new StringValue("");
		}
		var extension = filename.substring(extensionIndex + 1, filename.length());
		return new StringValue(extension);
	}

	public static ExpressionValue splitPath(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		var pathExpr = args[0];
		if (pathExpr.getType() != DataType.String) {
			throw new ArgTypeException(args);
		}

		var path = stringToPath(pathExpr.asString());
		var ret = new ArrayList<ExpressionValue>();
		for (var part : path) {
			ret.add(new StringValue(part.toString()));
		}
		return new ArrayValue(ret);
	}

	public static ExpressionValue getParentDir(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		var pathExpr = args[0];
		if (pathExpr.getType() != DataType.String) {
			throw new ArgTypeException(args);
		}

		var path = stringToPath(pathExpr.asString());
		var absolutePath = path.toAbsolutePath();
		return new StringValue(absolutePath.getParent().toString());
	}

	public static ExpressionValue getAbsolutePath(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		var pathExpr = args[0];
		if (pathExpr.getType() != DataType.String) {
			throw new ArgTypeException(args);
		}

		var path = stringToPath(pathExpr.asString());
		return new StringValue(path.toAbsolutePath().toString());
	}

	public static ExpressionValue getChildren(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		var pathExpr = args[0];
		if (pathExpr.getType() != DataType.String) {
			throw new ArgTypeException(args);
		}

		var path = stringToPath(pathExpr.asString());
		var dir = path.toFile();
		if (!dir.isDirectory()) {
			return new ArrayValue(new ExpressionValue[]{});
		}

		var ret = new ArrayList<ExpressionValue>();
		for (var child : dir.list()) {
			ret.add(new StringValue(child));
		}
		return new ArrayValue(ret);
	}

	public static ExpressionValue canRead(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		var pathExpr = args[0];
		if (pathExpr.getType() != DataType.String) {
			throw new ArgTypeException(args);
		}

		var path = stringToPath(pathExpr.asString());
		return new BooleanValue(path.toFile().canRead());
	}

	public static ExpressionValue canWrite(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		var pathExpr = args[0];
		if (pathExpr.getType() != DataType.String) {
			throw new ArgTypeException(args);
		}

		var path = stringToPath(pathExpr.asString());
		return new BooleanValue(path.toFile().canWrite());
	}

	public static ExpressionValue getCurrentWorkingDir(ExpressionValue[] args) {
		if (args.length != 0) {
			throw new ArgCountException(0, args.length);
		}

		var workingDir = System.getProperty("user.dir");
		return new StringValue(workingDir);
	}

	public static ExpressionValue getPathSeparator(ExpressionValue[] args) {
		if (args.length != 0) {
			throw new ArgCountException(0, args.length);
		}

		return new StringValue(Character.toString(pathSeparator));
	}

	public static ExpressionValue joinPath(ExpressionValue[] args) {
		if (args.length == 0) {
			throw new ArgCountException(1, args.length);
		}

		String[] pathParts;
		if (args.length == 1 && args[0].getType() == DataType.Array) {
			var array = ((ArrayValue) args[0]).elements;
			pathParts = new String[array.length];
			for (int i = 0; i < array.length; i++) {
				var stringExpr = array[i];
				if (stringExpr.getType() != DataType.String) {
					throw new RuntimeException("joinPath expects an array of strings, found " + stringExpr.getType());
				}
				pathParts[i] = stringExpr.asString();
			}
		} else {
			pathParts = new String[args.length];
			for (int i = 0; i < args.length; i++) {
				var stringExpr = args[i];
				if (stringExpr.getType() != DataType.String) {
					throw new ArgTypeException(args);
				}
				pathParts[i] = stringExpr.asString();
			}
		}

		// Go through a bunch of processing here to guarantee that one and only one
		// path separator is placed between path arguments, regardless of whether arguments
		// are prefixed or postfixed with a path separator.
		//
		// The first step is to strip out all prefix and suffix path separators. The one
		// corner case we have to be careful about is a combined path starting with "/" on
		// unix. That indicates the root directory. So we treat the first non-empty string
		// as special.
		boolean firstPathIsRoot = false;
		boolean firstPathFound = false;
		for (int i = 0; i < pathParts.length; i++) {
			var trimmed = pathParts[i].trim();

			if (trimmed.length() == 0) {
				pathParts[i] = "";
				// This doesn't count as the first path for root finding.
				continue;
			}

			String replaced;
			if (getPathSeparatorFromRuntime() == '\\') {
				replaced = trimmed.replace('\\', getPathSeparatorFromRuntime());
			} else {
				replaced = trimmed;
			}

			if (!firstPathFound) {
				firstPathFound = true;
				if (replaced.charAt(0) == getPathSeparatorFromRuntime()) {
					firstPathIsRoot = true;
				} else {
					firstPathIsRoot = false;
				}
			}

			String suffixRemoved;
			if (replaced.charAt(replaced.length() - 1) == getPathSeparatorFromRuntime()) {
				suffixRemoved = replaced.substring(0, replaced.length() - 1);
			} else {
				suffixRemoved = replaced;
			}

			if (suffixRemoved.length() == 0) {
				pathParts[i] = "";
				continue;
			}

			String prefixRemoved;
			if (replaced.charAt(0) == getPathSeparatorFromRuntime()) {
				prefixRemoved = suffixRemoved.substring(1);
			} else {
				prefixRemoved = suffixRemoved;
			}

			pathParts[i] = prefixRemoved;
		}

		// Glue all the parts together.
		// Technically, we could have figured out the total length of the string buffer
		// while we were trimming and iterating above. Premature optimization is the root
		// of all evil, though, so we don't do that unless it actually makes a difference.
		var fullPath = new StringBuilder();
		if (firstPathIsRoot) {
			fullPath.append(getPathSeparatorFromRuntime());
		}
		boolean appendedFirstPart = false;
		for (var part : pathParts) {
			if (part.length() == 0) {
				continue;
			}
			if (appendedFirstPart) {
				fullPath.append(getPathSeparatorFromRuntime());
			} else {
				appendedFirstPart = true;
			}

			fullPath.append(part);
		}

		return new StringValue(fullPath.toString());
	}

	private static Path stringToPath(String path) {
		try {
			return Paths.get(path);
		} catch (InvalidPathException e) {
			return null;
		}
	}
}
