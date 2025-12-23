package fiap.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import fiap.dto.Message;
import fiap.dto.SnsDefaultMessage;
import fiap.presenters.EmailPresenter;
import fiap.resources.EmailResource;

public class FeedbackHandler implements RequestHandler<SQSEvent, Void> {

    private final EmailResource emailResource;

    private final ObjectMapper objectMapper;

    public FeedbackHandler() {
        this(new EmailResource(), new ObjectMapper());
    }

    public FeedbackHandler(
            final EmailResource emailResource,
            final ObjectMapper objectMapper) {
        this.emailResource = emailResource;
        this.objectMapper = objectMapper;
    }

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            String messageBody = msg.getBody();

            try {
                SnsDefaultMessage snsEnvelope = objectMapper
                        .readValue(messageBody, SnsDefaultMessage.class);
                context.getLogger().log("Received message: " + messageBody);
                Message feedbackMessage = objectMapper
                        .readValue(snsEnvelope.Message(),
                                Message.class);
                String messageBodyText = EmailPresenter.formatEmailContent(feedbackMessage);
                String messageBodyHtml = EmailPresenter.formatEmailHtmlContent(feedbackMessage);
                emailResource.sendEmail(
                        System.getenv("RECIPIENT_EMAIL"),
                        "[URGENTE] Feedback recebido",
                        messageBodyText,
                        messageBodyHtml);
            } catch (Exception e) {
                context.getLogger().log("Error parsing message: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return null;
    }

}
