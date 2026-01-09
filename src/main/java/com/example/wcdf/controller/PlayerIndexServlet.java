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
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet Controller for PlayerIndex CRUD operations
 * Handles: list, add, edit, update, delete
 * Includes validation of index value within min/max range
 */
@WebServlet(name = "PlayerIndexServlet", urlPatterns = {"/playerindex", "/playerindex/*"})
public class PlayerIndexServlet extends HttpServlet {

    private PlayerIndexDAO playerIndexDAO;
    private PlayerDAO playerDAO;
    private IndexerDAO indexerDAO;

    @Override
    public void init() throws ServletException {
        playerIndexDAO = new PlayerIndexDAO();
        playerDAO = new PlayerDAO();
        indexerDAO = new IndexerDAO();
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
                    deletePlayerIndex(request, response);
                    break;
                case "byPlayer":
                    listByPlayer(request, response);
                    break;
                case "list":
                default:
                    listPlayerIndexes(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            listPlayerIndexes(request, response);
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
                    insertPlayerIndex(request, response);
                    break;
                case "update":
                    updatePlayerIndex(request, response);
                    break;
                default:
                    listPlayerIndexes(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            listPlayerIndexes(request, response);
        }
    }

    /**
     * Display list of all player indexes
     */
    private void listPlayerIndexes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<PlayerIndex> playerIndexes = playerIndexDAO.findAll();
        request.setAttribute("playerIndexes", playerIndexes);
        request.getRequestDispatcher("/WEB-INF/views/playerindex/list.jsp").forward(request, response);
    }

    /**
     * Display indexes for a specific player
     */
    private void listByPlayer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int playerId = ValidationUtil.parseIntSafe(request.getParameter("playerId"), 0);

        if (playerId > 0) {
            List<PlayerIndex> playerIndexes = playerIndexDAO.findByPlayerId(playerId);
            Player player = playerDAO.findById(playerId);
            request.setAttribute("playerIndexes", playerIndexes);
            request.setAttribute("selectedPlayer", player);
        } else {
            request.setAttribute("playerIndexes", playerIndexDAO.findAll());
        }

