package com.itranswarp.compiler;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class JavaStringCompilerTest {

	JavaStringCompiler compiler;

	@Before
	public void setUp() throws Exception {
		compiler = new JavaStringCompiler();
	}

	static final String testClass = "										"
			+ "			package foo.bar.object;								"
			+ "			import foo.bar.interfaces.TestInterface;			"
			+ "			public class TestClass implements TestInterface {}	";

	static final String testInterface = "									"
			+ "			package foo.bar.interfaces;							"
			+ "			public interface TestInterface {}					";

	@Test
	public void compileAndLoadMultipleFiles() throws Exception {
		Map<String, String> sourceAndNames = new HashMap<>();
		sourceAndNames.put("TestClass.java", testClass);
		sourceAndNames.put("TestInterface.java", testInterface);
		Map<String, byte[]> returned = compiler.compile(sourceAndNames);

		assertNotNull(returned);
		assertTrue(returned.containsKey("foo.bar.object.TestClass"));
		assertTrue(returned.containsKey("foo.bar.interfaces.TestInterface"));

		List<Class<?>> classList = compiler.getAllClasses(returned);
		assertNotNull(classList);

		for(Class<?> clazz : classList)
		{
			assertNotNull(clazz);
			System.out.println("As Class: " + clazz.getCanonicalName());
		}
	}

}
