package com.example.detekt.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression

private fun PsiElement.findParentDot(): KtDotQualifiedExpression? {
    var element: PsiElement? = this.parent
    while (element != null) {
        if (element is KtDotQualifiedExpression) return element
        element = element.parent
    }
    return null
}

class GlobalScopeCoroutineRule : Rule() {

    companion object {
        val ISSUE: Issue = Issue(
            "GlobalScopeCoroutine",
            Severity.CodeSmell,
            "Avoid using GlobalScope for launch/async",
            Debt.FIVE_MINS
        )
        private val disallowedCalls = setOf("launch", "async")
    }

    override val issue: Issue
        get() = ISSUE

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        val dot = expression.findParentDot() ?: return
        val receiverName = dot.receiverExpression.text
        if (receiverName != "GlobalScope") return

        val callName = expression.calleeExpression?.text ?: return
        if (callName !in disallowedCalls) return

        report(
            CodeSmell(
                issue,
                Entity.from(expression),
                "GlobalScope.$callName used. Avoid using GlobalScope for coroutines."
            )
        )
    }
}
