package com.example.demo;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import net.bytebuddy.agent.ByteBuddyAgent;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) throws InterruptedException, UnmodifiableClassException {
		SpringApplication.run(DemoApplication.class, args);
		Instrumentation instrumentation = ByteBuddyAgent.install();

		System.out.println("before retransform:");
		List<String> classesBeforeReTransform = findAllTestControllerClasses(instrumentation);
		printStrings(classesBeforeReTransform);
		System.out.println();

		System.out.println("retransform:");
		reTransform(instrumentation);
		System.out.println();

		System.out.println("after retransform:");
		List<String> classesAfterReTransform =findAllTestControllerClasses(instrumentation);
		printStrings(classesAfterReTransform);
		System.out.println();

		System.out.println("check retransform classes:");
		//check classes
		Comparator<String> comparator = new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		};
		classesBeforeReTransform.sort(comparator);
		classesAfterReTransform.sort(comparator);
		if (classesAfterReTransform.equals(classesBeforeReTransform)) {
			System.out.println("retransform classes successful.");
		} else {
			System.out.println("retransform classes not equal.");
		}
	}

	private static void reTransform(Instrumentation instrumentation) throws UnmodifiableClassException {
		ClassFileTransformer transformer = new ClassFileTransformer() {
			@Override
			public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
					ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
//				System.out.println(String.format("className=%s, classBeingRedefined=%s, classloader=%s, protectionDomain=%s, classfileBuffer=%d",
//						className, classBeingRedefined, loader, protectionDomain.getCodeSource(), classfileBuffer.length));
				return null;
			}
		};
		try {
			instrumentation.addTransformer(transformer, true);

			try {
				instrumentation.retransformClasses(TestController.class);
			} catch (Throwable e) {
				e.printStackTrace();
			}

		} finally {
			instrumentation.removeTransformer(transformer);
		}

	}

	private static List<String> findAllTestControllerClasses(Instrumentation instrumentation) {
		List<String> classNames = new ArrayList<>();
		Class<?>[] allLoadedClasses = instrumentation.getAllLoadedClasses();
		for (Class<?> clazz : allLoadedClasses) {
			if (clazz.getName().startsWith(TestController.class.getName())) {
				classNames.add(clazz.getName());
			}
		}
		return classNames;
	}

	private static void printStrings(List<String> list) {
		for (String str : list) {
			System.out.println(str);
		}
	}
}
