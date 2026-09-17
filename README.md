# Margit task manager

![Margit interface](Ui.png)

## Prerequisites

Use Java 17, as required for CS2103/T coursework. Keep `src/main/java` as the Java source root.

## Build a fat JAR

From the repository root, run:

```powershell
.\gradlew.bat shadowJar
```

Gradle compiles the application and packages its runtime dependencies into a single executable fat JAR at:

```text
build/libs/margit.jar
```

The `build/` directory is ignored by Git, so do not commit this generated JAR. Publish it as a GitHub Release asset when you need to share it.

## Run the JAR

Copy `build/libs/margit.jar` to an empty folder, open PowerShell in that folder, and run:

```powershell
java -jar margit.jar
```

Running it from an empty folder prevents its `data/Margit.txt` save file from mixing with files from another run.

## Run tests

```powershell
.\gradlew.bat test
```
