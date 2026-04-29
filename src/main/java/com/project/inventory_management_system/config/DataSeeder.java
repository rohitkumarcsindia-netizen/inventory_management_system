package com.project.inventory_management_system.config;

import com.project.inventory_management_system.entity.Department;
import com.project.inventory_management_system.entity.Users;
import com.project.inventory_management_system.repository.DepartmentRepository;
import com.project.inventory_management_system.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataSeeder
{

    @Bean
    CommandLineRunner seedData(
            DepartmentRepository departmentRepository,
            UsersRepository usersRepository,
            PasswordEncoder passwordEncoder
    ) {

        return args -> {

            // First time departments seed
            if (departmentRepository.count() == 0) {

                saveDepartment(departmentRepository,"Admin","admin@gmail.com");
                saveDepartment(departmentRepository,"Project Team","nareshsingh9oaz9o9@gmail.com");
                saveDepartment(departmentRepository,"Finance","rohitkumar.csindia@gmail.com");
                saveDepartment(departmentRepository,"SCM","shubhamkumar10510s@gmail.com");
                saveDepartment(departmentRepository,"Cloud Team","rohitkumar212721@gmail.com");
                saveDepartment(departmentRepository,"Syrma","rohitkumar.csindia.syrma@gmail.com");
                saveDepartment(departmentRepository,"RMA","rohitkumar.csindia.rma@gmail.com");
                saveDepartment(departmentRepository,"Logistic","rohitkumar.csindia.logistic@gmail.com");
                saveDepartment(departmentRepository,"AUDITOR","auditor@gmail.com");
            }

            // First admin user seed
            if (usersRepository.count() == 0) {

                Department adminDept =
                        departmentRepository
                                .findByDepartmentName("Admin");

                Users admin = new Users();
                admin.setEmail("admin@gmail.com");
                admin.setUsername("admin");

                // login password = 12345
                admin.setPassword(
                        passwordEncoder.encode("12345")
                );

                admin.setDepartment(adminDept);

                usersRepository.save(admin);
            }

        };
    }


    private void saveDepartment(
            DepartmentRepository repo,
            String name,
            String email
    ){
        Department dept = new Department();
        dept.setDepartmentName(name);
        dept.setDepartmentEmail(email);
        repo.save(dept);
    }

}