package com.epam.esm.mapper;

import com.epam.esm.entity.User;
import com.epam.esm.model.RegisteredUser;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RegisteredUserMapper {
    RegisteredUserMapper REGISTER_USER_MAPPER = Mappers.getMapper(RegisteredUserMapper.class);

    User registeredUserToUser(RegisteredUser registeredUser);
}
