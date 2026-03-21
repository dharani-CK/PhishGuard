package phishguard;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/deleteCredential")
public class DeleteCredentialServlet extends HttpServlet {

    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        System.out.println("🔥 DELETE HIT");

        try {
            HttpSession session = request.getSession(false);

            if (session == null || session.getAttribute("userId") == null) {
                response.setStatus(401);
                return;
            }

            int userId = (int) session.getAttribute("userId");
            int id = Integer.parseInt(request.getParameter("id"));

            Connection conn = DBConnection.getConnection();

            String sql = "DELETE FROM credentials WHERE id=? AND user_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, id);
            ps.setInt(2, userId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ Deleted");
                response.setStatus(200);
            } else {
                response.setStatus(404);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
        }
    }
}
