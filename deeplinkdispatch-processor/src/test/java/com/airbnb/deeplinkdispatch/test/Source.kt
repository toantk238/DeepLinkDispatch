package com.airbnb.deeplinkdispatch.test

import com.tschuchort.compiletesting.SourceFile
import java.io.File

sealed class Source {
    abstract val contents: String
    abstract fun toKotlinSourceFile(srcRoot: File): SourceFile

    class JavaSource(private val qName: String, override val contents: String) : Source() {
        override fun toKotlinSourceFile(srcRoot: File): SourceFile {
            println("srcRoot: $srcRoot")
            val relativePath = qName.replace(".", "/") + ".java"
            val outFile = srcRoot.resolve(relativePath)
                .also { it.parentFile.mkdirs() }
            println("outFile: $outFile")
            return SourceFile.java(relativePath, contents, true)
        }
    }

    class KotlinSource(
        private val relativePath: String,
        override val contents: String
    ) : Source() {

        override fun toKotlinSourceFile(srcRoot: File): SourceFile {
            println("srcRoot: $srcRoot")
            val outFile = srcRoot.resolve(relativePath).also {
                it.parentFile.mkdirs()
            }
            println("outFile: $outFile")
            return SourceFile.kotlin(relativePath, contents, true)
        }
    }
}
