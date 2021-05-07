package com.epam.esm.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import javax.persistence.Transient;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersistentObject implements Persistable<Long>, Serializable {

    private Long id;

    @Override
    public Long getId() {
        return id;
    }

    @Transient
    @Override
    public boolean isNew() {
        return id == null;
    }
}
