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

    private final EmailResource emailResource = new EmailResource();

    private final ObjectMapper objectMapper = new ObjectMapper();

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
                messageBody = EmailPresenter.formatEmailContent(feedbackMessage);
                emailResource.sendEmail(
                        System.getenv("RECIPIENT_EMAIL"),
                        "[URGENTE] Feedback recebido",
                        messageBody);
            } catch (Exception e) {
                context.getLogger().log("Error parsing message: " + e.getMessage());
            }
        }
        return null;
    }

}
