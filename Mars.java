import java.net.URI;
import java.net.http.*;
import java.util.Scanner;

public class Mars {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        HttpClient client = HttpClient.newHttpClient();
        Database.init();
        while (true) {
            System.out.print("You: ");
            String msg = sc.nextLine();
            if (msg.equalsIgnoreCase("exit") || msg.equalsIgnoreCase("bye")) break;
            String history = Database.lastMessages(10).replace("\\", "\\\\").replace("\"", "\\\"");
            Database.save(msg);
            String sys = "You are Mars, a personal desktop AI assistant created by sir Roshanpreet Singh. Answer in short and simple words. "
                + "When user wants an action, reply with ONLY ONE LINE in format: TOOL: tool_name | argument. "
                + "Tools: "
                + "open_app | app_name (opens Mac app) "
                + "close_app | app_name (closes Mac app) "
                + "open_url | url (opens website in browser) "
                + "create_file | path or path|content (creates file) "
                + "delete_file | path (deletes file) "
                + "read_file | path (reads file content) "
                + "say | text (speaks text out loud) "
                + "cmd | command (runs shell command) "
                + "Examples: "
                + "User: open brave -> TOOL: open_app | Brave "
                + "User: close brave -> TOOL: close_app | Brave "
                + "User: say hello -> TOOL: say | hello "
                + "User: open youtube -> TOOL: open_url | https://youtube.com "
                + "User: open settings -> TOOL: open_app | System Settings "
                + "User: create file test.txt -> TOOL: create_file | Desktop/test.txt "
                + "If no tool needed, answer normally in 1-2 short sentences. "
                + "Old chat: " + history;
            String json = "{\"model\":\"qwen2.5:1.5b\"," + "\"system\":\"" + sys + "\"," + "\"prompt\":"
                + "\"" + msg.replace("\\", "\\\\").replace("\"", "\\\"") + "\",\"stream\":false}";
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:11434/api/generate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            String key = "\"response\":\"";
            int s = body.indexOf(key) + key.length();
            int e = body.indexOf("\",\"done\"", s);
            String answ = body.substring(s, e);
            String reply = answ.replace("\\n", "\n").trim();
            if (reply.startsWith("TOOL")) {
                String line = reply.substring(reply.indexOf("TOOL") + 4).replaceFirst("^:", "").trim().split("\n")[0];
                String[] parts = line.split("\\|", 2);
                String name = parts[0].trim();
                String arg = parts.length > 1 ? parts[1].trim() : "";
                System.out.println("Mars: " + Tools.run(name, arg));
            } else if (reply.contains(":") && isTool(reply.split(":", 2)[0].trim())) {
                String[] parts = reply.split(":", 2);
                System.out.println("Mars: " + Tools.run(parts[0].trim(), parts[1].trim()));
            } else {
                System.out.println("Mars: " + reply);
            }
        }
    }

    static boolean isTool(String s) {
        return s.equals("open_app") || s.equals("close_app") || s.equals("open_url")
            || s.equals("create_file") || s.equals("delete_file") || s.equals("read_file")
            || s.equals("say") || s.equals("cmd");
    }
}
