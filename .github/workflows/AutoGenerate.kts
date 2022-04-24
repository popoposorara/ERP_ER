#!/usr/bin/env kotlin

import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.net.URLConnection
import java.util.regex.Pattern

val branchName = if (args.contains("-b")) args[1 + args.indexOf("-b")] else "main"
main(branchName)

fun main(branchName: String) {
    val command = "find . -name *.pu"
    val result = Runtime.getRuntime().exec(command).let { process ->
        process.inputStream.use { stream ->
            stream.bufferedReader().use { reader ->
                reader.lineSequence()
                    .filter {
                        hasIncludeFunction(it)
                    }
                    .forEach {
                        witeText(it, branchName)
                    }
            }
        }
    }
    println(result)
}

fun hasIncludeFunction(filePath: String): Boolean {
    return File(filePath).useLines { lineSequences: Sequence<String> ->
        lineSequences
            .map {
                it.contains("\$include")
            }
            .contains(true)
    }
}

fun witeText(filePath: String, branchName: String) {
    val input = File(filePath)
    val output = File(filePath + "_generated")
    input.useLines { lineSequences: Sequence<String> ->
        output.bufferedWriter().apply {
            lineSequences.forEach { lineText ->
                val puFileNameMacher = Pattern.compile("\\\$include\\(\"(\\w+)\\.pu\"\\)").matcher(lineText.trim())
                if (puFileNameMacher.matches()) {
                    val puFileName = puFileNameMacher.group(1)

                    val CHARSET = "UTF-8"
                    val url = URL("https://github.com/popoposorara/ERP_ER/blob/$branchName/Entity/$puFileName.pu?raw=true")
                    val conn: URLConnection = url.openConnection()
                    val inputStream: InputStream = conn.getInputStream()
                    BufferedReader(InputStreamReader(inputStream, CHARSET)).use { reader ->
                        reader.lineSequence().forEach {
                            if (it.isEmpty() || it.contains("@startuml") || it.contains("@enduml")) return@forEach
                            write(it)
                            newLine()
                        }
                    }
                    inputStream.close()
                } else {
                    write(lineText)
                }
                newLine()
            }
            close()
        }
    }
}


