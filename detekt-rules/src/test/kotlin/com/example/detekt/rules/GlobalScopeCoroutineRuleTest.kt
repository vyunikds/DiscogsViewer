package com.example.detekt.rules

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GlobalScopeCoroutineRuleTest {

    @Test
    fun `detects launch on GlobalScope`() {
        val code = """
            import kotlinx.coroutines.*
            fun foo() {
                GlobalScope.launch { }
            }
        """.trimIndent()

        val findings = GlobalScopeCoroutineRule().compileAndLint(code)
        assertEquals(1, findings.size)
        assertTrue(findings[0].message.contains("GlobalScope.launch"))
    }

    @Test
    fun `detects async on GlobalScope`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun foo() {
                GlobalScope.async { 42 }
            }
        """.trimIndent()

        val findings = GlobalScopeCoroutineRule().compileAndLint(code)
        assertEquals(1, findings.size)
        assertTrue(findings[0].message.contains("GlobalScope.async"))
    }

    @Test
    fun `does not report launch on scope variable`() {
        val code = """
            import kotlinx.coroutines.*
            fun foo(scope: CoroutineScope) {
                scope.launch { }
            }
        """.trimIndent()

        val findings = GlobalScopeCoroutineRule().compileAndLint(code)
        assertEquals(0, findings.size)
    }

    @Test
    fun `does not report coroutineScope launch`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun foo() {
                coroutineScope {
                    launch { }
                }
            }
        """.trimIndent()

        val findings = GlobalScopeCoroutineRule().compileAndLint(code)
        assertEquals(0, findings.size)
    }

    @Test
    fun `does not report MainScope launch`() {
        val code = """
            import kotlinx.coroutines.*
            fun foo() {
                val s = MainScope()
                s.launch { }
            }
        """.trimIndent()

        val findings = GlobalScopeCoroutineRule().compileAndLint(code)
        assertEquals(0, findings.size)
    }
}
