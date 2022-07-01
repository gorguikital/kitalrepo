package com.ucad.memoire.kital.service.mapper;


import com.ucad.memoire.kital.domain.*;
import com.ucad.memoire.kital.service.dto.UtilisateurDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Utilisateur} and its DTO {@link UtilisateurDTO}.
 */
@Mapper(componentModel = "spring", uses = {ServiceMapper.class, ProfileMapper.class})
public interface UtilisateurMapper extends EntityMapper<UtilisateurDTO, Utilisateur> {

    @Mapping(source = "service.id", target = "serviceId")
    UtilisateurDTO toDto(Utilisateur utilisateur);

    @Mapping(source = "serviceId", target = "service")
    @Mapping(target = "removeProfile", ignore = true)
    Utilisateur toEntity(UtilisateurDTO utilisateurDTO);

    default Utilisateur fromId(Long id) {
        if (id == null) {
            return null;
        }
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(id);
        return utilisateur;
    }
}
