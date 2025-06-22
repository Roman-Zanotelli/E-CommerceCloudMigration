package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;

import java.security.Principal;

@RestController
@RequestMapping("profile")
@CrossOrigin
@PreAuthorize("isAuthenticated()")
public class ProfileController {
    @Autowired
    private ProfileDao profileDao;
    @Autowired
    private UserDao userDao;

    @GetMapping
    public Profile getProfile(Principal principal){
        return profileDao.getByUserId(userDao.getIdByUsername(principal.getName()));
    }

    @PostMapping
    public Profile updateProfile(Principal principal, @RequestBody Profile profile){
        return profileDao.update(userDao.getIdByUsername(principal.getName()), profile);
    }
}
