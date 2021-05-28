package com.epam.esm.mapper;

import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.ModifiedGiftCertificate;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ModifiedCertificateMapper {
    ModifiedCertificateMapper MODIFIED_CERTIFICATE_MAPPER = Mappers.getMapper(ModifiedCertificateMapper.class);

    GiftCertificate modifiedGiftCertificateToGiftCertificate(ModifiedGiftCertificate modifiedGiftCertificate);
}
