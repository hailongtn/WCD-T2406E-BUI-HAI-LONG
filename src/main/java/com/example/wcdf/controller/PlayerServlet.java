package com.example.wcdf.controller;

import com.example.wcdf.dao.IndexerDAO;
import com.example.wcdf.dao.PlayerDAO;
import com.example.wcdf.dao.PlayerIndexDAO;
import com.example.wcdf.model.Indexer;
import com.example.wcdf.model.Player;
import com.example.wcdf.model.PlayerIndex;
import com.example.wcdf.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Servlet Controller for Player CRUD operations
 * Handles: list, add, edit, update, delete
 */
@WebServlet(name = "PlayerServlet", urlPatterns = {"/player", "/player/*"})
public class PlayerServlet extends HttpServlet {

    private PlayerDAO playerDAO;
    private IndexerDAO indexerDAO;
    private PlayerIndexDAO playerIndexDAO;

    @Override
    public void init() throws ServletException {
        playerDAO = new PlayerDAO();
        indexerDAO = new IndexerDAO();
        playerIndexDAO = new PlayerIndexDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "new":
                    showNewForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "delete":
                    deletePlayer(request, response);
                    break;
                case "search":
                    searchPlayers(request, response);
                    break;
                case "list":
                default:
                    listPlayers(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            listPlayers(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        try {
            switch (action) {
                case "insert":
                    insertPlayer(request, response);
                    break;
                case "update":
                    updatePlayer(request, response);
                    break;
                default:
                    listPlayers(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            listPlayers(request, response);
        }
    }

    /**
     * Display list of all players
     */
    private void listPlayers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Player> players = playerDAO.findAll();
        request.setAttribute("players", players);
        request.getRequestDispatcher("/WEB-INF/views/player/list.jsp").forward(request, response);
    }

    /**
     * Search players by name
     */
    private void searchPlayers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String searchName = request.getParameter("searchName");
        List<Player> players;

        if (searchName != null && !searchName.trim().isEmpty()) {
            players = playerDAO.findByName(searchName.trim());
            request.setAttribute("searchName", searchName);
        } else {
            players = playerDAO.findAll();
        }

        request.setAttribute("players", players);
        request.getRequestDispatcher("/WEB-INF/views/player/list.jsp").forward(request, response);
    }

    /**
     * Show form for new player
     */
    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Indexer> indexers = indexerDAO.findAll();
        request.setAttribute("indexers", indexers);
        request.getRequestDispatcher("/WEB-INF/views/player/form.jsp").forward(request, response);
    }

    /**
     * Show form for editing existing player
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int playerId = ValidationUtil.parseIntSafe(request.getParameter("id"), 0);

        if (playerId <= 0) {
            request.setAttribute("errorMessage", "Invalid player ID");
            listPlayers(request, response);
            return;
        }

        Player player = playerDAO.findById(playerId);

        if (player == null) {
            request.setAttribute("errorMessage", "Player not found");
            listPlayers(request, response);
            return;
        }

        List<Indexer> indexers = indexerDAO.findAll();
        request.setAttribute("player", player);
        request.setAttribute("indexers", indexers);
        request.getRequestDispatcher("/WEB-INF/views/player/form.jsp").forward(request, response);
    }

    /**
     * Insert new player with validation
     */
    private void insertPlayer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Extract form data
        Player player = extractPlayerFromRequest(request);

        // Server-side validation
        List<String> errors = ValidationUtil.validatePlayer(player);

        // Validate age is numeric (additional check)
        String ageStr = request.getParameter("age");
        if (!ValidationUtil.isValidInteger(ageStr)) {
            errors.add("Age must be a valid number");
        }

        if (!errors.isEmpty()) {
            // Validation failed - return to form with errors
            request.setAttribute("errors", errors);
            request.setAttribute("player", player);
            request.setAttribute("indexers", indexerDAO.findAll());
            request.getRequestDispatcher("/WEB-INF/views/player/form.jsp").forward(request, response);
            return;
        }

        // Insert player
        int newId = playerDAO.insert(player);

        if (newId > 0) {
            request.getSession().setAttribute("successMessage", "Player added successfully!");
        } else {
            request.getSession().setAttribute("errorMessage", "Failed to add player");
        }

        response.sendRedirect(request.getContextPath() + "/player?action=list");
    }

    /**
     * Update existing player with validation
     */
    private void updatePlayer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Extract form data
        Player player = extractPlayerFromRequest(request);
        player.setPlayerId(ValidationUtil.parseIntSafe(request.getParameter("playerId"), 0));

        // Server-side validation
        List<String> errors = ValidationUtil.validatePlayer(player);

        // Validate age is numeric
        String ageStr = request.getParameter("age");
        if (!ValidationUtil.isValidInteger(ageStr)) {
            errors.add("Age must be a valid number");
        }

        // Validate player ID
        if (player.getPlayerId() <= 0) {
            errors.add("Invalid player ID");
        }

        if (!errors.isEmpty()) {
            // Validation failed - return to form with errors
            request.setAttribute("errors", errors);
            request.setAttribute("player", player);
            request.setAttribute("indexers", indexerDAO.findAll());
            request.getRequestDispatcher("/WEB-INF/views/player/form.jsp").forward(request, response);
            return;
        }

        // Update player
        boolean success = playerDAO.update(player);

        if (success) {
            request.getSession().setAttribute("successMessage", "Player updated successfully!");
        } else {
            request.getSession().setAttribute("errorMessage", "Failed to update player");
        }

        response.sendRedirect(request.getContextPath() + "/player?action=list");
    }

    /**
     * Delete player
     */
    private void deletePlayer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int playerId = ValidationUtil.parseIntSafe(request.getParameter("id"), 0);

        if (playerId <= 0) {
            request.getSession().setAttribute("errorMessage", "Invalid player ID");
        } else {
            boolean success = playerDAO.delete(playerId);
            if (success) {
                request.getSession().setAttribute("successMessage", "Player deleted successfully!");
            } else {
                request.getSession().setAttribute("errorMessage", "Failed to delete player");
            }
        }

        response.sendRedirect(request.getContextPath() + "/player?action=list");
    }

    /**
     * Extract Player object from request parameters
     */
    private Player extractPlayerFromRequest(HttpServletRequest request) {
        Player player = new Player();
        player.setName(request.getParameter("name"));
        player.setFullName(request.getParameter("fullName"));
        player.setAge(ValidationUtil.parseIntSafe(request.getParameter("age"), 0));

        int indexId = ValidationUtil.parseIntSafe(request.getParameter("indexId"), 0);
        if (indexId > 0) {
            player.setIndexId(indexId);
        }

        return player;
    }
}

