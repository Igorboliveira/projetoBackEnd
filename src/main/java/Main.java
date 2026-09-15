import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Main{
    public static void main(String[] args) {
        try {
            String apiKey = "AQ.Ab8RN6INntQhXtsIbhGhmQlqCRaUU4OsQeh5LHlASoaHlpx7rQ";
            String llamaHost = System.getenv("LLAMA_HOST");
            if (llamaHost == null || llamaHost.isBlank()) {
                llamaHost = "http://localhost:11434";
            }

            String urlGemini = "https://" +
                    "generativelanguage.googleapis.com/" +
                    "v1beta/models/" +
                    "gemini-3.7-flash:generateContent?key="
                    + apiKey;

            if (apiKey == null && llamaHost.isBlank()) {
                System.out.println("ERRO: Variáveis de ambiente GEMINI_API_KEY ou GROQ_API_KEY não encontradas.");
                return;
            }

            Scanner scanner = new Scanner(System.in);
            HttpClient client = HttpClient.newHttpClient();

            System.out.println("Cole a mensagem suspeita para descobrir se é golpe:");
            String mensagemUsuario = scanner.nextLine();

            String textoLimpo = mensagemUsuario.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
            String instrucao = "Você é um especialista em segurança digital. Analise a seguinte mensagem, diga se é um golpe e explique o motivo brevemente: " + textoLimpo;

            String requestBodyGemini = """
                {
                  "contents": [{"parts":[{"text": "%s"}]}],
                  "generationConfig": { "maxOutputTokens": 1024, "temperature": 0.7 }
                }
                """.formatted(instrucao);

            HttpRequest requestGemini = HttpRequest.newBuilder()
                    .uri(URI.create(urlGemini))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyGemini))
                    .build();

            HttpResponse<String> responseGemini = client.send(requestGemini, HttpResponse.BodyHandlers.ofString());

            if (responseGemini.statusCode() == 200) {
                processarRespostaGemini(responseGemini.body());
            } else {
                System.out.println("Gemini fora ou limitado (Status " + responseGemini.statusCode() + "), alternando para Llama...");

                String requestBodyLlama = """
                {
                    "model": "llama3.2",
                    "messages": [{"role": "user", "content": "%s"}],
                    "stream": false,
                    "options": {
                        "temperature": 0.7,
                        "num_predict": 1024
                    }
                }
                """.formatted(instrucao);

                HttpClient clientLlama =HttpClient.newHttpClient();
                HttpRequest requestLlama = HttpRequest.newBuilder()
                        .uri(URI.create(llamaHost + "/api/chat"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBodyLlama))
                        .build();

                HttpResponse<String> responseLlama = clientLlama.send(requestLlama, HttpResponse.BodyHandlers.ofString());

                if (responseLlama.statusCode() == 200) {
                    processarRespostaLlama(responseLlama.body());
                } else {
                    System.out.println("Erro também no Llama! Código: " + responseLlama.statusCode());
                    System.out.println("Detalhes: " + responseLlama.body());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processarRespostaGemini(String json) {
        try {
            int inicio = json.indexOf("\"text\": \"");
            if (inicio == -1) {
                System.out.println("\nResposta bruta Gemini: " + json);
                return;
            }
            inicio += 9;

            int fim = json.indexOf("\"", inicio);
            while (fim > 0 && json.charAt(fim - 1) == '\\') {
                fim = json.indexOf("\"", fim + 1);
            }

            String veredito = json.substring(inicio, fim)
                    .replace("\\n", "\n")
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");

            System.out.println("\n--- ANÁLISE DA PERGUNTA (GEMINI) ---");
            System.out.println(veredito);
        } catch (Exception e) {
            System.out.println("\nResposta bruta Gemini: " + json);
        }
    }

    private static void processarRespostaLlama(String json) {
        try {
            int inicio = json.indexOf("\"content\": \"");
            if (inicio == -1) {
                System.out.println("\nResposta bruta Llama: " + json);
                return;
            }
            inicio += 12;

            int fim = json.indexOf("\"", inicio);
            while (fim > 0 && json.charAt(fim - 1) == '\\') {
                fim = json.indexOf("\"", fim + 1);
            }

            String veredito = json.substring(inicio, fim)
                    .replace("\\n", "\n")
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");

            System.out.println("\n--- ANÁLISE DA PERGUNTA (LLAMA) ---");
            System.out.println(veredito);
        } catch (Exception e) {
            System.out.println("\nResposta bruta Llama: " + json);
        }
    }
}

//Você acabou de ganhar 600 reais, clique agora no link abaixo para resgatar!
//Você acabou de ganhar um cupom de 5% de desconto nas lojas Lebes, resgate na loja mais próxima assim que possível!