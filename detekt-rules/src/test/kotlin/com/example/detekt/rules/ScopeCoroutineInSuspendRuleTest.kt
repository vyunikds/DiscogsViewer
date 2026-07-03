package com.example.detekt.rules

import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.test.compileAndLint
import io.github.detekt.test.utils.createEnvironment
import io.gitlab.arturbosch.detekt.test.getContextForPaths
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import java.io.File

class ScopeCoroutineInSuspendRuleTest {

    private lateinit var env: io.github.detekt.test.utils.KotlinCoreEnvironmentWrapper

    @Before
    fun setUp() {
        val classpath = System.getProperty("java.class.path").split(File.pathSeparator).map(::File)
        env = createEnvironment(additionalRootPaths = classpath)
    }

    private fun lint(code: String): List<io.gitlab.arturbosch.detekt.api.Finding> {
        val ktFile = compileContentForTest(code)
        val bindingContext = env.env.getContextForPaths(listOf(ktFile))
        val rule = ScopeCoroutineInSuspendRule()
        rule.visitFile(ktFile, bindingContext)
        return rule.findings
    }

    @Test
    fun `detects launch on scope inside suspend`() {
        val code = """
            import kotlinx.coroutines.*
            class MyScope : CoroutineScope {
                override val coroutineContext: CoroutineContext = EmptyCoroutineContext
            }
            suspend fun foo() {
                MyScope().launch { }
            }
        """.trimIndent()

        val findings = lint(code)
        assertEquals(1, findings.size)
        assertTrue(findings[0].message.contains("launch"))
    }

    @Test
    fun `detects async on scope inside suspend`() {
        val code = """
            import kotlinx.coroutines.*
            class MyScope : CoroutineScope {
                override val coroutineContext: CoroutineContext = EmptyCoroutineContext
            }
            suspend fun foo() {
                MyScope().async { 42 }
            }
        """.trimIndent()

        val findings = lint(code)
        assertEquals(1, findings.size)
        assertTrue(findings[0].message.contains("async"))
    }

    @Test
    fun `does not report launch outside suspend`() {
        val code = """
            import kotlinx.coroutines.*
            class MyScope : CoroutineScope {
                override val coroutineContext: CoroutineContext = EmptyCoroutineContext
            }
            fun foo() {
                MyScope().launch { }
            }
        """.trimIndent()

        val findings = lint(code)
        assertEquals(0, findings.size)
    }

    @Test
    fun `does not report coroutineScope inside suspend`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun foo() {
                coroutineScope {
                    launch { }
                }
            }
        """.trimIndent()

        val rule = ScopeCoroutineInSuspendRule()
        rule.compileAndLint(code)
        assertEquals(0, rule.findings.size)
    }

    @Test
    fun `does not report supervisorScope inside suspend`() {
        val code = """
            import kotlinx.coroutines.*
            suspend fun foo() {
                supervisorScope {
                    launch { }
                }
            }
        """.trimIndent()

        val rule = ScopeCoroutineInSuspendRule()
        rule.compileAndLint(code)
        assertEquals(0, rule.findings.size)
    }
}
