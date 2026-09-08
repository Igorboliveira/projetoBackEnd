import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            String apiKey = "AQ.Ab8RN6INntQhXtsIbhGhmQlqCRaUU4OsQeh5LHlASoaHlpx7rQ";
            String apiKeyLlama = "llx-rLBEt69o3L3W1xa690b8HLwrWslSMtUYQmEdLx3tMT0F6dZv";

            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.7-flash:generateContent?key=" + apiKey;
            String urlLlama = "https://api.cloud.llamaindex.ai" + apiKeyLlama;

            Scanner scanner = new Scanner(System.in);
            HttpClient client = HttpClient.newHttpClient();

            System.out.println("Cole a mensagem suspeita descobrir se é golpe:");
            String mensagemUsuario = scanner.nextLine();

            String textoLimpo = mensagemUsuario.replace("\"", "\\\"").replace("\n", "\\n");

            String instrucao = "Você é um especialista em segurança digital. Analise a seguinte mensagem, diga se é um golpe e explique o motivo brevemente: " + textoLimpo;

            String requestBody = "{\n" +
                    "  \"contents\": [{\n" +
                    "    \"parts\":[{\"text\": \"" + instrucao + "\"}]\n" +
                    "  }],\n" +
                    "  \"generationConfig\": {\n" +
                    "    \"maxOutputTokens\": 1024,\n" +
                    "    \"temperature\": 0.7\n" +
                    "  }\n" +
                    "}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String corpoResposta = response.body();

            if (response.statusCode() != 200 && response.statusCode() != 503) {
                System.out.println("Erro na conexão! Código: " + response.statusCode());
                System.out.println("Detalhes do erro: " + corpoResposta);
                return;
            }
            else if (response.statusCode() == 503){
                System.out.println("Erro 503");
                String instrucaoLlama = "Você é um especialista em segurança digital. Analise a seguinte mensagem, diga se é um golpe e explique o motivo brevemente: " + textoLimpo;

                String requestBodyLlama = "{\n" +
                        "  \"contents\": [{\n" +
                        "    \"parts\":[{\"text\": \"" + instrucao + "\"}]\n" +
                        "  }],\n" +
                        "  \"generationConfig\": {\n" +
                        "    \"maxOutputTokens\": 1024,\n" +
                        "    \"temperature\": 0.7\n" +
                        "  }\n" +
                        "}";
                HttpRequest requestLlama = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();
                HttpResponse<String> responseLlama = client.send(request, HttpResponse.BodyHandlers.ofString());
                String corpoRespostaLlama = responseLlama.body();
                System.out.println("Alterando IA" + corpoRespostaLlama);

            }

            try {
                int inicioTexto = corpoResposta.indexOf("\"text\": \"") + 9;
                int fimTexto = corpoResposta.lastIndexOf("\"\n          }");

                String veredito = corpoResposta.substring(inicioTexto, fimTexto);
                veredito = veredito.replace("\\n", "\n").replace("\\\"", "\"");

                System.out.println("\n--- ANÁLISE DA PERGUNTA ---");
                System.out.println(veredito);

            } catch (Exception e) {
                System.out.println("\nResposta bruta: " + corpoResposta);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

//Você acabou de ganhar 600 reais, clique agora no link abaixo para resgatar!
//Você acabou de ganhar um cupom de 5% de desconto nas lojas Lebes, resgate na loja mais próxima assim que possível!
