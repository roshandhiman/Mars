import java.sql.*;

public class Database {
    static final String URL = "jdbc:mysql://localhost:3306/mars?allowPublicKeyRetrieval=true&useSSL=false";
    static final String USER = "root";
    static final String PASS = "xyz";

    static void init() {
        try (Connection c = DriverManager.getConnection(URL, USER, PASS);
             Statement st = c.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS messages (id INT AUTO_INCREMENT PRIMARY KEY, text TEXT)");
        } catch (Exception e) {
            System.out.println("DB error: " + e.getMessage());
        }
    }

    static void save(String text) {
        try (Connection c = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = c.prepareStatement("INSERT INTO messages(text) VALUES (?)")) {
            ps.setString(1, text);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("DB error: " + e.getMessage());
        }
    }

    static String lastMessages(int n) {
        StringBuilder sb = new StringBuilder();
        try (Connection c = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = c.prepareStatement(
                 "SELECT text FROM (SELECT id, text FROM messages ORDER BY id DESC LIMIT ?) AS t ORDER BY id ASC")) {
            ps.setInt(1, n);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                sb.append("- ").append(rs.getString("text")).append(" ");
            }
        } catch (Exception e) {
            System.out.println("DB error: " + e.getMessage());
        }
        return sb.toString();
    }
}
