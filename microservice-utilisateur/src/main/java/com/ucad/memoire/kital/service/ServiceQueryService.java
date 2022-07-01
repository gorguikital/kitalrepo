package com.ucad.memoire.kital.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.ucad.memoire.kital.repository.ServiceRepository;
import com.ucad.memoire.kital.service.dto.ServiceCriteria;
import com.ucad.memoire.kital.service.dto.ServiceDTO;
import com.ucad.memoire.kital.service.mapper.ServiceMapper;

/**
 * Service for executing complex queries for {@link Service} entities in the database.
 * The main input is a {@link ServiceCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ServiceDTO} or a {@link Page} of {@link ServiceDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ServiceQueryService extends QueryService<Service> {

    private final Logger log = LoggerFactory.getLogger(ServiceQueryService.class);

    private final ServiceRepository serviceRepository;

    private final ServiceMapper serviceMapper;

    public ServiceQueryService(ServiceRepository serviceRepository, ServiceMapper serviceMapper) {
        this.serviceRepository = serviceRepository;
        this.serviceMapper = serviceMapper;
    }

    /**
     * Return a {@link List} of {@link ServiceDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ServiceDTO> findByCriteria(ServiceCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Service> specification = createSpecification(criteria);
        return serviceMapper.toDto(serviceRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ServiceDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ServiceDTO> findByCriteria(ServiceCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Service> specification = createSpecification(criteria);
        return serviceRepository.findAll(specification, page)
            .map(serviceMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ServiceCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Service> specification = createSpecification(criteria);
        return serviceRepository.count(specification);
    }

    /**
     * Function to convert {@link ServiceCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Service> createSpecification(ServiceCriteria criteria) {
        Specification<Service> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Service_.id));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), Service_.code));
            }
            if (criteria.getLibelle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLibelle(), Service_.libelle));
            }
            if (criteria.getDescrition() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescrition(), Service_.descrition));
            }
        }
        return specification;
    }
}
