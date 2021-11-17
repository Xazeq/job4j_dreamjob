package ru.job4j.dream.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.City;
import ru.job4j.dream.model.Post;
import ru.job4j.dream.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MemStore implements Store {
    private static final Store INST = new MemStore();
    private final Map<Integer, Post> posts = new ConcurrentHashMap<>();
    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();
    private final Map<Integer, User> users = new ConcurrentHashMap<>();
    private final Map<Integer, City> cities = new ConcurrentHashMap<>();
    private static AtomicInteger postId = new AtomicInteger(4);
    private static AtomicInteger candidateId = new AtomicInteger(4);
    private static AtomicInteger userId = new AtomicInteger(4);
    private static AtomicInteger cityId = new AtomicInteger(4);
    private final Properties cfg = new Properties();
    private static final Logger LOG = LoggerFactory.getLogger(MemStore.class.getName());

    private MemStore() {
        try (InputStream in = MemStore.class.getClassLoader().getResourceAsStream("app.properties")) {
            cfg.load(in);
        } catch (IOException e) {
            LOG.error("Exception in MemStore constructor", e);
        }
        posts.put(1, new Post(1, "Junior Java Job", "Description for Junior Java Job"));
        posts.put(2, new Post(2, "Middle Java Job", "Description for Middle Java Job"));
        posts.put(3, new Post(3, "Senior Java Job", "Description for Senior Java Job"));
        candidates.put(1, new Candidate(1, "Junior Java"));
        candidates.put(2, new Candidate(2, "Middle Java"));
        candidates.put(3, new Candidate(3, "Senior Java"));
        users.put(1, new User("user1", "user1@mail.ru", "12345"));
        users.put(2, new User("user2", "user2@mail.ru", "12345"));
        users.put(3, new User("user3", "user3@mail.ru", "12345"));
        cities.put(1, new City("Москва"));
        cities.put(2, new City("Санкт-Петербург"));
        cities.put(3, new City("Казань"));
    }

    public static Store instOf() {
        return INST;
    }

    public Properties getConfig() {
        return cfg;
    }

    public Collection<Post> findAllPosts() {
        return posts.values();
    }

    public Collection<Candidate> findAllCandidates() {
        return candidates.values();
    }

    public void save(Post post) {
        if (post.getId() == 0) {
            post.setId(postId.incrementAndGet());
        }
        posts.put(post.getId(), post);
    }

    public void save(Candidate candidate) {
        if (candidate.getId() == 0) {
            candidate.setId(candidateId.incrementAndGet());
        }
        candidates.put(candidate.getId(), candidate);
    }

    public Post findPostById(int id) {
        return posts.get(id);
    }

    public Candidate findCandidateById(int id) {
        return candidates.get(id);
    }

    public void deleteCandidate(int id) {
        Candidate candidate = findCandidateById(id);
        if (candidate != null) {
            candidates.remove(id);
        }
    }

    @Override
    public void clearTables() {
    }

    @Override
    public Collection<User> findAllUsers() {
        return users.values();
    }

    @Override
    public void save(User user) {
        if (user.getId() == 0) {
            user.setId(userId.incrementAndGet());
        }
        users.put(user.getId(), user);
    }

    @Override
    public User findUserById(int id) {
        return users.get(id);
    }

    @Override
    public User findUserByEmail(String email) {
        for (var user : users.values()) {
            if (email.equals(user.getEmail())) {
                return user;
            }
        }
        return null;
    }

    @Override
    public Collection<City> findAllCities() {
        return cities.values();
    }

    @Override
    public City findCityByName(String name) {
        for (var city : cities.values()) {
            if (name.equals(city.getName())) {
                return city;
            }
        }
        return null;
    }

    @Override
    public Collection<Post> findLastDayPosts() {
        List<Post> result = new ArrayList<>();
        for (var post : posts.values()) {
            if (LocalDate.now().equals(post.getCreated())) {
                result.add(post);
            }
        }
        return result;
    }

    @Override
    public Collection<Candidate> findLastDayCandidates() {
        List<Candidate> result = new ArrayList<>();
        for (var candidate : candidates.values()) {
            if (LocalDate.now().equals(candidate.getCreationDate())) {
                result.add(candidate);
            }
        }
        return result;
    }

    @Override
    public City findCityById(int id) {
        return cities.get(id);
    }
}
