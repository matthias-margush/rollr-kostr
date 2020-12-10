package com.acmeinc.underwriting

import kostr.test.consume
import kostr.test.produce
import kostr.test.withTopology
import org.junit.jupiter.api.Test

class LoanApprovalTest {
    @Test
    fun `loan approval topology`() =
        withTopology(::loanApprovalTopology).use { topology ->
            val application = LoanApplication.generate()
            topology.produce(Topics.LoanApplication, application)
            val approval = topology.consume(Topics.LoanApproval)
            println("Result: $approval")
    }
}
