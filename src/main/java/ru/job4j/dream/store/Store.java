package ru.job4j.dream.store;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.Post;
import ru.job4j.dream.model.User;
import java.util.Collection;
import java.util.Properties;

public interface Store {
    Collection<Post> findAllPosts();
    Collection<Candidate> findAllCandidates();
    void save(Post post);
    void save(Candidate candidate);
    Post findPostById(int id);
    Candidate findCandidateById(int id);
    void deleteCandidate(int id);
    Properties getConfig();
    void clearTables();
    Collection<User> findAllUsers();
    void save(User user);
    User findUserById(int id);
    User findUserByEmail(String email);
}
