package com.epam.esm.mapper;

import com.epam.esm.entity.User;
import com.epam.esm.model.UserGift;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper USER_MAPPER = Mappers.getMapper(UserMapper.class);

    UserGift userToUserGift(User user);

    User userGiftToUser(UserGift userGift);
}
