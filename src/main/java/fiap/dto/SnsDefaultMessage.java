package fiap.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SnsDefaultMessage(
                @JsonProperty("Type") String Type,
                @JsonProperty("MessageId") String MessageId,
                @JsonProperty("TopicArn") String TopicArn,
                @JsonProperty("Message") String Message,
                @JsonProperty("Timestamp") String Timestamp,
                @JsonProperty("SignatureVersion") String SignatureVersion,
                @JsonProperty("Signature") String Signature,
                @JsonProperty("SigningCertURL") String SigningCertURL,
                @JsonProperty("UnsubscribeURL") String UnsubscribeURL) {

}
