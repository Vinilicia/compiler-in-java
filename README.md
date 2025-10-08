# Compiler in Java

A simple compiler written in Java.

## Prerequisites

To build and run this project, you will need a Java Development Kit (JDK) version 21 or later.

### Arch Linux

To install the latest JDK on Arch Linux, run the following command:

```bash
sudo pacman -S jdk-openjdk
```

### Other Linux Distributions (Debian/Ubuntu)

```bash
sudo apt update
sudo apt install default-jdk
```

### macOS

You can use Homebrew to install the JDK:

```bash
brew install openjdk
```

### Windows

You can download the latest JDK from the official Oracle website or use a package manager like Chocolatey:

```bash
choco install openjdk
```

## Building

To build the project, run the following command from the root directory:

```bash
./gradlew build
```

This will compile the source code and create an executable JAR file in the `app/build/libs` directory.

## Running

To run the application, you first need to specify the input file in the `app/src/main/java/org/App.java` file. Edit the `inputFile` variable to the name of the file you want to process. The file must be located in the `app/src/main/resources` directory.

For example, to process the `soma.p` file, you would change the `App.java` file to look like this:

```java
public class App {

    public static void main(String[] args) {
        try {
            String inputFile = "soma.p"; // Change this line
            String outputFile = inputFile.replace(".p", "_tokens.json");

            SourceReader reader = new SourceReader(inputFile);
//...
```

Once you have set the input file, you can run the application using the `run` task from Gradle:

```bash
./gradlew run
```

This will generate a token file in the `output` directory.

## Usage

The compiler will process the file specified in `app/src/main/java/org/App.java`. For the input file, the compiler will tokenize it and save the resulting tokens to a JSON file.

### Output

The generated JSON files are saved in the `output` directory, which is created in the root of the project if it doesn't exist. The output file will have the same name as the input file, but with a `_tokens.json` extension.

For example, running the compiler on `soma.p` will create an `output/soma_tokens.json` file.

### Examples

The `app/src/main/resources` directory contains several example source files that you can use to test the compiler:

*   `calculadora.p`
*   `lexical_error.p`
*   `loop_simples.p`
*   `media.p`
*   `soma.p`
*   `test.p`
*   `tokens.p`
