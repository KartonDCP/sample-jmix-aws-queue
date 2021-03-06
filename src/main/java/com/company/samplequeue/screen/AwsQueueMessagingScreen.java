package com.company.samplequeue.screen;

import com.company.samplequeue.AppProperties;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.TextArea;
import io.jmix.ui.screen.Screen;
import io.jmix.ui.screen.Subscribe;
import io.jmix.ui.screen.UiController;
import io.jmix.ui.screen.UiDescriptor;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;

import java.util.UUID;

@UiController("AwsQueueMessagingScreen")
@UiDescriptor("aws-queue-messaging-screen.xml")
public class AwsQueueMessagingScreen extends Screen {
    private static final String messageGroupId = "1337";
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(AwsQueueMessagingScreen.class);

    @Autowired
    private TextArea<String> messageField;
    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;
    @Autowired
    private AppProperties appProperties;

    @Subscribe("sendButton")
    public void onSendButtonClick(Button.ClickEvent event) {
        MessageBuilder<String> messageBuilder = MessageBuilder.withPayload(messageField.getRawValue());
        if (appProperties.getQueueName().endsWith(".fifo")) {
            messageBuilder.setHeader("message-group-id", messageGroupId);
            messageBuilder.setHeader("message-deduplication-id", UUID.randomUUID().toString());
        }

        queueMessagingTemplate.send(appProperties.getQueueName(), messageBuilder.build());
        log.info("Message send to the Amazon Queue");
    }

}