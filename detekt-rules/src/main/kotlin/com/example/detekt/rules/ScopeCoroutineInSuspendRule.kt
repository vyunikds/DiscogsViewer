package com.example.detekt.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.getAllSuperClassifiers

private fun PsiElement.findDotQualifiedExpression(): KtDotQualifiedExpression? {
    var element: PsiElement? = this.parent
    while (element != null) {
        if (element is KtDotQualifiedExpression) return element
        element = element.parent
    }
    return null
}

class ScopeCoroutineInSuspendRule : Rule() {

    companion object {
        val ISSUE: Issue = Issue(
            "ScopeCoroutineInSuspend",
            Severity.CodeSmell,
            "Avoid launching coroutine on arbitrary scope inside suspend function",
            Debt.FIVE_MINS
        )
        private val disallowedCalls = setOf("launch", "async")
        private val scopeBuilders = setOf("coroutineScope", "supervisorScope")
        private val coroutineScopeFqn = FqName("kotlinx.coroutines.CoroutineScope")
    }

    override val issue: Issue
        get() = ISSUE

    private var insideSuspend = false
    private var insideSafeScope = 0

    override fun visitNamedFunction(function: KtNamedFunction) {
        val prev = insideSuspend
        insideSuspend = function.modifierList?.hasModifier(KtTokens.SUSPEND_KEYWORD) == true
        super.visitNamedFunction(function)
        insideSuspend = prev
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (!insideSuspend || insideSafeScope > 0) return

        if (isScopeBuilder(expression)) {
            handleScopeBuilder(expression)
            return
        }

        val dot = expression.findDotQualifiedExpression() ?: return
        val callName = expression.calleeExpression?.text ?: return
        if (callName !in disallowedCalls) return

        val receiver = dot.receiverExpression
        if (isCoroutineScope(receiver)) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(expression),
                    "Using $callName on CoroutineScope inside suspend function. Use coroutineScope or supervisorScope instead."
                )
            )
        }
    }

    private fun isScopeBuilder(expression: KtCallExpression): Boolean {
        val callName = expression.calleeExpression?.text ?: return false
        return callName in scopeBuilders
    }

    private fun handleScopeBuilder(expression: KtCallExpression) {
        val lambdaArg = expression.lambdaArguments.lastOrNull() ?: return
        val lambda = lambdaArg.getLambdaExpression() ?: return
        val block = lambda.bodyExpression ?: return
        val prev = insideSafeScope
        insideSafeScope = prev + 1
        for (child in block.children) {
            child.accept(this)
        }
        insideSafeScope = prev
    }

    private fun isCoroutineScope(receiver: org.jetbrains.kotlin.psi.KtExpression): Boolean {
        val type = bindingContext.getType(receiver) ?: return false
        val classDescriptor = type.constructor.declarationDescriptor as? ClassDescriptor ?: return false
        val fqName = classDescriptor.fqNameSafe
        if (fqName == coroutineScopeFqn) return true
        val supers = classDescriptor.getAllSuperClassifiers().toSet()
        for (superDesc in supers) {
            if (superDesc.fqNameSafe == coroutineScopeFqn) return true
        }
        return false
    }
}
