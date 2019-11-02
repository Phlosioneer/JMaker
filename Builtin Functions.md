# Built-in Functions

### Arrays

* `Array set(Array original, Integer index, Any newValue)`
* `Integer length(Array array)`
* `Array remove(Array original, Integer index)`
* `Array insert(Array original, Integer index, Any value)`
* `Array range(Array original, Integer start [, Integer end])`
* `Integer find(Array array, Any value [, Integer start [, Integer end]])`
* `Array findAll(Array array, Any value [, Integer start [, Integer end]])`

### Strings

* `Integer length(String string)`
* `String range(String original, Integer start [, Integer end])`
* `String toLower(String original)`
* `String toUpper(String original)`
* `String isLower(String original)`
* `String isUpper(String original)`
* `String isAlphabetic(String original)`
* `String isNumeric(String original)`
* `String isWhitespace(String original)`
* `String isAscii(String original)`
* `String trim(String original)`
* `Integer find(String string, String substring [, Integer start [, Integer end]])`
* `Array findAll(String string, String substring [, Integer start [, Integer end]])`
* `String replaceRange(String original, String newSubstring, Integer start, Integer end)`
* `String replaceFirst(String original, String pattern, String newSubstring)`
* `String replaceAll(String original, String pattern, String newSubstring)`
* `Array split(String original, String pattern)`

### Dictionaries

* `Integer length(Dict dict)`
* `Dict set(Dict original, Any key, Any newValue)`
* `Dict remove(Dict original, Any key)`
* `Array keys(Dict dict)`
* `Array values(Dict dict)`
* `Array pairs(Dict dict)` Returns an array of `[key, value]` pairs.
* `Dict subDict(Dict original, Array includeKeys)`
* `Bool contains(Dict dict, Any key)`

### Functions

* `Any call(Function function, Array args)`

### Types

* `Bool isBool(Any value)`
* `Bool isInteger(Any value)`
* `Bool isDouble(Any value)`
* `Bool isString(Any value)`
* `Bool isArray(Any value)`
* `Bool isDict(Any value)`
* `Bool isFunction(Any value)`
* `Integer parseInt(String value)`
* `Double parseDouble(String value)`
* `String toString(Any value)`

### Math

* `Integer round(Double value)`
* `Integer floor(Double value)`
* `Integer ceil(Double value)`
* `Number abs(Number value)`
* `Number max(Number value, Number value)`
* `Number min(Number value, Number value)`

### Filesystem

* `Bool isValidPath(String path)`
* `Bool isFile(String path)`
* `Bool isDirectory(String path)`
* `String getExtension(String path)`
* `Array splitPath(String path)`
* `String getParentDir(String path)`
* `String getAbsolutePath(String path)`
* `Array getChildren(String path)`
* `Bool canRead(String path)`
* `Bool canWrite(String path)`
* `String getCurrentWorkingDir()`
* `String getPathSeparator()`
* `String joinPath(Array subPaths)`
* `String joinPath(String subPath, String subPath, ...)`
TODO: * `Array findFiles(String rootDir, String pattern, Bool recurse)` replacement 
  for `wildcard`
TODO: * `Array changeExt(Array paths, String newExtension)` partial replacement for 
  patsubst
TODO: * `Array changePathParts(Array originalPaths, String oldRootPath, String newRootPath)` 
  partial replacement for patsubst

### Shell

* `Bool runCommand(String command)`
* `Dict getEnvVars()`
* `Dict runCommandFull(String command [, Dict options])`
* `print(Any value [, Bool newLine])`
* `printErr(Any value [, Bool newLine])`

Input dict: `{"stdin": String or Array, "timeout": Double}`

Output dict: `{"exitCode": Integer, "didTimeout": Bool, "stdout": Array, 
"stderr": Array, "runtime": Double}`

### JMaker

* `recurseJMaker(String path)`
* `abort(String message)`

### Misc

* `String getDate()`
* `String getTime()`
