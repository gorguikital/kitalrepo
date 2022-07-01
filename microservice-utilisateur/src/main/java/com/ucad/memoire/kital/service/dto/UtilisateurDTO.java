package com.ucad.memoire.kital.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import com.ucad.memoire.kital.domain.enumeration.Sexe;

/**
 * A DTO for the {@link com.ucad.memoire.kital.domain.Utilisateur} entity.
 */
public class UtilisateurDTO implements Serializable {
    
    private Long id;

    @NotNull
    @Size(min = 3, max = 10)
    private String nom;

    @NotNull
    @Size(min = 4, max = 50)
    private String prenom;

    @NotNull
    private Sexe sexe;

    @NotNull
    @Size(min = 7, max = 9)
    private String telephone;

    @NotNull
    private String adresse;

    @Size(min = 10, max = 14)
    private String cin;

    @NotNull
    @Size(min = 5, max = 50)
    private String email;

    @NotNull
    private String password;


    private Long serviceId;
    private Set<ProfileDTO> profiles = new HashSet<>();
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Sexe getSexe() {
        return sexe;
    }

    public void setSexe(Sexe sexe) {
        this.sexe = sexe;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Set<ProfileDTO> getProfiles() {
        return profiles;
    }

    public void setProfiles(Set<ProfileDTO> profiles) {
        this.profiles = profiles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UtilisateurDTO)) {
            return false;
        }

        return id != null && id.equals(((UtilisateurDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UtilisateurDTO{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", prenom='" + getPrenom() + "'" +
            ", sexe='" + getSexe() + "'" +
            ", telephone='" + getTelephone() + "'" +
            ", adresse='" + getAdresse() + "'" +
            ", cin='" + getCin() + "'" +
            ", email='" + getEmail() + "'" +
            ", password='" + getPassword() + "'" +
            ", serviceId=" + getServiceId() +
            ", profiles='" + getProfiles() + "'" +
            "}";
    }
}
