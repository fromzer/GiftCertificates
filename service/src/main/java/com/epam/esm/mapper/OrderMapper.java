package com.epam.esm.mapper;

import com.epam.esm.entity.Order;
import com.epam.esm.model.GiftOrder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderMapper {

    OrderMapper ORDER_MAPPER = Mappers.getMapper(OrderMapper.class);

    GiftOrder orderToGiftOrder(Order order);

    Order giftOrderToOrder(GiftOrder giftOrder);
}
