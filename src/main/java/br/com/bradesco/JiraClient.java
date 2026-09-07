package br.com.bradesco;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class JiraClient {
    public static void main(String[] args) throws Exception {

        
        String jiraUrl = "https://renatoarai.atlassian.net";
        String issueKey = "KAN-4";
        String email = System.getenv("JIRA_EMAIL");

        String apiToken = System.getenv("JIRA_API_TOKEN");

        String credenciais = email + ":" + apiToken;
        String autorizacao = Base64.getEncoder()
                .encodeToString(credenciais.getBytes(StandardCharsets.UTF_8));

        String json = """
            {
              "body": {
                "type": "doc",
                "version": 1,
                "content": [
                  {
                    "type": "paragraph",
                    "content": [
                      {
                        "type": "text",
                        "text": "Teste automatizado concluído com sucesso5."
                      }
                    ]
                  }
                ]
              }
            }
            """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(jiraUrl + "/rest/api/3/issue/" + issueKey + "/comment"))
                .header("Authorization", "Basic " + autorizacao)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        System.out.println("Status: " + response.statusCode());
        System.out.println("Resposta: " + response.body());

   
    }
}