package phishguard;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.io.PrintWriter;

@WebServlet("/getCredentials")
public class GetCredentialsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // ===============================
            // 1️⃣ CHECK SESSION
            // ===============================
            HttpSession session = request.getSession(false);

            if (session == null || session.getAttribute("userId") == null) {
                System.out.println("❌ Unauthorized access");
                response.setStatus(401);
                return;
            }

            int userId = (int) session.getAttribute("userId");

            // ===============================
            // 2️⃣ CONNECT DATABASE
            // ===============================
            Connection conn = DBConnection.getConnection();

            String sql = "SELECT id, website, site_username, encrypted_password FROM credentials WHERE user_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            // ===============================
            // 3️⃣ BUILD JSON RESPONSE
            // ===============================
            StringBuilder json = new StringBuilder("[");
            boolean first = true;

            while (rs.next()) {

                String website = rs.getString("website");
                String username = rs.getString("site_username");

                // 🔐 DECRYPT PASSWORD
                String encrypted = rs.getString("encrypted_password");
                String decrypted = AESUtil.decrypt(encrypted);

                if (!first) json.append(",");
                json.append("{")
                    .append("\"id\":").append(rs.getInt("id")).append(",")
                    .append("\"website\":\"").append(website).append("\",")
                    .append("\"username\":\"").append(username).append("\",")
                    .append("\"password\":\"").append(decrypted).append("\"")
                    .append("}");

                first = false;
            }

            json.append("]");

            out.print(json.toString());

            System.out.println("✅ Credentials fetched successfully");

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
        }
    }
}
