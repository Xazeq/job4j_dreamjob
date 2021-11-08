package ru.job4j.dream.servlet;

import ru.job4j.dream.model.User;
import ru.job4j.dream.store.DbStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class RegServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        User user = DbStore.instOf().findUserByEmail(email);
        if (user != null) {
            req.setAttribute("error", "Пользователь с такой почтой уже зарегистрирован");
            req.getRequestDispatcher("reg.jsp").forward(req, resp);
        } else {
            user = new User(name, email, password);
            DbStore.instOf().save(user);
            HttpSession sc = req.getSession();
            sc.setAttribute("user", user);
            resp.sendRedirect(req.getContextPath() + "/posts.do");
        }
    }
}
