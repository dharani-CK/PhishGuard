package phishguard;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.io.PrintWriter;

@WebServlet("/getPassword")
public class GetPasswordServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        setCors(response, request);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        setCors(response, request);

        PrintWriter out = response.getWriter();

        try {
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

            int id = Integer.parseInt(request.getParameter("id"));

            Connection conn = DBConnection.getConnection();

            String sql = "SELECT encrypted_password FROM credentials WHERE id=? AND user_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, id);
            ps.setInt(2, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String decrypted = AESUtil.decrypt(rs.getString("encrypted_password"));
                out.print(decrypted);
            } else {
                response.setStatus(404);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
        }
    }

    private void setCors(HttpServletResponse response, HttpServletRequest request) {
        String origin = request.getHeader("Origin");

        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
}
