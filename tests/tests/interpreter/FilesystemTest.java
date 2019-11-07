package tests.interpreter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tests.interpreter.TestUtil.runProgram;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import jmaker.interpreter.ArrayValue;
import jmaker.interpreter.BooleanValue;
import jmaker.interpreter.ExpressionValue;
import jmaker.interpreter.StringValue;
import jmaker.runtime.FileSystemFunctions;

class FilesystemTest {

	@Test
	void testIsFile() throws IOException {
		var path = Paths.get("test.txt");
		Files.deleteIfExists(path);
		var createdFile = Files.createFile(path);
		assertEquals(path, createdFile);
		ExpressionValue out;

		out = runProgram("out = isFile(\"" + path.toString() + "\");");
		assertEquals(new BooleanValue(true), out);
		out = runProgram("out = isDirectory(\"" + path.toString() + "\");");
		assertEquals(new BooleanValue(false), out);
		out = runProgram("out = getExtension(\"" + path.toString() + "\");");
		assertEquals(new StringValue("txt"), out);
		out = runProgram("out = canRead(\"" + path.toString() + "\");");
		assertEquals(new BooleanValue(true), out);
		out = runProgram("out = canWrite(\"" + path.toString() + "\");");
		assertEquals(new BooleanValue(true), out);

		Files.delete(path);
	}

	@Test
	void testIsDirectory() throws IOException {
		var path = Paths.get("testDir\\testFile.txt");
		Files.deleteIfExists(path);
		Files.deleteIfExists(path.getParent());
		var parentDirPath = Paths.get("testDir");
		var createdDir = Files.createDirectory(parentDirPath);
		assertEquals(parentDirPath, createdDir);
		var createdFile = Files.createFile(path);
		assertEquals(path, createdFile);
		ExpressionValue out;
		String escapedPath;
		char separator = FileSystemFunctions.getPathSeparatorFromRuntime();
		if (separator == '\\') {
			escapedPath = "testDir\\\\testFile.txt";
		} else {
			escapedPath = "testDir" + separator + "testFile.txt";
		}
		var parentDir = "testDir";

		out = runProgram("out = isFile(\"" + escapedPath + "\");");
		assertEquals(new BooleanValue(true), out);
		out = runProgram("out = isDirectory(\"" + escapedPath + "\");");
		assertEquals(new BooleanValue(false), out);
		out = runProgram("out = getExtension(\"" + escapedPath + "\");");
		assertEquals(new StringValue("txt"), out);
		out = runProgram("out = canRead(\"" + escapedPath + "\");");
		assertEquals(new BooleanValue(true), out);
		out = runProgram("out = canWrite(\"" + escapedPath + "\");");
		assertEquals(new BooleanValue(true), out);
		out = runProgram("out = splitPath(\"" + escapedPath + "\");");
		assertEquals(new ArrayValue(new ExpressionValue[]{
			new StringValue("testDir"),
			new StringValue("testFile.txt")
		}), out);
		out = runProgram("out = joinPath(\"testDir\", \"testFile.txt\");");
		assertEquals(new StringValue(path.toString()), out);
		out = runProgram("out = joinPath([\"testDir\", \"testFile.txt\"]);");
		assertEquals(new StringValue(path.toString()), out);
		out = runProgram("out = getParentDir(\"" + escapedPath + "\");");
		assertEquals(new StringValue(parentDirPath.toAbsolutePath().toString()), out);

		out = runProgram("out = isFile(\"" + parentDir + "\");");
		assertEquals(new BooleanValue(false), out);
		out = runProgram("out = isDirectory(\"" + parentDir + "\");");
		assertEquals(new BooleanValue(true), out);
		out = runProgram("out = canRead(\"" + parentDir + "\");");
		assertEquals(new BooleanValue(true), out);
		out = runProgram("out = canWrite(\"" + parentDir + "\");");
		assertEquals(new BooleanValue(true), out);
		out = runProgram("out = getChildren(\"" + parentDir + "\");");
		assertEquals(new ArrayValue(new ExpressionValue[]{
			new StringValue("testFile.txt")
		}), out);

		Files.delete(path);
		Files.delete(path.getParent());
	}
}
