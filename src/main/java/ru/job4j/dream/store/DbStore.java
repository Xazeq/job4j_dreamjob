package ru.job4j.dream.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.model.City;
import ru.job4j.dream.model.Post;
import ru.job4j.dream.model.User;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class DbStore implements Store {
    private static final DbStore INSTANCE = new DbStore();
    private final BasicDataSource pool = new BasicDataSource();
    private static final Logger LOG = LoggerFactory.getLogger(DbStore.class.getName());
    private final Properties appCfg = new Properties();

    private DbStore() {
        Properties cfg = new Properties();
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        DbStore.class.getClassLoader()
                                .getResourceAsStream("db.properties")
                )
        );
             InputStream appIn = DbStore.class.getClassLoader()
                     .getResourceAsStream("app.properties")) {
            cfg.load(in);
            appCfg.load(appIn);
        } catch (Exception e) {
            LOG.error("Exception in DbStore constructor", e);
        }
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
    }

    private static final class Lazy {
        private static final Store INST = new DbStore();
    }

    public static Store instOf() {
        return Lazy.INST;
    }

    public Properties getConfig() {
        return appCfg;
    }

    @Override
    public Collection<Post> findAllPosts() {
        List<Post> posts = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM post")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    posts.add(new Post(it.getInt("id"), it.getString("name"),
                            it.getString("description"),
                            it.getDate("creation_time").toLocalDate()));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in findAllPosts method", e);
        }
        return posts;
    }

    @Override
    public Collection<Candidate> findAllCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        try (Connection cn = pool.getConnection();
            PreparedStatement ps = cn.prepareStatement(
                    "SELECT c.id as c_id, c.name as c_name, c.creation_time as c_creation_time, city.id as city_id, city.name as city_name "
                            + "FROM candidate c "
                            + "JOIN city ON city.id = c.city_id"
            )
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    candidates.add(new Candidate(it.getInt("c_id"), it.getString("c_name"),
                            new City(it.getInt("city_id"), it.getString("city_name")),
                            it.getDate("c_creation_time").toLocalDate()));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in findAllCandidates method", e);
        }
        return candidates;
    }

    @Override
    public void save(Post post) {
        if (post.getId() == 0) {
            create(post);
        } else {
            update(post);
        }
    }

    private Post create(Post post) {
        try (Connection cn = pool.getConnection();
            PreparedStatement ps = cn.prepareStatement(
                    "INSERT INTO post(name, description, creation_time) VALUES (?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, post.getName());
            ps.setString(2, post.getDescription());
            ps.setDate(3, Date.valueOf(post.getCreated()));
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    post.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in create method", e);
        }
        return post;
    }

    private void update(Post post) {
        try (Connection cn = pool.getConnection();
            PreparedStatement ps = cn.prepareStatement("UPDATE post SET name = ?, description = ? WHERE id = ?")
        ) {
            ps.setString(1, post.getName());
            ps.setString(2, post.getDescription());
            ps.setInt(3, post.getId());
            ps.execute();
        } catch (Exception e) {
            LOG.error("Exception in update method", e);
        }
    }

    @Override
    public void save(Candidate candidate) {
        if (candidate.getId() == 0) {
            create(candidate);
        } else {
            update(candidate);
        }
    }

    private Candidate create(Candidate candidate) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("INSERT INTO candidate(name, city_id, creation_time) VALUES (?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, candidate.getName());
            ps.setInt(2, candidate.getCity().getId());
            ps.setDate(3, Date.valueOf(candidate.getCreationDate()));
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    candidate.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in create method", e);
        }
        return candidate;
    }

    private void update(Candidate candidate) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("UPDATE candidate SET name = ?, city_id = ? WHERE id = ?")
        ) {
            ps.setString(1, candidate.getName());
            ps.setInt(2, candidate.getCity().getId());
            ps.setInt(3, candidate.getId());
            ps.execute();
        } catch (Exception e) {
            LOG.error("Exception in update method", e);
        }
    }

    @Override
    public Post findPostById(int id) {
        try (Connection cn = pool.getConnection();
            PreparedStatement ps = cn.prepareStatement("SELECT * FROM post WHERE id = ?")
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return new Post(it.getInt("id"), it.getString("name"),
                            it.getString("description"),
                            it.getDate("creation_time").toLocalDate());
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in findPostById method", e);
        }
        return null;
    }

    @Override
    public Candidate findCandidateById(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT c.id as c_id, c.name as c_name, c.creation_time as c_creation_time, city.id as city_id, city.name as city_name "
                     + "FROM candidate c "
                     + "JOIN city ON city.id = c.city_id "
                     + "WHERE c.id = ?")
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return new Candidate(it.getInt("c_id"), it.getString("c_name"),
                            new City(it.getInt("city_id"), it.getString("city_name")),
                            it.getDate("c_creation_time").toLocalDate());
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in findCandidateById method", e);
        }
        return null;
    }

    @Override
    public void deleteCandidate(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("DELETE FROM candidate WHERE id = ?")
        ) {
            ps.setInt(1, id);
            ps.execute();
        } catch (Exception e) {
            LOG.error("Exception in deleteCandidate method", e);
        }
    }

    @Override
    public void clearTables() {
        try (Connection cn = pool.getConnection();
             Statement statement = cn.createStatement()
        ) {
            statement.execute("TRUNCATE TABLE post RESTART IDENTITY");
            statement.execute("TRUNCATE TABLE candidate RESTART IDENTITY");
        } catch (Exception e) {
            LOG.error("Exception in clearTable method", e);
        }
    }

    @Override
    public Collection<User> findAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM users")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    users.add(new User(it.getInt("id"), it.getString("name"),
                            it.getString("email"), it.getString("password")));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in findAllUsers method", e);
        }
        return users;
    }

    @Override
    public void save(User user) {
        if (user.getId() == 0) {
            create(user);
        } else {
            update(user);
        }
    }


    private User create(User user) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("INSERT INTO users(name, email, password) VALUES (?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    user.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in create method", e);
        }
        return user;
    }

    private void update(User user) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "UPDATE users SET name = ?, email = ?, password = ? WHERE id = ?")
        ) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setInt(4, user.getId());
            ps.execute();
        } catch (Exception e) {
            LOG.error("Exception in update method", e);
        }
    }

    @Override
    public User findUserById(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM users WHERE id = ?")
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return new User(it.getInt("id"), it.getString("name"),
                            it.getString("email"), it.getString("password"));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in findUserById method", e);
        }
        return null;
    }

    @Override
    public User findUserByEmail(String email) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM users WHERE email = ?")
        ) {
            ps.setString(1, email);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return new User(it.getInt("id"), it.getString("name"),
                            it.getString("email"), it.getString("password"));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in findUserByEmail method", e);
        }
        return null;
    }

    @Override
    public Collection<City> findAllCities() {
        List<City> cities = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM city")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    cities.add(new City(it.getInt("id"), it.getString("name")));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in findAllCities method", e);
        }
        return cities;
    }

    @Override
    public City findCityByName(String name) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM city WHERE name = ?")
        ) {
            ps.setString(1, name);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return new City(it.getInt("id"), it.getString("name"));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in findCityByName method", e);
        }
        return null;
    }

    @Override
    public Collection<Post> findLastDayPosts() {
        List<Post> posts = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT * FROM post WHERE creation_time BETWEEN current_date - interval '1 day' AND current_date")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    posts.add(new Post(it.getInt("id"), it.getString("name"),
                            it.getString("description"),
                            it.getDate("creation_time").toLocalDate()));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in findLastDayPosts method", e);
        }
        return posts;
    }

    @Override
    public Collection<Candidate> findLastDayCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT c.id as c_id, c.name as c_name, c.creation_time as c_creation_time, city.id as city_id, city.name as city_name "
                             + "FROM candidate c "
                             + "JOIN city ON city.id = c.city_id "
                             + "WHERE c.creation_time BETWEEN current_date - interval '1 day' AND current_date")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    candidates.add(new Candidate(it.getInt("c_id"), it.getString("c_name"),
                            new City(it.getInt("city_id"), it.getString("city_name")),
                            it.getDate("c_creation_time").toLocalDate()));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in findLastDayCandidates method", e);
        }
        return candidates;
    }

    @Override
    public City findCityById(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM city WHERE id = ?")
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    return new City(it.getInt("id"), it.getString("name"));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in findCityById method", e);
        }
        return null;
    }
}
