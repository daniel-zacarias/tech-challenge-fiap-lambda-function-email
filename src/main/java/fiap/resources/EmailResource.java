package fiap.resources;

import jakarta.annotation.Resource;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;

import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Message;

@Resource
public class EmailResource {

        private final SesClient ses = SesClient.builder().build();

        private static final String SOURCE = System.getenv("SOURCE_EMAIL");

        public void sendEmail(String recipient, String subject, String content) {

                Destination destinyRecipient = Destination.builder()
                                .toAddresses(recipient)
                                .build();

                Content subjectContent = Content.builder()
                                .data(subject)
                                .build();

                Content bodyContent = Content.builder()
                                .data(content)
                                .build();

                Body body = Body.builder()
                                .text(bodyContent)
                                .build();

                Message message = Message.builder()
                                .subject(subjectContent)
                                .body(body)
                                .build();

                SendEmailRequest request = SendEmailRequest.builder()
                                .destination(destinyRecipient)
                                .message(message)
                                .source(SOURCE)
                                .build();

                ses.sendEmail(request);
        }

}
