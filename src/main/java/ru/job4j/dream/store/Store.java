package ru.job4j.dream.store;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.City;
import ru.job4j.dream.model.Post;
import ru.job4j.dream.model.User;
import java.util.Collection;
import java.util.Properties;

public interface Store {
    Collection<Post> findAllPosts();
    Collection<Candidate> findAllCandidates();
    Collection<User> findAllUsers();
    Collection<City> findAllCities();
    void save(Post post);
    void save(Candidate candidate);
    void save(User user);
    Post findPostById(int id);
    Candidate findCandidateById(int id);
    City findCityById(int id);
    User findUserById(int id);
    void deleteCandidate(int id);
    Properties getConfig();
    void clearTables();
    User findUserByEmail(String email);
    City findCityByName(String name);
    Collection<Post> findLastDayPosts();
    Collection<Candidate> findLastDayCandidates();
}
