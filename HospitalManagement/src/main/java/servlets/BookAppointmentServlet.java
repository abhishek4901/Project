package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/bookAppointment")
public class BookAppointmentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Change DB username & password according to your system
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hospital";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "abhishek";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Fetch form data
        String patientId = request.getParameter("patientId");
        String doctorId = request.getParameter("doctorId");
        String doctorName = request.getParameter("doctorName");
        String date = request.getParameter("appointmentDate");
        String time = request.getParameter("appointmentTime");
        String reason = request.getParameter("reason");

        try {
            // 1. Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2. Connect to Database
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            // 3. Prepare SQL Insert Statement
            String sql = "INSERT INTO appointments " +
                         "(patient_id, doctor_id, doctor_name, appointment_date, appointment_time, reason, status) " +
                         "VALUES (?, ?, ?, ?, ?, ?, 'Scheduled')";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, Integer.parseInt(patientId));
            ps.setInt(2, Integer.parseInt(doctorId));
            ps.setString(3, doctorName);
            ps.setString(4, date);
            ps.setString(5, time);
            ps.setString(6, reason);

            // 4. Execute Update
            int rows = ps.executeUpdate();

            if (rows > 0) {
                out.println("<h3 style='color:green;'>✅ Appointment booked successfully!</h3>");
            } else {
                out.println("<h3 style='color:red;'>❌ Failed to book appointment.</h3>");
            }

            // 5. Close Connection
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<h3 style='color:red;'>⚠ Error: " + e.getMessage() + "</h3>");
        }
    }
}
