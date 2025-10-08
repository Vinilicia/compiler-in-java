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

You can run the application in two ways:

### With File Arguments

You can pass one or more source files as arguments. The paths should be relative to the project root.

```bash
./gradlew run --args="<file1.p> <file2.p> ..."
```

For example, to run the compiler on the `soma.p` and `media.p` example files, use the following command:

```bash
./gradlew run --args="./src/main/resources/soma.p ./src/main/resources/media.p"
```

### Without Arguments

If you run the application without any arguments, it will process a default file specified in `app/src/main/java/org/App.java`.

```bash
./gradlew run
```

## Usage

The compiler takes one or more arguments, which are the paths to the source files to compile. For each input file, the compiler will tokenize it and save the resulting tokens to a JSON file.

If no arguments are provided, a default file is processed.

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
