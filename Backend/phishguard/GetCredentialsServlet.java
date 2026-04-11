package phishguard;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.io.PrintWriter;

@WebServlet("/getCredentials")
public class GetCredentialsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    //  Handle preflight
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        setCors(response, request);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        setCors(response, request); //  MUST FIRST
        response.setContentType("application/json");

        PrintWriter out = response.getWriter();

        try {
            //  HYBRID AUTH (SESSION + userId)
        	int userId;

        	if (request.getParameter("userId") != null) {
        	    userId = Integer.parseInt(request.getParameter("userId"));
        	} else {
        	    HttpSession session = request.getSession(false);

        	    if (session == null || session.getAttribute("userId") == null) {
        	        response.setStatus(401);
        	        return;
        	    }

        	    userId = (int) session.getAttribute("userId");
        	}

            //  DB
            Connection conn = DBConnection.getConnection();

            String sql = "SELECT id, website, site_username, encrypted_password FROM credentials WHERE user_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            //  Build JSON
            StringBuilder json = new StringBuilder("[");
            boolean first = true;

            while (rs.next()) {

                String decrypted = AESUtil.decrypt(rs.getString("encrypted_password"));

                if (!first) json.append(",");

                json.append("{")
                        .append("\"id\":").append(rs.getInt("id")).append(",")
                        .append("\"website\":\"").append(rs.getString("website")).append("\",")
                        .append("\"username\":\"").append(rs.getString("site_username")).append("\",")
                        .append("\"password\":\"").append(decrypted).append("\"")
                        .append("}");

                first = false;
            }

            json.append("]");
            out.print(json.toString());

            System.out.println("✅ Credentials sent");

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            out.print("{\"error\":\"Server error\"}");
        }
    }

    //  CORS
    private void setCors(HttpServletResponse response, HttpServletRequest request) {
        String origin = request.getHeader("Origin");

        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}
