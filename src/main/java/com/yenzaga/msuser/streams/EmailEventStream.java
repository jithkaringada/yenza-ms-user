package com.yenzaga.msuser.streams;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface EmailEventStream {
    String OUTPUT_EMAILS_TO_BE_SENT = "emails-to-be-sent-out-stream";

    @Output(OUTPUT_EMAILS_TO_BE_SENT)
    MessageChannel outboundEmailEvents();
}
