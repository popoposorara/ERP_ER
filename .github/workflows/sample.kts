#!/usr/bin/env kotlin

import java.io.File

val input = File("README.md") // Assuming you ran checkout before
val output = File("result.txt")
val readmeFirstLine = input.readLines().first()
// output.writeText(readmeFirstLine)

val command = "pwd"
val result = Runtime.getRuntime().exec(
    command,
    null,
    File(".")
).let { process ->
    process.inputStream.use { stream ->
        stream.bufferedReader().use { it.readText() }
    }
}
println(result)