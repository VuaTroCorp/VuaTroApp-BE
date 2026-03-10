package fpt.ntu.vuatrovn.service;

import com.vonage.client.VonageClient;
import com.vonage.client.sms.SmsSubmissionResponse;
import com.vonage.client.sms.messages.TextMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {
    @Value("${vonage.api.key}")
    private String apiKey;

    @Value("${vonage.api.secret}")
    private String apiSecret;

    @Value("${vonage.sms.from}")
    private String from;

    public String sendSms(String phone, String messageTest) {
        VonageClient client = VonageClient.builder()
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .build();

        TextMessage message = new TextMessage(
                from,
                phone,
                messageTest
        );

        SmsSubmissionResponse response = client.getSmsClient().submitMessage(message);

        return response.getMessages().get(0).getStatus().name();
    }

}
