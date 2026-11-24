import java.io.File;
import java.nio.file.Files;

public class Tools {
    public static String run(String name, String arg) {
        try {
            arg = arg.trim();
            switch (name) {
                case "open_url":
                    if (!arg.startsWith("http")) arg = (arg.contains(".") ? "https://" : "https://www.") + arg + (arg.contains(".") ? "" : ".com");
                    new ProcessBuilder("open", arg).start();
                    return "Opened " + arg;
                case "open_app":
                    Process p = new ProcessBuilder("open", "-a", arg).start();
                    if (p.waitFor() != 0) {
                        Process fp = new ProcessBuilder("zsh", "-c", "mdfind \"kMDItemContentType == 'com.apple.application-bundle'\" | grep -i \"" + arg + "\" | head -n 1").start();
                        String path = new String(fp.getInputStream().readAllBytes()).trim();
                        if (!path.isEmpty()) new ProcessBuilder("open", path).start();
                        else return "App not found: " + arg;
                    }
                    return "Opened " + arg;
                case "close_app":
                    new ProcessBuilder("pkill", "-f", "-i", arg).start().waitFor();
                    return "Closed " + arg;
                case "create_file":
                    String[] fp = arg.split("\\|", 2);
                    File cf = resolveFile(fp[0].trim());
                    if (cf.getParentFile() != null) cf.getParentFile().mkdirs();
                    if (fp.length > 1) Files.writeString(cf.toPath(), fp[1].trim());
                    else cf.createNewFile();
                    return "Created " + cf.getPath();
                case "delete_file":
                    File df = resolveFile(arg);
                    return df.delete() ? "Deleted " + df.getPath() : "File not found";
                case "read_file":
                    File rf = resolveFile(arg);
                    return Files.exists(rf.toPath()) ? Files.readString(rf.toPath()) : "File not found";
                case "say":
                    new ProcessBuilder("say", arg).start();
                    return "Speaking: " + arg;
                case "cmd":
                    Process cp = new ProcessBuilder("zsh", "-c", arg).start();
                    String out = new String(cp.getInputStream().readAllBytes()).trim();
                    String err = new String(cp.getErrorStream().readAllBytes()).trim();
                    return out.isEmpty() ? (err.isEmpty() ? "Done" : err) : out;
                default:
                    return "Tool not found: " + name;
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    static File resolveFile(String path) {
        String home = System.getProperty("user.home");
        if (path.startsWith("~")) path = home + path.substring(1);
        else if (path.startsWith("Desktop/") || path.startsWith("Downloads/") || path.startsWith("Documents/")) path = home + "/" + path;
        return new File(path);
    }
}
