package com.epam.esm.mapper;

import com.epam.esm.entity.Tag;
import com.epam.esm.model.GiftTag;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TagMapper {
    TagMapper TAG_MAPPER = Mappers.getMapper(TagMapper.class);

    GiftTag tagToGiftTag(Tag tag);

    Tag giftTagToTag(GiftTag giftTag);
}
