package com.acmeinc.underwriting

import com.acmeinc.underwriting.Topics.LoanApplication
import com.acmeinc.underwriting.Topics.LoanApproval
import kostr.streams.stream
import kostr.streams.to
import org.apache.kafka.streams.StreamsBuilder

fun loanApprovalTopology(
    builder: StreamsBuilder,
) =
    builder.stream(LoanApplication)
        .mapValues(::rubberStamp) to LoanApproval

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
        app.trackingId.toString()
    )
