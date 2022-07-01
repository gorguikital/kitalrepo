package com.ucad.memoire.kital.service.mapper;


import com.ucad.memoire.kital.domain.*;
import com.ucad.memoire.kital.service.dto.ServiceDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Service} and its DTO {@link ServiceDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ServiceMapper extends EntityMapper<ServiceDTO, Service> {



    default Service fromId(Long id) {
        if (id == null) {
            return null;
        }
        Service service = new Service();
        service.setId(id);
        return service;
    }
}
