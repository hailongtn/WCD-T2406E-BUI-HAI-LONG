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

    private void listPlayers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Player> players = playerDAO.findAll();
        request.setAttribute("players", players);
        request.getRequestDispatcher("/WEB-INF/views/player/list.jsp").forward(request, response);
    }

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

    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Indexer> indexers = indexerDAO.findAll();
        request.setAttribute("indexers", indexers);
        request.getRequestDispatcher("/WEB-INF/views/player/form.jsp").forward(request, response);
    }

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

    private void insertPlayer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Player player = extractPlayerFromRequest(request);

        int indexValue = ValidationUtil.parseIntSafe(request.getParameter("indexValue"), 0);
        player.setIndexValue(indexValue > 0 ? indexValue : null);

        List<String> errors = ValidationUtil.validatePlayer(player);

        String ageStr = request.getParameter("age");
        if (!ValidationUtil.isValidInteger(ageStr)) {
            errors.add("Age must be a valid number");
        }

        if (player.getIndexId() != null && player.getIndexId() > 0) {
            Indexer indexer = indexerDAO.findById(player.getIndexId());
            if (indexer != null) {
                if (player.getIndexValue() == null || player.getIndexValue() == 0) {
                    errors.add("Value is required when Index is selected");
                } else if (player.getIndexValue() < indexer.getValueMin() || player.getIndexValue() > indexer.getValueMax()) {
                    errors.add("Value must be between " + indexer.getValueMin() + " and " + indexer.getValueMax());
                }
            }
        }

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("player", player);
            request.setAttribute("indexers", indexerDAO.findAll());
            request.getRequestDispatcher("/WEB-INF/views/player/form.jsp").forward(request, response);
            return;
        }

        int newId = playerDAO.insert(player);

        if (newId > 0) {
            if (player.getIndexId() != null && player.getIndexValue() != null) {
                PlayerIndex playerIndex = new PlayerIndex();
                playerIndex.setPlayerId(newId);
                playerIndex.setIndexId(player.getIndexId());
                playerIndex.setValue(player.getIndexValue());
                playerIndexDAO.insert(playerIndex);
            }
            request.getSession().setAttribute("successMessage", "Player added successfully!");
        } else {
            request.getSession().setAttribute("errorMessage", "Failed to add player");
        }

        response.sendRedirect(request.getContextPath() + "/player?action=list");
    }

    private void updatePlayer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Player player = extractPlayerFromRequest(request);
        player.setPlayerId(ValidationUtil.parseIntSafe(request.getParameter("playerId"), 0));

        int indexValue = ValidationUtil.parseIntSafe(request.getParameter("indexValue"), 0);
        player.setIndexValue(indexValue > 0 ? indexValue : null);

        List<String> errors = ValidationUtil.validatePlayer(player);

        String ageStr = request.getParameter("age");
        if (!ValidationUtil.isValidInteger(ageStr)) {
            errors.add("Age must be a valid number");
        }

        if (player.getPlayerId() <= 0) {
            errors.add("Invalid player ID");
        }

        if (player.getIndexId() != null && player.getIndexId() > 0) {
            Indexer indexer = indexerDAO.findById(player.getIndexId());
            if (indexer != null) {
                if (player.getIndexValue() == null || player.getIndexValue() == 0) {
                    errors.add("Value is required when Index is selected");
                } else if (player.getIndexValue() < indexer.getValueMin() || player.getIndexValue() > indexer.getValueMax()) {
                    errors.add("Value must be between " + indexer.getValueMin() + " and " + indexer.getValueMax());
                }
            }
        }

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("player", player);
            request.setAttribute("indexers", indexerDAO.findAll());
            request.getRequestDispatcher("/WEB-INF/views/player/form.jsp").forward(request, response);
            return;
        }

        boolean success = playerDAO.update(player);

        if (success) {
            if (player.getIndexId() != null && player.getIndexValue() != null) {
                PlayerIndex playerIndex = new PlayerIndex();
                playerIndex.setPlayerId(player.getPlayerId());
                playerIndex.setIndexId(player.getIndexId());
                playerIndex.setValue(player.getIndexValue());
                playerIndexDAO.insertOrUpdate(playerIndex);
            }
            request.getSession().setAttribute("successMessage", "Player updated successfully!");
        } else {
            request.getSession().setAttribute("errorMessage", "Failed to update player");
        }

        response.sendRedirect(request.getContextPath() + "/player?action=list");
    }

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
