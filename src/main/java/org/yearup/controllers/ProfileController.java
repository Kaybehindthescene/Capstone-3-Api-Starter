package org.yearup.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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
    @PutMapping
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProfile(@RequestBody Profile profile,Principal principal){

        try {
            String userName = principal.getName();
            int userId = userDao.getIdByUsername(userName);

            //Force the profile to belong to the logged-in user
            profile.setUserId(userId);

            Profile existing = profileDao.getByUserId(userId);
            if (existing == null){
                profileDao.create(profile);
            }
            else {
                profileDao.update(userId,profile);
            }
        }
        catch (ResponseStatusException e){
            throw e;
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

}
