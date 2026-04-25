package phishguard;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URL;
import java.sql.*;

@WebServlet("/addCredential")
@MultipartConfig
public class AddCredentialServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        System.out.println("🔥 SERVLET HIT");

        try {
            // 🔹 GET DATA
            String website = request.getParameter("website");
            String username = request.getParameter("site_username");
            String password = request.getParameter("password");

            System.out.println("DATA: " + website + " " + username);

            // 🚨 BACKEND PHISHING CHECK (DB + RULES)
            if (isPhishingFromDB(website) || isPhishingRules(website)) {
                System.out.println("🚫 BLOCKED PHISHING: " + website);
                response.setStatus(403);
                response.getWriter().write("Phishing detected");
                return;
            }

            // 🔐 SESSION CHECK
            HttpSession session = request.getSession(false);

            if (session == null || session.getAttribute("userId") == null) {
                System.out.println("❌ Session missing");
                response.setStatus(401);
                return;
            }

            int userId = (int) session.getAttribute("userId");

            Connection conn = DBConnection.getConnection();

            // 🔹 CREATE TABLE IF NOT EXISTS
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS credentials (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "user_id INT," +
                    "website VARCHAR(255)," +
                    "site_username VARCHAR(255)," +
                    "encrypted_password VARCHAR(255))");

            // 🔹 INSERT DATA
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
            e.printStackTrace();
            response.setStatus(500);
        }
    }

    // 🔥 DB-BASED PHISHING DETECTION (MAIN LOGIC)
    private boolean isPhishingFromDB(String url) {
        try {
            URL u = new URL(url);
            String host = u.getHost().toLowerCase();

            // remove www
            if (host.startsWith("www.")) {
                host = host.substring(4);
            }

            Connection conn = DBConnection.getConnection();

            String sql = "SELECT domain FROM trusted_domains";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            boolean safe = false;

            while (rs.next()) {
                String dbDomain = rs.getString("domain");

                if (host.equals(dbDomain) || host.endsWith("." + dbDomain)) {
                    safe = true;
                    break;
                }
            }

            System.out.println("🔍 Checking domain: " + host + " → " + (safe ? "SAFE" : "PHISHING"));

            return !safe; // NOT safe → phishing

        } catch (Exception e) {
            return true;
        }
    }

    // ⚠️ OPTIONAL RULE-BASED CHECK (EXTRA SECURITY)
    private boolean isPhishingRules(String url) {
        try {
            URL u = new URL(url);
            String host = u.getHost().toLowerCase();

            // ❌ must be HTTPS
            if (!url.startsWith("https://")) return true;

            // ❌ suspicious keywords
            if (host.contains("login") || host.contains("secure") || host.contains("verify")) return true;

            // ❌ too many subdomains
            if (host.split("\\.").length > 3) return true;

            return false;

        } catch (Exception e) {
            return true;
        }
    }
}
