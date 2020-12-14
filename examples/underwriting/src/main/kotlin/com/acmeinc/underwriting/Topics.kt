package com.acmeinc.underwriting

import kostr.topic.Topic
import kostr.serde.EDNSerde
import kostr.streams.serdes.avro.AvroSerde
import org.apache.kafka.common.serialization.Serdes

object Topics {
    val LoanApplication = Topic<String, LoanApplication>(
        "loan-application",
        Serdes.String(),
        EDNSerde { LoanApplication(it) }
    )

    val LoanApproval = Topic<String, LoanApproval>(
        "loan-approval",
        Serdes.String(),
        AvroSerde<LoanApproval>(),
    )
}
