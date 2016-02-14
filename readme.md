# Define

**Define** is a system wide dictionary for android. It shows the meaning of a copied word in a floating layout.

### RetroLambda ###

The project uses RetroLambda to make life simpler while using RxJava. Follow these instructions to set it up.

- Install JDK 8
- Define `JAVA7_HOME` and `JAVA8_HOME` gradle.properties (For system wide use, put them in .gradle/gradle.properties)

```
    JAVA8_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_xx.jdk/Contents/Home
    JAVA7_HOME='/Library/Java/JavaVirtualMachines/jdk1.7.0_xx.jdk/Contents/Home
```

- Change jdk path in Android Studio

    `Command + Shift + A` , 'Project Structure', set jdk path to jdk8

License
-------

    Copyright 2015 Workarounds

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
