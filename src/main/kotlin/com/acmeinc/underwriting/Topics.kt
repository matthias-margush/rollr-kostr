package com.acmeinc.underwriting

import kostr.Topic
import kostr.serde.EDNSerde
import kostr.serde.avroSerde
import kostr.serde.stringSerde

object Topics {
    val LoanApplication = Topic<String, LoanApplication>(
        "loan-application",
        stringSerde{},
        EDNSerde { LoanApplication(it) }
    )

    val LoanApproval = Topic<String, LoanApproval>(
        "loan-approval",
        stringSerde{},
        avroSerde{}
    )
}