        request.getRequestDispatcher("/WEB-INF/views/playerindex/list.jsp").forward(request, response);
    }

    /**
     * Show form for new player index
     */
    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Player> players = playerDAO.findAll();
        List<Indexer> indexers = indexerDAO.findAll();

        request.setAttribute("players", players);
        request.setAttribute("indexers", indexers);

        // Pre-select player if provided
        int preselectedPlayerId = ValidationUtil.parseIntSafe(request.getParameter("playerId"), 0);
        if (preselectedPlayerId > 0) {
            request.setAttribute("preselectedPlayerId", preselectedPlayerId);
        }

        request.getRequestDispatcher("/WEB-INF/views/playerindex/form.jsp").forward(request, response);
    }

    /**
     * Show form for editing existing player index
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = ValidationUtil.parseIntSafe(request.getParameter("id"), 0);

        if (id <= 0) {
            request.setAttribute("errorMessage", "Invalid player index ID");
            listPlayerIndexes(request, response);
            return;
        }

        PlayerIndex playerIndex = playerIndexDAO.findById(id);

        if (playerIndex == null) {
            request.setAttribute("errorMessage", "Player index not found");
            listPlayerIndexes(request, response);
            return;
        }

        List<Player> players = playerDAO.findAll();
        List<Indexer> indexers = indexerDAO.findAll();

        request.setAttribute("playerIndex", playerIndex);
        request.setAttribute("players", players);
        request.setAttribute("indexers", indexers);
        request.getRequestDispatcher("/WEB-INF/views/playerindex/form.jsp").forward(request, response);
    }

    /**
     * Insert new player index with validation
     */
    private void insertPlayerIndex(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Extract form data
        PlayerIndex playerIndex = extractPlayerIndexFromRequest(request);

        // Get the indexer for validation
        Indexer indexer = indexerDAO.findById(playerIndex.getIndexId());

        // Server-side validation
        List<String> errors = ValidationUtil.validatePlayerIndex(playerIndex, indexer);

        // Validate value is numeric
        String valueStr = request.getParameter("value");
        if (!ValidationUtil.isValidInteger(valueStr)) {
            errors.add("Value must be a valid number");
        }

        // Check for duplicate player-index combination
        if (playerIndexDAO.existsPlayerIndexCombination(
                playerIndex.getPlayerId(), playerIndex.getIndexId(), 0)) {
            errors.add("This player already has a value for this index");
        }

        if (!errors.isEmpty()) {
            // Validation failed - return to form with errors
            request.setAttribute("errors", errors);
            request.setAttribute("playerIndex", playerIndex);
            request.setAttribute("players", playerDAO.findAll());
            request.setAttribute("indexers", indexerDAO.findAll());
            request.getRequestDispatcher("/WEB-INF/views/playerindex/form.jsp").forward(request, response);
            return;
        }

        // Insert player index
        int newId = playerIndexDAO.insert(playerIndex);

        if (newId > 0) {
            request.getSession().setAttribute("successMessage", "Player index added successfully!");
        } else {
            request.getSession().setAttribute("errorMessage", "Failed to add player index");
        }

        response.sendRedirect(request.getContextPath() + "/playerindex?action=list");
    }

    /**
     * Update existing player index with validation
     */
    private void updatePlayerIndex(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Extract form data
        PlayerIndex playerIndex = extractPlayerIndexFromRequest(request);
        playerIndex.setId(ValidationUtil.parseIntSafe(request.getParameter("id"), 0));

        // Get the indexer for validation
        Indexer indexer = indexerDAO.findById(playerIndex.getIndexId());

        // Server-side validation
        List<String> errors = ValidationUtil.validatePlayerIndex(playerIndex, indexer);

        // Validate value is numeric
        String valueStr = request.getParameter("value");
        if (!ValidationUtil.isValidInteger(valueStr)) {
            errors.add("Value must be a valid number");
        }

        // Validate ID
        if (playerIndex.getId() <= 0) {
            errors.add("Invalid player index ID");
        }

        // Check for duplicate (excluding current record)
        if (playerIndexDAO.existsPlayerIndexCombination(
                playerIndex.getPlayerId(), playerIndex.getIndexId(), playerIndex.getId())) {
            errors.add("This player already has a value for this index");
        }

        if (!errors.isEmpty()) {
            // Validation failed - return to form with errors
            request.setAttribute("errors", errors);
            request.setAttribute("playerIndex", playerIndex);
            request.setAttribute("players", playerDAO.findAll());
            request.setAttribute("indexers", indexerDAO.findAll());
            request.getRequestDispatcher("/WEB-INF/views/playerindex/form.jsp").forward(request, response);
            return;
        }

        // Update player index
        boolean success = playerIndexDAO.update(playerIndex);

        if (success) {
            request.getSession().setAttribute("successMessage", "Player index updated successfully!");
        } else {
            request.getSession().setAttribute("errorMessage", "Failed to update player index");
        }

        response.sendRedirect(request.getContextPath() + "/playerindex?action=list");
    }

    /**
     * Delete player index
     */
    private void deletePlayerIndex(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = ValidationUtil.parseIntSafe(request.getParameter("id"), 0);

        if (id <= 0) {
            request.getSession().setAttribute("errorMessage", "Invalid player index ID");
        } else {
            boolean success = playerIndexDAO.delete(id);
            if (success) {
                request.getSession().setAttribute("successMessage", "Player index deleted successfully!");
            } else {
                request.getSession().setAttribute("errorMessage", "Failed to delete player index");
            }
        }

        response.sendRedirect(request.getContextPath() + "/playerindex?action=list");
    }

    /**
     * Extract PlayerIndex object from request parameters
     */
    private PlayerIndex extractPlayerIndexFromRequest(HttpServletRequest request) {
        PlayerIndex playerIndex = new PlayerIndex();
        playerIndex.setPlayerId(ValidationUtil.parseIntSafe(request.getParameter("playerId"), 0));
        playerIndex.setIndexId(ValidationUtil.parseIntSafe(request.getParameter("indexId"), 0));
        playerIndex.setValue(ValidationUtil.parseIntSafe(request.getParameter("value"), 0));
        return playerIndex;
    }
}

