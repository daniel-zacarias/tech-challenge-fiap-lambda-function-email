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

        public void sendEmail(
                        final String recipient,
                        final String subject,
                        final String content,
                        final String htmlContent) {

                Destination destinyRecipient = Destination.builder()
                                .toAddresses(recipient)
                                .build();

                Content subjectContent = Content.builder()
                                .data(subject)
                                .build();

                Content bodyContentText = Content.builder()
                                .data(content)
                                .build();

                Content bodyContentHtml = Content.builder()
                                .data(htmlContent)
                                .build();

                Body body = Body.builder()
                                .html(bodyContentHtml)
                                .text(bodyContentText)
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
