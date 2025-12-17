package org.yearup.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;

import java.security.Principal;

@CrossOrigin
@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileDao profileDao;
    private final UserDao userDao;

    public ProfileController(ProfileDao profileDao, UserDao userDao) {
        this.profileDao = profileDao;
        this.userDao = userDao;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Profile getProfile(Principal principal){

        try {
            String username = principal.getName();
            int userId = userDao.getIdByUsername(username);

            Profile profile = profileDao.getByUserId(userId);
            if (profile == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);

            return profile;
        }
        catch (ResponseStatusException e){
            throw e;
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Oops .... our bad.");
        }
    }


}
