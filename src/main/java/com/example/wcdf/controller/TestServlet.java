package com.example.wcdf.controller;

import com.example.wcdf.dao.PlayerDAO;
import com.example.wcdf.model.Player;
import com.example.wcdf.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 * Test Servlet to check database connection and data
 */
@WebServlet(name = "TestServlet", urlPatterns = {"/test"})
public class TestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<html><head><title>System Test</title></head><body>");
        out.println("<h2>System Test</h2>");

        // Test 1: Basic Servlet
        out.println("<p>1. Servlet: <b style='color:green'>OK</b></p>");

        // Test 2: Database Connection
        out.println("<p>2. Database Connection: ");
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                out.println("<b style='color:green'>OK</b></p>");

                // Test 3: Query player table directly
                out.println("<p>3. Direct SQL Query (player table): ");
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as cnt FROM player")) {
                    if (rs.next()) {
                        int count = rs.getInt("cnt");
                        out.println("<b style='color:green'>OK - " + count + " players</b></p>");
                    }
                } catch (Exception e) {
                    out.println("<b style='color:red'>FAILED - " + e.getMessage() + "</b></p>");
                }

                // Test 4: PlayerDAO
                out.println("<p>4. PlayerDAO.findAll(): ");
                try {
                    PlayerDAO dao = new PlayerDAO();
                    List<Player> players = dao.findAll();
                    out.println("<b style='color:green'>OK - " + players.size() + " players</b></p>");

                    if (!players.isEmpty()) {
                        out.println("<h3>Player Data:</h3>");
                        out.println("<table border='1' cellpadding='5'>");
                        out.println("<tr><th>ID</th><th>Name</th><th>Full Name</th><th>Age</th><th>Index</th></tr>");
                        for (Player p : players) {
                            out.println("<tr>");
                            out.println("<td>" + p.getPlayerId() + "</td>");
                            out.println("<td>" + p.getName() + "</td>");
                            out.println("<td>" + p.getFullName() + "</td>");
                            out.println("<td>" + p.getAge() + "</td>");
                            out.println("<td>" + (p.getIndexerName() != null ? p.getIndexerName() : "-") + "</td>");
                            out.println("</tr>");
                        }
                        out.println("</table>");
                    }
                } catch (Exception e) {
                    out.println("<b style='color:red'>FAILED</b></p>");
                    out.println("<pre style='color:red'>");
                    e.printStackTrace(out);
                    out.println("</pre>");
                }

                conn.close();
            } else {
                out.println("<b style='color:red'>FAILED - Connection is null</b></p>");
            }
        } catch (Exception e) {
            out.println("<b style='color:red'>FAILED</b></p>");
            out.println("<p style='color:red'>Error: " + e.getMessage() + "</p>");
            out.println("<pre>");
            e.printStackTrace(out);
            out.println("</pre>");
        }

        out.println("<hr><p><a href='" + request.getContextPath() + "/player?action=list'>Go to Player List</a></p>");
        out.println("</body></html>");
    }
}


