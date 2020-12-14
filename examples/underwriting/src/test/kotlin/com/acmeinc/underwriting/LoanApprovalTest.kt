package com.acmeinc.underwriting

import kostr.test.*
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class LoanApprovalTest {
    @Test
    fun `loan approval topology`() =
        withTopology(::loanApprovalTopology) { topology ->
            val application = LoanApplication.generate()

            topology.produce(Topics.LoanApplication, application)

            val approval = topology.consume(Topics.LoanApproval)
            assertNotNull(approval)

            val (id, details) = approval
            println("id: $id, details: $details")
    }
}
