package com.acmeinc

import kostr.streams.*
import org.apache.kafka.streams.StreamsBuilder

fun loanApprovalTopology(builder: StreamsBuilder) {
    builder.stream(Topics.LoanApplication)
        .mapValues(::approveLoan)
        .to(Topics.LoanApproval)
}

// All applications are approved automatically!
fun approveLoan(app: LoanApplication): LoanApproval =
    LoanApproval(
        app.id.toString(),
        app.publishedBy,
        app.publishedAt,
        app.income,
        app.submittedAt,
        app.firstName,
        app.lastName,
        "Approved",
        app.trackingId.toString())
