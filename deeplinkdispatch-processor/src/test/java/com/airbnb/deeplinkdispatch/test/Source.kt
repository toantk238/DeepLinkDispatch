package com.airbnb.deeplinkdispatch.test

import com.tschuchort.compiletesting.SourceFile
import java.io.File

sealed class Source {
    abstract val contents: String
    abstract fun toKotlinSourceFile(srcRoot: File): SourceFile

    class JavaSource(private val qName: String, override val contents: String) : Source() {
        override fun toKotlinSourceFile(srcRoot: File): SourceFile {
            println("srcRoot: $srcRoot")
            // Create in-memory SourceFile instead of writing to disk
            val fileName = qName.substringAfterLast(".") + ".java"
            println("fileName: $fileName")
            val outputFile = srcRoot.resolve(fileName)
            outputFile.parentFile.mkdirs()
            val outputName = outputFile.absolutePath
            println("outputName: $outputName")
            return SourceFile.java(outputFile.absolutePath, contents.trimIndent())
        }
    }

    class KotlinSource(
        private val relativePath: String,
        override val contents: String
    ) : Source() {

        override fun toKotlinSourceFile(srcRoot: File): SourceFile {
            println("srcRoot: $srcRoot")
            // Create in-memory SourceFile instead of writing to disk
            val fileName = relativePath.substringAfterLast("/")
            println("fileName: $fileName")
            val outputFile = srcRoot.resolve(fileName)
            outputFile.parentFile.mkdirs()
            val outputName = outputFile.absolutePath
            println("outputName: $outputName")
            return SourceFile.kotlin(outputFile.absolutePath, contents.trimIndent())
        }
    }
}
