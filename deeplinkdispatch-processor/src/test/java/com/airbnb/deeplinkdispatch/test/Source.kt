package com.airbnb.deeplinkdispatch.test

import com.tschuchort.compiletesting.SourceFile
import java.io.File

sealed class Source {
    abstract val contents: String
    abstract fun toKotlinSourceFile(srcRoot: File): SourceFile

    class JavaSource(private val qName: String, override val contents: String) : Source() {
        override fun toKotlinSourceFile(srcRoot: File): SourceFile {
            // Create in-memory SourceFile instead of writing to disk
            val fileName = qName.substringAfterLast(".") + ".java"
            return SourceFile.java(fileName, contents.trimIndent())
        }
    }

    class KotlinSource(
        private val relativePath: String,
        override val contents: String
    ) : Source() {

        override fun toKotlinSourceFile(srcRoot: File): SourceFile {
            // Create in-memory SourceFile instead of writing to disk
            val fileName = relativePath.substringAfterLast("/")
            return SourceFile.kotlin(fileName, contents.trimIndent())
        }
    }
}
