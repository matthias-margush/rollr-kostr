package com.acmeinc.underwriting

import kostr.Mapified
import java.util.*

data class LoanApplication(override val map: Map<String, Any?>) : Mapified {
    val submittedAt: Long by map
    val firstName: String by map
    val id: UUID by map
    val income: Long by map
    val lastName: String by map
    val publishedAt: Long by map
    val publishedBy: String by map
    val trackingId: UUID by map

    companion object {
        fun generate(): Pair<String, LoanApplication> {
            val id = UUID.randomUUID()
            return Pair(
                id.toString(), LoanApplication(
                    hashMapOf(
                        "submittedAt" to System.currentTimeMillis(),
                        "firstName" to "Jane",
                        "id" to id,
                        "income" to 12000,
                        "lastName" to "Smith",
                        "publishedAt" to System.currentTimeMillis(),
                        "publishedBy" to "unit-test",
                        "trackingId" to UUID.randomUUID()
                    )
                )
            )
        }
    }
}
