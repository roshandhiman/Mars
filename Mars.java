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
            String SysProm="You are MARS, created by sir roshanpreet singh. "
                + "You can use this tool: open_url | url (opens a website in the browser). "
                + "If the user wants an action, reply with ONE line only like this: TOOL: tool_name | argument. "
                + "If no tool is needed, just answer normally and short. "
                + "Example: user says open youtube, you reply: TOOL: open_url | https://www.youtube.com";
            String json="{\"model\":\"qwen2.5:1.5b\","+"\"system\":\""+SysProm+"\","+"\"prompt\":"
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
            // System.out.println("Status: "+resp.statusCode());
            // System.out.println("Raw Resp: "+body);
            String key="\"response\":\"";
            int s=body.indexOf(key)+key.length();
            int e=body.indexOf("\",\"done\"",s);
            String answ=body.substring(s,e);
            // System.out.println("Mars: "+answ.replace("\\n", "\n"));
            String reply=answ.replace("\\n","\n").trim();
            if(reply.startsWith("TOOL")){
                String line=reply.substring(5).split("\n")[0];
                String[] parts=line.split("\\|",2);
                String name=parts[0].trim();
                String arg=parts.length>1 ? parts[1].trim() : "";
                System.out.println("Mars: "+Tools.run(name,arg));
            }else{System.out.println("Mars: "+reply);}

        }
        

} }