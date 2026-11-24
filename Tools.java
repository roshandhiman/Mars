public class Tools {
    public static String run(String name,String arg){
        switch (name) {
            case "open_url":
                return openUrl(arg);
        
            default:
                return"i dont have a tool";
        }
    }
    static String openUrl(String url){
        try{
            if(!url.startsWith("http")) 
                url="https://"+url;
            new ProcessBuilder("open",url).start();
            return "Opened "+url;
        }catch(Exception e){
            return "could not open: " +e.getMessage();
        }
    }
}
