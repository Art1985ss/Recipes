package recipes;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.validation.*;
import java.util.NoSuchElementException;
import java.util.Set;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final Validator validator;
    private final ExampleMatcher userMather;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        userMather = ExampleMatcher.matching()
                .withIgnorePaths("id")
                .withIgnoreCase("email");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user could be found with email " + username));
        return new UserDetailsImpl(user);
    }

    public void save(User user) throws ValidationException {
        user.setRoles("ROLE_USER");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            ConstraintViolation<User> violation = violations.stream().findFirst().get();
            throw new ValidationException("User could not be registered : " + violation.getPropertyPath() + " " + violation.getMessage());
        }
        if (exists(user)) {
            throw new ValidationException("User with such email already exists!");
        }
        userRepository.save(user);
    }

    private boolean exists(User user) {
        Example<User> userExample = Example.of(user, userMather);
        return userRepository.exists(userExample);
    }

    public User findByEmail(String email) throws NoSuchElementException {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User with email " + email + " does not exists "));
    }
}
