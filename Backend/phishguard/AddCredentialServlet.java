package phishguard;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/addCredential")
@MultipartConfig
public class AddCredentialServlet extends HttpServlet {

private static final long serialVersionUID = 1L;

protected void doPost(HttpServletRequest request, HttpServletResponse response)
throws IOException {

System.out.println("🔥 SERVLET HIT");

try {
// GET DATA
String website = request.getParameter("website");
String username = request.getParameter("site_username");
String password = request.getParameter("password");

System.out.println("DATA: " + website + " " + username);

// SESSION CHECK
HttpSession session = request.getSession(false);

if (session == null || session.getAttribute("userId") == null) {
System.out.println("❌ Session missing");
response.setStatus(400);
return;
}

int userId = (int) session.getAttribute("userId");

Connection conn = DBConnection.getConnection();

// 🔥 CREATE TABLE EVERY TIME (FIXES H2 ISSUE)
Statement stmt = conn.createStatement();
stmt.execute("CREATE TABLE IF NOT EXISTS credentials (" +
"id INT AUTO_INCREMENT PRIMARY KEY," +
"user_id INT," +
"website VARCHAR(255)," +
"site_username VARCHAR(255)," +
"encrypted_password VARCHAR(255))");

// INSERT
String sql = "INSERT INTO credentials (user_id, website, site_username, encrypted_password) VALUES (?, ?, ?, ?)";
PreparedStatement ps = conn.prepareStatement(sql);

ps.setInt(1, userId);
ps.setString(2, website);
ps.setString(3, username);
String encrypted = AESUtil.encrypt(password);
ps.setString(4, encrypted);

ps.executeUpdate();

System.out.println("✅ INSERT SUCCESS");

response.setStatus(200);

} catch (Exception e) {
e.printStackTrace(); // 🔥 THIS WILL SHOW REAL ERROR
response.setStatus(500);
}
}
}
