### RetroLambda ###

- Install JDK 8
- Define `JAVA7_HOME` and `JAVA8_HOME` gradle.properties (For system wide use, put them in .gradle/gradle.properties)

```
    JAVA8_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_xx.jdk/Contents/Home
    JAVA7_HOME='/Library/Java/JavaVirtualMachines/jdk1.7.0_xx.jdk/Contents/Home
```

- Change jdk path in Android Studio

    `Command + Shift + A` , 'Project Structure', set jdk path to jdk8
