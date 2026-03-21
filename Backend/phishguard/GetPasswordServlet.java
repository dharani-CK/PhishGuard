package phishguard;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.io.PrintWriter;

@WebServlet("/getPassword")
public class GetPasswordServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        PrintWriter out = response.getWriter();

        try {
            HttpSession session = request.getSession(false);

            if (session == null || session.getAttribute("userId") == null) {
                response.setStatus(401);
                return;
            }

            int userId = (int) session.getAttribute("userId");

            int id = Integer.parseInt(request.getParameter("id"));

            Connection conn = DBConnection.getConnection();

            String sql = "SELECT encrypted_password FROM credentials WHERE id=? AND user_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, id);
            ps.setInt(2, userId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String encrypted = rs.getString("encrypted_password");

                // 🔐 DECRYPT
                String decrypted = AESUtil.decrypt(encrypted);

                out.print(decrypted);
            } else {
                response.setStatus(404);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
        }
    }
}
