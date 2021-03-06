package ru.job4j.dream.servlet;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.store.DbStore;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CandidateServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("candidates", DbStore.instOf().findAllCandidates());
        req.setAttribute("user", req.getSession().getAttribute("user"));
        req.getRequestDispatcher("candidates.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        int candidateId = Integer.parseInt(req.getParameter("id"));
        String candidateName = req.getParameter("name");
        int cityId = Integer.parseInt(req.getParameter("cities"));
        DbStore.instOf().save(
                new Candidate(
                        candidateId,
                        candidateName,
                        DbStore.instOf().findCityById(cityId)));
        resp.sendRedirect(req.getContextPath() + "/candidates.do");
    }
}
