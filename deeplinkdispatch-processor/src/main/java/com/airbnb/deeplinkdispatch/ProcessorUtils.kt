package com.airbnb.deeplinkdispatch

import androidx.room.compiler.processing.XAnnotation
import androidx.room.compiler.processing.XElement
import androidx.room.compiler.processing.XTypeElement
import androidx.room.compiler.processing.isMethod
import androidx.room.compiler.processing.isTypeElement

object ProcessorUtils {
    @JvmStatic
    fun String.decapitalizeIfNotTwoFirstCharsUpperCase(): String {
        return if (this.length > 1 && this[0].isUpperCase() && this[1].isUpperCase()) {
            this
        } else {
            this.replaceFirstChar { it.lowercase() }
        }
    }

    @JvmStatic
    fun Array<String>.hasEmptyOrNullString() = this.any { it.isNullOrEmpty() }
}

fun XTypeElement.implementedInterfaces(): List<XTypeElement> {
    // Implemented interfaces of supertype (recursively)
    return (superType?.typeElement?.implementedInterfaces() ?: emptyList()) +
        // Implemented interface by this element
        getSuperInterfaceElements() +
        // Implemented interfaces the interfaces implemented by this type (recursively)
        getSuperInterfaceElements().flatMap { it.implementedInterfaces() }
}

fun XTypeElement.implementsInterfaces(fqnList: List<String>) =
    fqnList.all { interfaceFqn ->
        implementedInterfaces().any { typeElement -> typeElement.qualifiedName == interfaceFqn }
    }

fun XTypeElement.inheritanceHierarchy(): List<XTypeElement> {
    return this.superType?.typeElement?.let { it.inheritanceHierarchy() + listOf(it) }
        ?: emptyList()
}

fun XTypeElement.inheritanceHierarchyContains(fqnList: List<String>) =
    inheritanceHierarchy().any { typeElement ->
        typeElement.qualifiedName in fqnList
    }

fun XTypeElement.inheritanceHierarchyDoesNotContain(fqnList: List<String>) =
    inheritanceHierarchy().none { typeElement ->
        typeElement.qualifiedName in fqnList
    }

fun XTypeElement.directlyImplementsInterfaces(fqnList: List<String>): Boolean {
    return fqnList.all { interfaceFqn ->
        getSuperInterfaceElements().any { typeElement -> typeElement.qualifiedName == interfaceFqn }
    }
}

inline fun <reified T> XAnnotation.getAsList(method: String): List<T> {
    val annotationValue = get(method)
    return if (annotationValue != null) {
        @Suppress("UNCHECKED_CAST")
        (annotationValue.value as? List<T>) ?: emptyList()
    } else {
        emptyList()
    }
}

/**
 * The order of symbols returns by KSP2 differs from that returned by KSP1.
 * This workaround ensure that the order of symbols is consistent across both KSP versions.
 *
 * @see [https://github.com/google/ksp/issues/1719]
 * */
internal fun <T : XElement> Collection<T>.ensureConsistentOrdering(): Sequence<T> {
    return this.asSequence()
        .sortedWith(
            compareBy { element ->
                when {
                    element.isTypeElement() -> 0
                    element.isMethod() -> 1
                    else -> 2
                }
            }
        )
}