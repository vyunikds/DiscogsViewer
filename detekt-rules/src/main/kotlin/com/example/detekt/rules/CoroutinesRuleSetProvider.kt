package com.example.detekt.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

class CoroutinesRuleSetProvider : RuleSetProvider {

    override val ruleSetId: String = "custom-coroutines"

    override fun instance(config: Config): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            GlobalScopeCoroutineRule(),
            ScopeCoroutineInSuspendRule()
        )
    )
}
