package com.acmeinc

import kostr.test.consume
import kostr.test.produce
import kostr.test.mocktop
import kotlin.test.Test

class LoanApprovalTest {
    @Test
    fun `loan approval topology`() =
        mocktop(::loanApprovalTopology).use { topology ->
            val application = LoanApplication.generate()
            topology.produce(Topics.LoanApplication, application)
            val approval = topology.consume(Topics.LoanApproval)
            println("Result: $approval")
    }
}
