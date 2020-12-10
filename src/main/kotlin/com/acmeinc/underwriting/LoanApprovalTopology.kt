package com.acmeinc.underwriting

import kostr.streams.*
import org.apache.kafka.streams.StreamsBuilder

fun loanApprovalTopology(builder: StreamsBuilder) =
    builder.stream(Topics.LoanApplication)
        .mapValues(::rubberStamp) to Topics.LoanApproval

// All applications are approved automatically!
fun rubberStamp(app: LoanApplication): LoanApproval =
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
