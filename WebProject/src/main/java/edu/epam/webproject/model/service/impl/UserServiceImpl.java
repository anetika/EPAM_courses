package edu.epam.webproject.model.service.impl;

import edu.epam.webproject.entity.User;
import edu.epam.webproject.exception.DaoException;
import edu.epam.webproject.exception.ServiceException;
import edu.epam.webproject.model.dao.UserDao;
import edu.epam.webproject.model.dao.impl.UserDaoImpl;
import edu.epam.webproject.model.service.UserService;
import edu.epam.webproject.util.PasswordEncryptor;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {
    private final UserDao userDao = UserDaoImpl.getInstance();
    private static final UserServiceImpl instance = new UserServiceImpl();

    private UserServiceImpl(){}

    public static UserServiceImpl getInstance(){
        return instance;
    }

    @Override
    public Optional<User> signIn(String email, String password) throws ServiceException {
        try {
            return userDao.signIn(email, password);
        } catch (DaoException e) {
            throw new ServiceException("Unable to execute a signIn request", e);
        }
    }

    @Override
    public Optional<User> signUp(String login, String email, String password) throws ServiceException {
        Optional<User> optionalUser;
        try {
            PasswordEncryptor encryptor = PasswordEncryptor.getInstance();
            String hashedPassword = encryptor.getHashedPassword(password);
            if (userDao.signUp(login, email, hashedPassword)){
                optionalUser = userDao.signIn(email, password);
            }else {
                optionalUser = Optional.empty();
            }
            return optionalUser;
        } catch (DaoException e) {
            throw new ServiceException("Unable to execute a signUp request", e);
        }
    }

    @Override
    public List<User> findAllUsers() throws ServiceException {
        try {
            return userDao.findAllUsers();
        } catch (DaoException e) {
            throw new ServiceException("Unable to execute a findAllUsers request", e);
        }
    }

    @Override
    public void activateUserByEmail(String email) throws ServiceException {
        try {
            userDao.changeUserStatusByEmail(email, User.UserStatus.APPROVED.getValue());
        } catch (DaoException e) {
            throw new ServiceException("Unable to execute an activateUserByEmail request", e);
        }
    }
}