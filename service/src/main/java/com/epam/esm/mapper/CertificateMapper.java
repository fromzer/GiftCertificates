package com.epam.esm.mapper;

import com.epam.esm.entity.Certificate;
import com.epam.esm.model.GiftCertificate;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel="spring")
public interface CertificateMapper {
    CertificateMapper CERTIFICATE_MAPPER = Mappers.getMapper(CertificateMapper.class);

    GiftCertificate certificateToGiftCertificate(Certificate certificate);

    Certificate giftCertificateToCertificate(GiftCertificate giftCertificate);
}
