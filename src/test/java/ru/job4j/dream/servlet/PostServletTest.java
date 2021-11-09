package ru.job4j.dream.servlet;

import org.junit.Before;
import org.junit.Test;
import ru.job4j.dream.model.Post;
import ru.job4j.dream.store.DbStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.notNullValue;

public class PostServletTest {
    @Before
    public void clearTable() {
        DbStore.instOf().clearTables();
    }

    @Test
    public void whenCreatePost() throws ServletException, IOException {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(req.getParameter("id")).thenReturn("0");
        when(req.getParameter("name")).thenReturn("name of new post");
        new PostServlet().doPost(req, resp);
        Post post =  DbStore.instOf().findAllPosts().stream().findFirst().get();
        assertThat(post, notNullValue());
        assertThat(post.getName(), is("name of new post"));
    }

    @Test
    public void whenUpdatePost() throws ServletException, IOException {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        when(req.getParameter("id")).thenReturn("0");
        when(req.getParameter("name")).thenReturn("name of new post");
        PostServlet postServlet = new PostServlet();
        postServlet.doPost(req, resp);
        Post oldPost = DbStore.instOf().findAllPosts().stream().findFirst().get();
        when(req.getParameter("id")).thenReturn(String.valueOf(oldPost.getId()));
        when(req.getParameter("name")).thenReturn("new name");
        postServlet.doPost(req, resp);
        Post newPost =  DbStore.instOf().findAllPosts().stream().findFirst().get();
        assertThat(newPost, notNullValue());
        assertThat(newPost.getName(), is("new name"));
    }
}