package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.ProfileDao;
import org.yearup.models.Profile;

import java.security.Principal;

@RestController
@RequestMapping("profile")
@CrossOrigin
@PreAuthorize("isAuthenticated()")
public class ProfileController {
    @Autowired
    private ProfileDao profileDao;

    @GetMapping
    public Profile getProfile(Principal principal){
        return profileDao.get(principal.getName());
    }

    @PostMapping
    public Profile updateProfile(Principal principal, @RequestBody Profile profile){
        return profileDao.update(principal.getName(), profile);
    }
}
