package com.acmeinc

import kostr.serde.EDNSerde
import kostr.Topic
import kostr.serde.AvroSerde
import kostr.serde.StringSerde

object Topics {
    val LoanApplication = Topic<String, LoanApplication>(
        "loan-application",
        StringSerde{},
        EDNSerde { LoanApplication(it) }
    )

    val LoanApproval = Topic<String, LoanApproval>(
        "loan-approval",
        StringSerde{},
        AvroSerde{}
    )
}