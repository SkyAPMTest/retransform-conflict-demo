# retransform-conflict-demo
Demonstration codes of an re-transform agent, which can't work with the SkyWalking version &lt; 8.1

## Test instruction

This application dynamically attaches a java agent through ByteBuddy, obtains the `Instrumentation` object, and calls the `instrumentation.retransformClasses()` to obtain the bytecode of target class.  
The project is copied from this [demo](https://github.com/hengyunabc/skywalking-error-demo), with some modifications.

```java
	public static void reTransform(Instrumentation instrumentation) throws UnmodifiableClassException {
		ClassFileTransformer transformer = new ClassFileTransformer() {
			@Override
			public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
					ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
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
```


## Run without SkyWalking agent

```
before retransform:
com.example.demo.TestController

retransform:

after retransform:
com.example.demo.TestController

check retransform classes:
retransform classes successful.
```

## Run with SkyWalking agent

```
java -javaagent:/apache-skywalking-apm-bin/agent/skywalking-agent.jar -jar retransform-conflict-demo.jar
```


```
before retransform:
com.example.demo.TestController$auxiliary$ckZJlKPI
com.example.demo.TestController
com.example.demo.TestController$auxiliary$DNtevU4I

retransform:
java.lang.ClassFormatError
        at java.instrument/sun.instrument.InstrumentationImpl.retransformClasses0(Native Method)
        at java.instrument/sun.instrument.InstrumentationImpl.retransformClasses(InstrumentationImpl.java:167)
        at com.example.demo.DemoApplication.reTransform(DemoApplication.java:69)
        at com.example.demo.DemoApplication.main(DemoApplication.java:30)
        at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
        at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.base/java.lang.reflect.Method.invoke(Method.java:566)
        at org.springframework.boot.loader.MainMethodRunner.run(MainMethodRunner.java:49)
        at org.springframework.boot.loader.Launcher.launch(Launcher.java:109)
        at org.springframework.boot.loader.Launcher.launch(Launcher.java:58)
        at org.springframework.boot.loader.JarLauncher.main(JarLauncher.java:88)

after retransform:
com.example.demo.TestController$auxiliary$twNcBbRq
com.example.demo.TestController$auxiliary$ckZJlKPI
com.example.demo.TestController
com.example.demo.TestController$auxiliary$DNtevU4I

check retransform classes:
retransform classes not equal.

```

## Run with SkyWalking agent and enable class cache feature

```
java -Dskywalking.agent.is_cache_enhanced_class=true -javaagent:/apache-skywalking-apm-bin/agent/skywalking-agent.jar -jar retransform-conflict-demo.jar
```

```
before retransform:
com.example.demo.TestController$auxiliary$lNjbfE8J
com.example.demo.TestController
com.example.demo.TestController$auxiliary$vmhaXoD8

retransform:

after retransform:
com.example.demo.TestController$auxiliary$lNjbfE8J
com.example.demo.TestController
com.example.demo.TestController$auxiliary$vmhaXoD8

check retransform classes:
retransform classes successful.

```