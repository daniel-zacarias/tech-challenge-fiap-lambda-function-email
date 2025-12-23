package fiap.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import fiap.dto.Message;
import fiap.dto.SnsDefaultMessage;
import fiap.resources.EmailResource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackHandlerTest {

        @Mock
        private EmailResource emailResource;

        @Mock
        private Context context;

        @Mock
        private LambdaLogger logger;

        private ObjectMapper objectMapper;

        private FeedbackHandler handler;

        @BeforeEach
        void setUp() {
                objectMapper = new ObjectMapper();
                handler = new FeedbackHandler(emailResource, objectMapper);

                when(context.getLogger()).thenReturn(logger);
        }

        @Test
        void givenValidSqsMessage_whenHandleRequest_shouldSendEmailSuccessfully() throws Exception {
                // Arrange

                final String ExpectedContentEmailText = String.format(
                                "Feedback recebido:\n\nID: %s\nDescrição: %s\nNota: %d",
                                123,
                                "Muito bom",
                                2);

                Message feedback = new Message(
                                "RECEIVED",
                                "123",
                                "Muito bom",
                                2,
                                Instant.now().toString());

                SnsDefaultMessage sns = new SnsDefaultMessage(
                                "Notification",
                                "12345678-1234-1234-1234-123456789012",
                                "arn:aws:sns:us-east-1:123456789012:MyTopic",
                                objectMapper.writeValueAsString(feedback),
                                Instant.now().toString(),
                                "1",
                                "MyTopic",
                                "123456789012",
                                "us-east-1");

                SQSEvent.SQSMessage sqsMessage = new SQSEvent.SQSMessage();
                sqsMessage.setBody(objectMapper.writeValueAsString(sns));

                SQSEvent event = new SQSEvent();
                event.setRecords(List.of(sqsMessage));

                handler.handleRequest(event, context);

                verify(emailResource, times(1))
                                .sendEmail(
                                                any(),
                                                eq("[URGENTE] Feedback recebido"),
                                                eq(ExpectedContentEmailText),
                                                anyString());

                verify(logger).log(contains("Received message"));
        }

        @Test
        void givenInvalidJson_whenHandleRequest_shouldThrowRuntimeException() {
                SQSEvent.SQSMessage sqsMessage = new SQSEvent.SQSMessage();
                sqsMessage.setBody("invalid-json");

                SQSEvent event = new SQSEvent();
                event.setRecords(List.of(sqsMessage));

                RuntimeException exception = assertThrows(
                                RuntimeException.class,
                                () -> handler.handleRequest(event, context));

                Assertions.assertEquals(RuntimeException.class, exception.getClass());
                verify(logger).log(contains("Error parsing message"));
                verifyNoInteractions(emailResource);
        }

}