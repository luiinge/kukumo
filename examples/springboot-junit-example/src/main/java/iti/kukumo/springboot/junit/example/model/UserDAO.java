package iti.kukumo.springboot.junit.example.model;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserDAO {

    @PersistenceContext 
    private EntityManager entityManager;
    
    
    public List<User> getAllUsers() {
        return entityManager.createQuery("select u from User u",User.class).getResultList();
    }
    
    public User getUserById(int id) {
        return entityManager.find(User.class, id);
    }
    
    public boolean userExists(int id) {
        return getUserById(id) != null;
    }
    
    @Transactional
    public User createUser(User user) {
        entityManager.persist(user);
        return user;
    }
    
    public void deleteUser(int id) {
        User user = getUserById(id);
        if (user == null) {
            throw new EntityNotFoundException();
        }
        entityManager.remove(user);
    }
    
    public User modifyUser(int id, User user) {
        if (!userExists(id)) {
            throw new EntityNotFoundException();
        }
        user.id = id;
        return entityManager.merge(user);
        
    }
    
}
