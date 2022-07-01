package com.ucad.memoire.kital.service.mapper;


import com.ucad.memoire.kital.domain.*;
import com.ucad.memoire.kital.service.dto.ProfileDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Profile} and its DTO {@link ProfileDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ProfileMapper extends EntityMapper<ProfileDTO, Profile> {


    @Mapping(target = "utilisateurs", ignore = true)
    @Mapping(target = "removeUtilisateur", ignore = true)
    Profile toEntity(ProfileDTO profileDTO);

    default Profile fromId(Long id) {
        if (id == null) {
            return null;
        }
        Profile profile = new Profile();
        profile.setId(id);
        return profile;
    }
}
