package com.acmeinc

import kostr.test.consume
import kostr.test.produce
import kostr.test.testTopology
import kotlin.test.Test
import kotlin.test.assertEquals

class LoanApprovalTest {
    @Test
    fun `loan approval topology`() {
        testTopology(::loanApprovalTopology).use { topology ->
            val application = LoanApplication.generate()
            topology.produce(Topics.LoanApplication, application)
            val approval = topology.consume(Topics.LoanApproval)
            println("Result: $approval")
        }
    }
}
