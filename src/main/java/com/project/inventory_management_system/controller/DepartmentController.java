//package com.project.inventory_management_system.controller;
//
//import com.project.inventory_management_system.entity.Department;
//import com.project.inventory_management_system.service.DepartmentService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/department")
//@RequiredArgsConstructor
//public class DepartmentController
//{
//    private final DepartmentService departmentService;
//
//    @PostMapping("/createdepartment")
//    public ResponseEntity<?> addDepartment(@RequestBody Department department)
//    {
//        Department cereateDepartment = departmentService.addDepartment(department);
//        if (cereateDepartment.getId() != null)
//        {
//            System.out.println(cereateDepartment.getId());
//            return ResponseEntity.ok(cereateDepartment);
//        }
//        else
//            return ResponseEntity.badRequest().body("Department not Create");
//
//    }
//
//
////    @PutMapping("/updatedepartment")
////    public ResponseEntity<?> updateDepartment(@RequestBody Department department)
////    {
////        Department updateDepartment = departmentService.updateDepartment(department);
////        if (updateDepartment != null)
////        {
////            return ResponseEntity.ok("Department Update Successfully");
////        }
////        else
////            return ResponseEntity.badRequest().body("Department Not Update");
////    }
////
////    @DeleteMapping("/deletedepartment")
////    public ResponseEntity<?> deleteDepartment(@RequestBody Department department)
////    {
////        Department deleteDepartment = departmentService.deleteDepartment(department);
////        if (deleteDepartment != null)
////        {
////            return ResponseEntity.ok("Department De Successfully");
////        }
////
////    }
//}
