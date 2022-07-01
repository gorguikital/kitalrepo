package com.ucad.memoire.kital.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * A DTO for the {@link com.ucad.memoire.kital.domain.Service} entity.
 */
public class ServiceDTO implements Serializable {
    
    private Long id;

    @NotNull
    @Size(min = 3, max = 8)
    private String code;

    @NotNull
    @Size(min = 3, max = 50)
    private String libelle;

    private String descrition;

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getDescrition() {
        return descrition;
    }

    public void setDescrition(String descrition) {
        this.descrition = descrition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServiceDTO)) {
            return false;
        }

        return id != null && id.equals(((ServiceDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ServiceDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", libelle='" + getLibelle() + "'" +
            ", descrition='" + getDescrition() + "'" +
            "}";
    }
}
