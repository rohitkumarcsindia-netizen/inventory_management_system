package com.project.inventory_management_system.service;

import com.project.inventory_management_system.entity.Orders;
import com.project.inventory_management_system.entity.UserAndOrderId;
import com.project.inventory_management_system.repository.UserAndOrderIdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserAndOrderIdServiceImpl implements UserAndOrderIdService
{
    //private final Orders orders;
    //private final UserAndOrderId userAndOrderId2;
    private final UserAndOrderIdRepository userAndOrderIdRepository;

    @Override
    public UserAndOrderId addUserIdAndOrdreId(UserAndOrderId userAndOrderId)
    {
        return userAndOrderIdRepository.save(userAndOrderId);
    }
}
