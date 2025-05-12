package com.MuharremAslan.redis.Service;

import com.MuharremAslan.redis.Model.User;
import com.MuharremAslan.redis.Repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Cacheable(cacheNames = "user_id",key="#root.methodName+ #id",unless = "#result=null")
    public User getUserById(Long id) {
        System.out.println(">>>Fetching user from DB, ID: " + id);
        return userRepository.findById(id).orElse(null); // kullanıcı yoksa null dön
    }

    // value da yazılan cacheler silinecek.
    // allEntries
    @CacheEvict(value = {"users","users_id"},allEntries = true)
    public User addUser(User user){
        return userRepository.save(user);
    }

    @Cacheable(value = "users",key = "#root.methodName",unless ="#result==null " ) // unless hiç data dönmezse
    public List<User> getUsers() {
        return userRepository.findAll();
    }


    @CachePut(cacheNames = "user_id",key = "#root.methodName+#id")
    public User updateUserById(User user, Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isPresent()){
            User updatedUser = userOptional.get();
            return userRepository.save(updatedUser);
        }
            return null;
    }
    @CacheEvict(value = {"users","users_id"},allEntries = true)
    public void deleteUserById(Long id) {
         userRepository.deleteById(id);
    }
}
