package com.ucad.memoire.kital.web.rest;

import com.ucad.memoire.kital.UtilisateurdbApp;
import com.ucad.memoire.kital.config.TestSecurityConfiguration;
import com.ucad.memoire.kital.domain.Service;
import com.ucad.memoire.kital.repository.ServiceRepository;
import com.ucad.memoire.kital.service.ServiceService;
import com.ucad.memoire.kital.service.dto.ServiceDTO;
import com.ucad.memoire.kital.service.mapper.ServiceMapper;
import com.ucad.memoire.kital.service.dto.ServiceCriteria;
import com.ucad.memoire.kital.service.ServiceQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ServiceResource} REST controller.
 */
@SpringBootTest(classes = { UtilisateurdbApp.class, TestSecurityConfiguration.class })
@AutoConfigureMockMvc
@WithMockUser
public class ServiceResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBB";

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRITION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRITION = "BBBBBBBBBB";

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ServiceMapper serviceMapper;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private ServiceQueryService serviceQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restServiceMockMvc;

    private Service service;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Service createEntity(EntityManager em) {
        Service service = new Service()
            .code(DEFAULT_CODE)
            .libelle(DEFAULT_LIBELLE)
            .descrition(DEFAULT_DESCRITION);
        return service;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Service createUpdatedEntity(EntityManager em) {
        Service service = new Service()
            .code(UPDATED_CODE)
            .libelle(UPDATED_LIBELLE)
            .descrition(UPDATED_DESCRITION);
        return service;
    }

    @BeforeEach
    public void initTest() {
        service = createEntity(em);
    }

    @Test
    @Transactional
    public void createService() throws Exception {
        int databaseSizeBeforeCreate = serviceRepository.findAll().size();
        // Create the Service
        ServiceDTO serviceDTO = serviceMapper.toDto(service);
        restServiceMockMvc.perform(post("/api/services").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(serviceDTO)))
            .andExpect(status().isCreated());

        // Validate the Service in the database
        List<Service> serviceList = serviceRepository.findAll();
        assertThat(serviceList).hasSize(databaseSizeBeforeCreate + 1);
        Service testService = serviceList.get(serviceList.size() - 1);
        assertThat(testService.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testService.getLibelle()).isEqualTo(DEFAULT_LIBELLE);
        assertThat(testService.getDescrition()).isEqualTo(DEFAULT_DESCRITION);
    }

    @Test
    @Transactional
    public void createServiceWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = serviceRepository.findAll().size();

        // Create the Service with an existing ID
        service.setId(1L);
        ServiceDTO serviceDTO = serviceMapper.toDto(service);

        // An entity with an existing ID cannot be created, so this API call must fail
        restServiceMockMvc.perform(post("/api/services").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(serviceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Service in the database
        List<Service> serviceList = serviceRepository.findAll();
        assertThat(serviceList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = serviceRepository.findAll().size();
        // set the field null
        service.setCode(null);

        // Create the Service, which fails.
        ServiceDTO serviceDTO = serviceMapper.toDto(service);


        restServiceMockMvc.perform(post("/api/services").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(serviceDTO)))
            .andExpect(status().isBadRequest());

        List<Service> serviceList = serviceRepository.findAll();
        assertThat(serviceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLibelleIsRequired() throws Exception {
        int databaseSizeBeforeTest = serviceRepository.findAll().size();
        // set the field null
        service.setLibelle(null);

        // Create the Service, which fails.
        ServiceDTO serviceDTO = serviceMapper.toDto(service);


        restServiceMockMvc.perform(post("/api/services").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(serviceDTO)))
            .andExpect(status().isBadRequest());

        List<Service> serviceList = serviceRepository.findAll();
        assertThat(serviceList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllServices() throws Exception {
        // Initialize the database
        serviceRepository.saveAndFlush(service);

        // Get all the serviceList
        restServiceMockMvc.perform(get("/api/services?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(service.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].descrition").value(hasItem(DEFAULT_DESCRITION)));
    }
    
    @Test
    @Transactional
    public void getService() throws Exception {
        // Initialize the database
        serviceRepository.saveAndFlush(service);

        // Get the service
        restServiceMockMvc.perform(get("/api/services/{id}", service.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(service.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE))
            .andExpect(jsonPath("$.descrition").value(DEFAULT_DESCRITION));
    }


    @Test
    @Transactional
    public void getServicesByIdFiltering() throws Exception {
        // Initialize the database
        serviceRepository.saveAndFlush(service);

        Long id = service.getId();

        defaultServiceShouldBeFound("id.equals=" + id);
        defaultServiceShouldNotBeFound("id.notEquals=" + id);

        defaultServiceShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultServiceShouldNotBeFound("id.greaterThan=" + id);

        defaultServiceShouldBeFound("id.lessThanOrEqual=" + id);
        defaultServiceShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllServicesByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        serviceRepository.saveAndFlush(service);

        // Get all the serviceList where code equals to DEFAULT_CODE
        defaultServiceShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the serviceList where code equals to UPDATED_CODE
        defaultServiceShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllServicesByCodeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        serviceRepository.saveAndFlush(service);

        // Get all the serviceList where code not equals to DEFAULT_CODE
        defaultServiceShouldNotBeFound("code.notEquals=" + DEFAULT_CODE);

        // Get all the serviceList where code not equals to UPDATED_CODE
        defaultServiceShouldBeFound("code.notEquals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllServicesByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        serviceRepository.saveAndFlush(service);

        // Get all the serviceList where code in DEFAULT_CODE or UPDATED_CODE
        defaultServiceShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the serviceList where code equals to UPDATED_CODE
        defaultServiceShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllServicesByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        serviceRepository.saveAndFlush(service);

        // Get all the serviceList where code is not null
        defaultServiceShouldBeFound("code.specified=true");

        // Get all the serviceList where code is null
        defaultServiceShouldNotBeFound("code.specified=false");
    }
                @Test
    @Transactional
    public void getAllServicesByCodeContainsSomething() throws Exception {
        // Initialize the database
        serviceRepository.saveAndFlush(service);

        // Get all the serviceList where code contains DEFAULT_CODE
        defaultServiceShouldBeFound("code.contains=" + DEFAULT_CODE);

        // Get all the serviceList where code contains UPDATED_CODE
        defaultServiceShouldNotBeFound("code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllServicesByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        serviceRepository.saveAndFlush(service);

        // Get all the serviceList where code does not contain DEFAULT_CODE
        defaultServiceShouldNotBeFound("code.doesNotContain=" + DEFAULT_CODE);

        // Get all the serviceList where code does not contain UPDATED_CODE
        defaultServiceShouldBeFound("code.doesNotContain=" + UPDATED_CODE);
    }


    @Test
    @Transactional
    public void getAllServicesByLibelleIsEqualToSomething() throws Exception {
        // Initialize the database
        serviceRepository.saveAndFlush(service);

        // Get all the serviceList where libelle equals to DEFAULT_LIBELLE
        defaultServiceShouldBeFound("libelle.equals=" + DEFAULT_LIBELLE);

        // Get all the serviceList where libelle equals to UPDATED_LIBELLE
        defaultServiceShouldNotBeFound("libelle.equals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    public void getAllServicesByLibelleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        serviceRepository.saveAndFlush(service);

        // Get all the serviceList where libelle not equals to DEFAULT_LIBELLE
        defaultServiceShouldNotBeFound("libelle.notEquals=" + DEFAULT_LIBELLE);

        // Get all the serviceList where libelle not equals to UPDATED_LIBELLE
        defaultServiceShouldBeFound("libelle.notEquals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    public void getAllServicesByLibelleIsInShouldWork() throws Exception {
        // Initialize the database
        serviceRepository.saveAndFlush(service);

        // Get all the serviceList where libelle in DEFAULT_LIBELLE or UPDATED_LIBELLE
        defaultServiceShouldBeFound("libelle.in=" + DEFAULT_LIBELLE + "," + UPDATED_LIBELLE);

        // Get all the serviceList where libelle equals to UPDATED_LIBELLE
        defaultServiceShouldNotBeFound("libelle.in=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    public void getAllServicesByLibelleIsNullOrNotNull() throws Exception {
        // Initialize the database
        serviceRepository.saveAndFlush(service);

        // Get all the serviceList where libelle is not null
        defaultServiceShouldBeFound("libelle.specified=true");

        // Get all the serviceList where libelle is null
        defaultServiceShouldNotBeFound("libelle.specified=false");
    }
                @Test
    @Transactional
    public void getAllServicesByLibelleContainsSomething() throws Exception {
        // Initialize the database
        serviceRepository.saveAndFlush(service);

        // Get all the serviceList where libelle contains DEFAULT_LIBELLE
        defaultServiceShouldBeFound("libelle.contains=" + DEFAULT_LIBELLE);

        // Get all the serviceList where libelle contains UPDATED_LIBELLE
        defaultServiceShouldNotBeFound("libelle.contains=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    public void getAllServicesByLibelleNotContainsSomething() throws Exception {
        // Initialize the database
        serviceRepository.saveAndFlush(service);

        // Get all the serviceList where libelle does not contain DEFAULT_LIBELLE
        defaultServiceShouldNotBeFound("libelle.doesNotContain=" + DEFAULT_LIBELLE);

        // Get all the serviceList where libelle does not contain UPDATED_LIBELLE
        defaultServiceShouldBeFound("libelle.doesNotContain=" + UPDATED_LIBELLE);
    }


    @Test
    @Transactional
    public void getAllServicesByDescritionIsEqualToSomething() throws Exception {
        // Initialize the database
        serviceRepository.saveAndFlush(service);

        // Get all the serviceList where descrition equals to DEFAULT_DESCRITION
        defaultServiceShouldBeFound("descrition.equals=" + DEFAULT_DESCRITION);

        // Get all the serviceList where descrition equals to UPDATED_DESCRITION
        defaultServiceShouldNotBeFound("descrition.equals=" + UPDATED_DESCRITION);
    }

    @Test
    @Transactional
    public void getAllServicesByDescritionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        serviceRepository.saveAndFlush(service);

        // Get all the serviceList where descrition not equals to DEFAULT_DESCRITION
        defaultServiceShouldNotBeFound("descrition.notEquals=" + DEFAULT_DESCRITION);

        // Get all the serviceList where descrition not equals to UPDATED_DESCRITION
        defaultServiceShouldBeFound("descrition.notEquals=" + UPDATED_DESCRITION);
    }

    @Test
    @Transactional
    public void getAllServicesByDescritionIsInShouldWork() throws Exception {
        // Initialize the database
        serviceRepository.saveAndFlush(service);

        // Get all the serviceList where descrition in DEFAULT_DESCRITION or UPDATED_DESCRITION
        defaultServiceShouldBeFound("descrition.in=" + DEFAULT_DESCRITION + "," + UPDATED_DESCRITION);

        // Get all the serviceList where descrition equals to UPDATED_DESCRITION
        defaultServiceShouldNotBeFound("descrition.in=" + UPDATED_DESCRITION);
    }

    @Test
    @Transactional
    public void getAllServicesByDescritionIsNullOrNotNull() throws Exception {
        // Initialize the database
        serviceRepository.saveAndFlush(service);

        // Get all the serviceList where descrition is not null
        defaultServiceShouldBeFound("descrition.specified=true");

        // Get all the serviceList where descrition is null
        defaultServiceShouldNotBeFound("descrition.specified=false");
    }
                @Test
    @Transactional
    public void getAllServicesByDescritionContainsSomething() throws Exception {
        // Initialize the database
        serviceRepository.saveAndFlush(service);

        // Get all the serviceList where descrition contains DEFAULT_DESCRITION
        defaultServiceShouldBeFound("descrition.contains=" + DEFAULT_DESCRITION);

        // Get all the serviceList where descrition contains UPDATED_DESCRITION
        defaultServiceShouldNotBeFound("descrition.contains=" + UPDATED_DESCRITION);
    }

    @Test
    @Transactional
    public void getAllServicesByDescritionNotContainsSomething() throws Exception {
        // Initialize the database
        serviceRepository.saveAndFlush(service);

        // Get all the serviceList where descrition does not contain DEFAULT_DESCRITION
        defaultServiceShouldNotBeFound("descrition.doesNotContain=" + DEFAULT_DESCRITION);

        // Get all the serviceList where descrition does not contain UPDATED_DESCRITION
        defaultServiceShouldBeFound("descrition.doesNotContain=" + UPDATED_DESCRITION);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultServiceShouldBeFound(String filter) throws Exception {
        restServiceMockMvc.perform(get("/api/services?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(service.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].descrition").value(hasItem(DEFAULT_DESCRITION)));

        // Check, that the count call also returns 1
        restServiceMockMvc.perform(get("/api/services/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultServiceShouldNotBeFound(String filter) throws Exception {
        restServiceMockMvc.perform(get("/api/services?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restServiceMockMvc.perform(get("/api/services/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingService() throws Exception {
        // Get the service
        restServiceMockMvc.perform(get("/api/services/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateService() throws Exception {
        // Initialize the database
        serviceRepository.saveAndFlush(service);

        int databaseSizeBeforeUpdate = serviceRepository.findAll().size();

        // Update the service
        Service updatedService = serviceRepository.findById(service.getId()).get();
        // Disconnect from session so that the updates on updatedService are not directly saved in db
        em.detach(updatedService);
        updatedService
            .code(UPDATED_CODE)
            .libelle(UPDATED_LIBELLE)
            .descrition(UPDATED_DESCRITION);
        ServiceDTO serviceDTO = serviceMapper.toDto(updatedService);

        restServiceMockMvc.perform(put("/api/services").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(serviceDTO)))
            .andExpect(status().isOk());

        // Validate the Service in the database
        List<Service> serviceList = serviceRepository.findAll();
        assertThat(serviceList).hasSize(databaseSizeBeforeUpdate);
        Service testService = serviceList.get(serviceList.size() - 1);
        assertThat(testService.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testService.getLibelle()).isEqualTo(UPDATED_LIBELLE);
        assertThat(testService.getDescrition()).isEqualTo(UPDATED_DESCRITION);
    }

    @Test
    @Transactional
    public void updateNonExistingService() throws Exception {
        int databaseSizeBeforeUpdate = serviceRepository.findAll().size();

        // Create the Service
        ServiceDTO serviceDTO = serviceMapper.toDto(service);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServiceMockMvc.perform(put("/api/services").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(serviceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Service in the database
        List<Service> serviceList = serviceRepository.findAll();
        assertThat(serviceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteService() throws Exception {
        // Initialize the database
        serviceRepository.saveAndFlush(service);

        int databaseSizeBeforeDelete = serviceRepository.findAll().size();

        // Delete the service
        restServiceMockMvc.perform(delete("/api/services/{id}", service.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Service> serviceList = serviceRepository.findAll();
        assertThat(serviceList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
