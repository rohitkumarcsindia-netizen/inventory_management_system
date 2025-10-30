package com.project.inventory_management_system.controller;


import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.UserAndOrderId;
import com.project.inventory_management_system.service.UserAndOrderIdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class UserAndOrderIdController
{
    private UserAndOrderIdService userAndOrderIdService;
    public ResponseEntity<?> addUserIdAndOrdreId(UserAndOrderId userAndOrderId)
    {
           UserAndOrderId createUserAndOrderId = userAndOrderIdService.addUserIdAndOrdreId(userAndOrderId);

           if (createUserAndOrderId != null)
           {
               return ResponseEntity.ok(createUserAndOrderId);
           }
           else
               return ResponseEntity.badRequest().body("user annd order Id not save");
    }
}
