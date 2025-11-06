package com.project.inventory_management_system.service;


import com.project.inventory_management_system.entity.Users;
import com.project.inventory_management_system.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UserDetailServiceImpl implements UserDetailsService
{
    @Autowired
    private UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        Users user = usersRepository.findByUsername(username);
        if (user == null)
        {
            throw new UsernameNotFoundException("User not found");

        }
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()).build();
//                .roles(user.getUserRoles().stream()
//                        .map(userRoles -> userRoles.getRole().getRoleName())
//                        .collect(Collectors.toList())
//                        .toArray(new String[0])).build();
    }
}
