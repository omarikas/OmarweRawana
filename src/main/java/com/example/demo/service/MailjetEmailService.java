package com.example.demo.service;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Emailv31;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MailjetEmailService {

    private String apiKey="bf7324106d8d725b2b780f1ddd3cd7c4";

    private String apiSecret="50ac48b075ec8d736ae6feb37790a7c4";

    private String fromEmail="omarrrr240@gmail.com";

    private String fromName="omar sayed";
 public boolean sendEmail(String toEmail, String subject, String textContent) {
        try {
            ClientOptions options = ClientOptions.builder()
                    .apiKey(apiKey)
                    .apiSecretKey(apiSecret)
                    .build();

            MailjetClient client = new MailjetClient(options);

            MailjetRequest request = new MailjetRequest(Emailv31.resource)
                    .property(Emailv31.MESSAGES, new JSONArray()
                            .put(new JSONObject()
                                    .put(Emailv31.Message.FROM, new JSONObject()
                                            .put("Email", fromEmail)
                                            .put("Name", fromName))
                                    .put(Emailv31.Message.TO, new JSONArray()
                                            .put(new JSONObject()
                                                    .put("Email", toEmail)))
                                    .put(Emailv31.Message.SUBJECT, subject)
                                    .put(Emailv31.Message.TEXTPART, textContent)
                            )
                    );

            MailjetResponse response = client.post(request);

            System.out.println("Status: " + response.getStatus());
            System.out.println("Response: " + response.getData());

            return response.getStatus() == 200;
        } catch (Exception e) {

            System.err.println(textContent);
            return false;
        }
    }
}
