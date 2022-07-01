package com.ucad.memoire.kital.service;

import com.ucad.memoire.kital.service.dto.UtilisateurDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link com.ucad.memoire.kital.domain.Utilisateur}.
 */
public interface UtilisateurService {

    /**
     * Save a utilisateur.
     *
     * @param utilisateurDTO the entity to save.
     * @return the persisted entity.
     */
    UtilisateurDTO save(UtilisateurDTO utilisateurDTO);

    /**
     * Get all the utilisateurs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<UtilisateurDTO> findAll(Pageable pageable);

    /**
     * Get all the utilisateurs with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    Page<UtilisateurDTO> findAllWithEagerRelationships(Pageable pageable);


    /**
     * Get the "id" utilisateur.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<UtilisateurDTO> findOne(Long id);

    /**
     * Delete the "id" utilisateur.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
