import java.net.URI;
import java.net.http.*;
import java.util.Scanner;
public class Mars{  
    public static void main(String[] args) throws Exception{
        Scanner sc=new Scanner(System.in);
        HttpClient client = HttpClient.newHttpClient();
        while(true) {
            System.out.print("You: ");
            String message=sc.nextLine();
            if(message.equalsIgnoreCase("exit")) break;
            String json="{\"model\":\"qwen2.5:1.5b\",\"prompt\":"
            + "\"" + message.replace("\\","\\\\").replace("\"","\\\"")+"\",\"stream\":false}";
            HttpRequest request=HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:11434/api/generate"))
                .header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
            HttpResponse<String> resp=client.send(request, HttpResponse.BodyHandlers.ofString());
            String body=resp.body();
            // String answer=body.split("\"resp\":\"")[1]
            //     .split("\",\"done\"")[0];
            // System.out.println("MARS: "+answer.replace("\\n","\n"));
            System.out.println("Status: "+resp.statusCode());
            System.out.println("Raw Resp: "+body);
        }
        

} }