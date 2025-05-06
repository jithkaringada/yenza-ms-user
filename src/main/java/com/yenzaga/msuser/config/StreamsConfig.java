package com.yenzaga.msuser.config;

import com.yenzaga.msuser.streams.EmailEventStream;
import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableBinding(EmailEventStream.class)
public class StreamsConfig {
}
