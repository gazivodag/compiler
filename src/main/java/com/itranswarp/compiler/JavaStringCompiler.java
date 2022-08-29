package com.itranswarp.compiler;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

/**
 * In-memory compile Java source code as String.
 * 
 * @author michael
 */
public class JavaStringCompiler {

	JavaCompiler compiler;
	StandardJavaFileManager stdManager;
	Writer writer;

	public JavaStringCompiler(Writer writer) {
		this.compiler = ToolProvider.getSystemJavaCompiler();
		this.stdManager = compiler.getStandardFileManager(null, null, null);
		this.writer = writer;
	}

	public JavaStringCompiler() {
		this(null);
	}

	/**
	 * Compile a Java map of source files in memory.
	 *
	 * @param nameSourceMap Key being ClassFile.java, value being source code
	 * @return a map with the key being the fully qualified class name, and the value being bytecode
	 * @throws IOException
	 */
	public Map<String, byte[]> compile(Map<String, String> nameSourceMap) throws IOException {
		try (MemoryJavaFileManager manager = new MemoryJavaFileManager(stdManager)) {

//			JavaFileObject javaFileObject = manager.makeStringSource(fileName, source);

			List<JavaFileObject> jfos = new ArrayList<>();
			for (Map.Entry<String, String> entry : nameSourceMap.entrySet())
				jfos.add(manager.makeStringSource(entry.getKey(), entry.getValue()));

			CompilationTask task = compiler.getTask(writer, manager, null, null, null, jfos);

			Boolean result = task.call();

			if (result == null || !result.booleanValue()) {
				throw new RuntimeException("Compilation failed.");
			}

			return manager.getClassBytes();
		}
	}

	/**
	 * Uses an in-memory classloader to load a map of compiled classes
	 * @param classBytes The result from JavaStringCompiler.compile()
	 * @return ArrayList of Class objects
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public List<Class<?>> getAllClasses(Map<String, byte[]> classBytes) throws IOException, ClassNotFoundException {
		try (MemoryClassLoader classLoader = new MemoryClassLoader(classBytes)) {
			List<Class<?>> classes = new ArrayList<>();
			for (Map.Entry<String, byte[]> entry : classBytes.entrySet())
				classes.add(classLoader.loadClass(entry.getKey()));
			return classes;
		}
	}
}
