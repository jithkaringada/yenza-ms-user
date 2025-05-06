package com.yenzaga.msuser.service;

import com.yenzaga.common.domain.EmailStreamMessage;
import com.yenzaga.msuser.streams.EmailEventStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

@Service
public class EmailSenderService {
    private static final Logger logger = LoggerFactory.getLogger(EmailSenderService.class);

    private final EmailEventStream emailEventStream;

    public EmailSenderService(EmailEventStream emailEventStream) {
        this.emailEventStream = emailEventStream;
    }

    public void sendEmailEvent(final EmailStreamMessage emailStreamMessage) {
        logger.debug("Sending email {}", emailStreamMessage);

        MessageChannel messageChannel = emailEventStream.outboundEmailEvents();
        messageChannel.send(MessageBuilder
                            .withPayload(emailStreamMessage)
                            .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                            .build());
    }
}
