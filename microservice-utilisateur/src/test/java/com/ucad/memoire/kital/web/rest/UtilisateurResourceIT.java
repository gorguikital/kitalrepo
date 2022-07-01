package com.ucad.memoire.kital.web.rest;

import com.ucad.memoire.kital.UtilisateurdbApp;
import com.ucad.memoire.kital.config.TestSecurityConfiguration;
import com.ucad.memoire.kital.domain.Utilisateur;
import com.ucad.memoire.kital.domain.Service;
import com.ucad.memoire.kital.domain.Profile;
import com.ucad.memoire.kital.repository.UtilisateurRepository;
import com.ucad.memoire.kital.service.UtilisateurService;
import com.ucad.memoire.kital.service.dto.UtilisateurDTO;
import com.ucad.memoire.kital.service.mapper.UtilisateurMapper;
import com.ucad.memoire.kital.service.dto.UtilisateurCriteria;
import com.ucad.memoire.kital.service.UtilisateurQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ucad.memoire.kital.domain.enumeration.Sexe;
/**
 * Integration tests for the {@link UtilisateurResource} REST controller.
 */
@SpringBootTest(classes = { UtilisateurdbApp.class, TestSecurityConfiguration.class })
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class UtilisateurResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_PRENOM = "AAAAAAAAAA";
    private static final String UPDATED_PRENOM = "BBBBBBBBBB";

    private static final Sexe DEFAULT_SEXE = Sexe.MASCULIN;
    private static final Sexe UPDATED_SEXE = Sexe.FEMININ;

    private static final String DEFAULT_TELEPHONE = "AAAAAAAAA";
    private static final String UPDATED_TELEPHONE = "BBBBBBBBB";

    private static final String DEFAULT_ADRESSE = "AAAAAAAAAA";
    private static final String UPDATED_ADRESSE = "BBBBBBBBBB";

    private static final String DEFAULT_CIN = "AAAAAAAAAA";
    private static final String UPDATED_CIN = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private UtilisateurRepository utilisateurRepositoryMock;

    @Autowired
    private UtilisateurMapper utilisateurMapper;

    @Mock
    private UtilisateurService utilisateurServiceMock;

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private UtilisateurQueryService utilisateurQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUtilisateurMockMvc;

    private Utilisateur utilisateur;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Utilisateur createEntity(EntityManager em) {
        Utilisateur utilisateur = new Utilisateur()
            .nom(DEFAULT_NOM)
            .prenom(DEFAULT_PRENOM)
            .sexe(DEFAULT_SEXE)
            .telephone(DEFAULT_TELEPHONE)
            .adresse(DEFAULT_ADRESSE)
            .cin(DEFAULT_CIN)
            .email(DEFAULT_EMAIL)
            .password(DEFAULT_PASSWORD);
        // Add required entity
        Profile profile;
        if (TestUtil.findAll(em, Profile.class).isEmpty()) {
            profile = ProfileResourceIT.createEntity(em);
            em.persist(profile);
            em.flush();
        } else {
            profile = TestUtil.findAll(em, Profile.class).get(0);
        }
        utilisateur.getProfiles().add(profile);
        return utilisateur;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Utilisateur createUpdatedEntity(EntityManager em) {
        Utilisateur utilisateur = new Utilisateur()
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .sexe(UPDATED_SEXE)
            .telephone(UPDATED_TELEPHONE)
            .adresse(UPDATED_ADRESSE)
            .cin(UPDATED_CIN)
            .email(UPDATED_EMAIL)
            .password(UPDATED_PASSWORD);
        // Add required entity
        Profile profile;
        if (TestUtil.findAll(em, Profile.class).isEmpty()) {
            profile = ProfileResourceIT.createUpdatedEntity(em);
            em.persist(profile);
            em.flush();
        } else {
            profile = TestUtil.findAll(em, Profile.class).get(0);
        }
        utilisateur.getProfiles().add(profile);
        return utilisateur;
    }

    @BeforeEach
    public void initTest() {
        utilisateur = createEntity(em);
    }

    @Test
    @Transactional
    public void createUtilisateur() throws Exception {
        int databaseSizeBeforeCreate = utilisateurRepository.findAll().size();
        // Create the Utilisateur
        UtilisateurDTO utilisateurDTO = utilisateurMapper.toDto(utilisateur);
        restUtilisateurMockMvc.perform(post("/api/utilisateurs").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(utilisateurDTO)))
            .andExpect(status().isCreated());

        // Validate the Utilisateur in the database
        List<Utilisateur> utilisateurList = utilisateurRepository.findAll();
        assertThat(utilisateurList).hasSize(databaseSizeBeforeCreate + 1);
        Utilisateur testUtilisateur = utilisateurList.get(utilisateurList.size() - 1);
        assertThat(testUtilisateur.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testUtilisateur.getPrenom()).isEqualTo(DEFAULT_PRENOM);
        assertThat(testUtilisateur.getSexe()).isEqualTo(DEFAULT_SEXE);
        assertThat(testUtilisateur.getTelephone()).isEqualTo(DEFAULT_TELEPHONE);
        assertThat(testUtilisateur.getAdresse()).isEqualTo(DEFAULT_ADRESSE);
        assertThat(testUtilisateur.getCin()).isEqualTo(DEFAULT_CIN);
        assertThat(testUtilisateur.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testUtilisateur.getPassword()).isEqualTo(DEFAULT_PASSWORD);
    }

    @Test
    @Transactional
    public void createUtilisateurWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = utilisateurRepository.findAll().size();

        // Create the Utilisateur with an existing ID
        utilisateur.setId(1L);
        UtilisateurDTO utilisateurDTO = utilisateurMapper.toDto(utilisateur);

        // An entity with an existing ID cannot be created, so this API call must fail
        restUtilisateurMockMvc.perform(post("/api/utilisateurs").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(utilisateurDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Utilisateur in the database
        List<Utilisateur> utilisateurList = utilisateurRepository.findAll();
        assertThat(utilisateurList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = utilisateurRepository.findAll().size();
        // set the field null
        utilisateur.setNom(null);

        // Create the Utilisateur, which fails.
        UtilisateurDTO utilisateurDTO = utilisateurMapper.toDto(utilisateur);


        restUtilisateurMockMvc.perform(post("/api/utilisateurs").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(utilisateurDTO)))
            .andExpect(status().isBadRequest());

        List<Utilisateur> utilisateurList = utilisateurRepository.findAll();
        assertThat(utilisateurList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPrenomIsRequired() throws Exception {
        int databaseSizeBeforeTest = utilisateurRepository.findAll().size();
        // set the field null
        utilisateur.setPrenom(null);

        // Create the Utilisateur, which fails.
        UtilisateurDTO utilisateurDTO = utilisateurMapper.toDto(utilisateur);


        restUtilisateurMockMvc.perform(post("/api/utilisateurs").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(utilisateurDTO)))
            .andExpect(status().isBadRequest());

        List<Utilisateur> utilisateurList = utilisateurRepository.findAll();
        assertThat(utilisateurList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSexeIsRequired() throws Exception {
        int databaseSizeBeforeTest = utilisateurRepository.findAll().size();
        // set the field null
        utilisateur.setSexe(null);

        // Create the Utilisateur, which fails.
        UtilisateurDTO utilisateurDTO = utilisateurMapper.toDto(utilisateur);


        restUtilisateurMockMvc.perform(post("/api/utilisateurs").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(utilisateurDTO)))
            .andExpect(status().isBadRequest());

        List<Utilisateur> utilisateurList = utilisateurRepository.findAll();
        assertThat(utilisateurList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTelephoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = utilisateurRepository.findAll().size();
        // set the field null
        utilisateur.setTelephone(null);

        // Create the Utilisateur, which fails.
        UtilisateurDTO utilisateurDTO = utilisateurMapper.toDto(utilisateur);


        restUtilisateurMockMvc.perform(post("/api/utilisateurs").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(utilisateurDTO)))
            .andExpect(status().isBadRequest());

        List<Utilisateur> utilisateurList = utilisateurRepository.findAll();
        assertThat(utilisateurList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAdresseIsRequired() throws Exception {
        int databaseSizeBeforeTest = utilisateurRepository.findAll().size();
        // set the field null
        utilisateur.setAdresse(null);

        // Create the Utilisateur, which fails.
        UtilisateurDTO utilisateurDTO = utilisateurMapper.toDto(utilisateur);


        restUtilisateurMockMvc.perform(post("/api/utilisateurs").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(utilisateurDTO)))
            .andExpect(status().isBadRequest());

        List<Utilisateur> utilisateurList = utilisateurRepository.findAll();
        assertThat(utilisateurList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = utilisateurRepository.findAll().size();
        // set the field null
        utilisateur.setEmail(null);

        // Create the Utilisateur, which fails.
        UtilisateurDTO utilisateurDTO = utilisateurMapper.toDto(utilisateur);


        restUtilisateurMockMvc.perform(post("/api/utilisateurs").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(utilisateurDTO)))
            .andExpect(status().isBadRequest());

        List<Utilisateur> utilisateurList = utilisateurRepository.findAll();
        assertThat(utilisateurList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPasswordIsRequired() throws Exception {
        int databaseSizeBeforeTest = utilisateurRepository.findAll().size();
        // set the field null
        utilisateur.setPassword(null);

        // Create the Utilisateur, which fails.
        UtilisateurDTO utilisateurDTO = utilisateurMapper.toDto(utilisateur);


        restUtilisateurMockMvc.perform(post("/api/utilisateurs").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(utilisateurDTO)))
            .andExpect(status().isBadRequest());

        List<Utilisateur> utilisateurList = utilisateurRepository.findAll();
        assertThat(utilisateurList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllUtilisateurs() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList
        restUtilisateurMockMvc.perform(get("/api/utilisateurs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(utilisateur.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM)))
            .andExpect(jsonPath("$.[*].sexe").value(hasItem(DEFAULT_SEXE.toString())))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)))
            .andExpect(jsonPath("$.[*].adresse").value(hasItem(DEFAULT_ADRESSE)))
            .andExpect(jsonPath("$.[*].cin").value(hasItem(DEFAULT_CIN)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)));
    }
    
    @SuppressWarnings({"unchecked"})
    public void getAllUtilisateursWithEagerRelationshipsIsEnabled() throws Exception {
        when(utilisateurServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUtilisateurMockMvc.perform(get("/api/utilisateurs?eagerload=true"))
            .andExpect(status().isOk());

        verify(utilisateurServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllUtilisateursWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(utilisateurServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restUtilisateurMockMvc.perform(get("/api/utilisateurs?eagerload=true"))
            .andExpect(status().isOk());

        verify(utilisateurServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getUtilisateur() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get the utilisateur
        restUtilisateurMockMvc.perform(get("/api/utilisateurs/{id}", utilisateur.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(utilisateur.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.prenom").value(DEFAULT_PRENOM))
            .andExpect(jsonPath("$.sexe").value(DEFAULT_SEXE.toString()))
            .andExpect(jsonPath("$.telephone").value(DEFAULT_TELEPHONE))
            .andExpect(jsonPath("$.adresse").value(DEFAULT_ADRESSE))
            .andExpect(jsonPath("$.cin").value(DEFAULT_CIN))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD));
    }


    @Test
    @Transactional
    public void getUtilisateursByIdFiltering() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        Long id = utilisateur.getId();

        defaultUtilisateurShouldBeFound("id.equals=" + id);
        defaultUtilisateurShouldNotBeFound("id.notEquals=" + id);

        defaultUtilisateurShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultUtilisateurShouldNotBeFound("id.greaterThan=" + id);

        defaultUtilisateurShouldBeFound("id.lessThanOrEqual=" + id);
        defaultUtilisateurShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllUtilisateursByNomIsEqualToSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where nom equals to DEFAULT_NOM
        defaultUtilisateurShouldBeFound("nom.equals=" + DEFAULT_NOM);

        // Get all the utilisateurList where nom equals to UPDATED_NOM
        defaultUtilisateurShouldNotBeFound("nom.equals=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    public void getAllUtilisateursByNomIsNotEqualToSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where nom not equals to DEFAULT_NOM
        defaultUtilisateurShouldNotBeFound("nom.notEquals=" + DEFAULT_NOM);

        // Get all the utilisateurList where nom not equals to UPDATED_NOM
        defaultUtilisateurShouldBeFound("nom.notEquals=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    public void getAllUtilisateursByNomIsInShouldWork() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where nom in DEFAULT_NOM or UPDATED_NOM
        defaultUtilisateurShouldBeFound("nom.in=" + DEFAULT_NOM + "," + UPDATED_NOM);

        // Get all the utilisateurList where nom equals to UPDATED_NOM
        defaultUtilisateurShouldNotBeFound("nom.in=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    public void getAllUtilisateursByNomIsNullOrNotNull() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where nom is not null
        defaultUtilisateurShouldBeFound("nom.specified=true");

        // Get all the utilisateurList where nom is null
        defaultUtilisateurShouldNotBeFound("nom.specified=false");
    }
                @Test
    @Transactional
    public void getAllUtilisateursByNomContainsSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where nom contains DEFAULT_NOM
        defaultUtilisateurShouldBeFound("nom.contains=" + DEFAULT_NOM);

        // Get all the utilisateurList where nom contains UPDATED_NOM
        defaultUtilisateurShouldNotBeFound("nom.contains=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    public void getAllUtilisateursByNomNotContainsSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where nom does not contain DEFAULT_NOM
        defaultUtilisateurShouldNotBeFound("nom.doesNotContain=" + DEFAULT_NOM);

        // Get all the utilisateurList where nom does not contain UPDATED_NOM
        defaultUtilisateurShouldBeFound("nom.doesNotContain=" + UPDATED_NOM);
    }


    @Test
    @Transactional
    public void getAllUtilisateursByPrenomIsEqualToSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where prenom equals to DEFAULT_PRENOM
        defaultUtilisateurShouldBeFound("prenom.equals=" + DEFAULT_PRENOM);

        // Get all the utilisateurList where prenom equals to UPDATED_PRENOM
        defaultUtilisateurShouldNotBeFound("prenom.equals=" + UPDATED_PRENOM);
    }

    @Test
    @Transactional
    public void getAllUtilisateursByPrenomIsNotEqualToSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where prenom not equals to DEFAULT_PRENOM
        defaultUtilisateurShouldNotBeFound("prenom.notEquals=" + DEFAULT_PRENOM);

        // Get all the utilisateurList where prenom not equals to UPDATED_PRENOM
        defaultUtilisateurShouldBeFound("prenom.notEquals=" + UPDATED_PRENOM);
    }

    @Test
    @Transactional
    public void getAllUtilisateursByPrenomIsInShouldWork() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where prenom in DEFAULT_PRENOM or UPDATED_PRENOM
        defaultUtilisateurShouldBeFound("prenom.in=" + DEFAULT_PRENOM + "," + UPDATED_PRENOM);

        // Get all the utilisateurList where prenom equals to UPDATED_PRENOM
        defaultUtilisateurShouldNotBeFound("prenom.in=" + UPDATED_PRENOM);
    }

    @Test
    @Transactional
    public void getAllUtilisateursByPrenomIsNullOrNotNull() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where prenom is not null
        defaultUtilisateurShouldBeFound("prenom.specified=true");

        // Get all the utilisateurList where prenom is null
        defaultUtilisateurShouldNotBeFound("prenom.specified=false");
    }
                @Test
    @Transactional
    public void getAllUtilisateursByPrenomContainsSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where prenom contains DEFAULT_PRENOM
        defaultUtilisateurShouldBeFound("prenom.contains=" + DEFAULT_PRENOM);

        // Get all the utilisateurList where prenom contains UPDATED_PRENOM
        defaultUtilisateurShouldNotBeFound("prenom.contains=" + UPDATED_PRENOM);
    }

    @Test
    @Transactional
    public void getAllUtilisateursByPrenomNotContainsSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where prenom does not contain DEFAULT_PRENOM
        defaultUtilisateurShouldNotBeFound("prenom.doesNotContain=" + DEFAULT_PRENOM);

        // Get all the utilisateurList where prenom does not contain UPDATED_PRENOM
        defaultUtilisateurShouldBeFound("prenom.doesNotContain=" + UPDATED_PRENOM);
    }


    @Test
    @Transactional
    public void getAllUtilisateursBySexeIsEqualToSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where sexe equals to DEFAULT_SEXE
        defaultUtilisateurShouldBeFound("sexe.equals=" + DEFAULT_SEXE);

        // Get all the utilisateurList where sexe equals to UPDATED_SEXE
        defaultUtilisateurShouldNotBeFound("sexe.equals=" + UPDATED_SEXE);
    }

    @Test
    @Transactional
    public void getAllUtilisateursBySexeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where sexe not equals to DEFAULT_SEXE
        defaultUtilisateurShouldNotBeFound("sexe.notEquals=" + DEFAULT_SEXE);

        // Get all the utilisateurList where sexe not equals to UPDATED_SEXE
        defaultUtilisateurShouldBeFound("sexe.notEquals=" + UPDATED_SEXE);
    }

    @Test
    @Transactional
    public void getAllUtilisateursBySexeIsInShouldWork() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where sexe in DEFAULT_SEXE or UPDATED_SEXE
        defaultUtilisateurShouldBeFound("sexe.in=" + DEFAULT_SEXE + "," + UPDATED_SEXE);

        // Get all the utilisateurList where sexe equals to UPDATED_SEXE
        defaultUtilisateurShouldNotBeFound("sexe.in=" + UPDATED_SEXE);
    }

    @Test
    @Transactional
    public void getAllUtilisateursBySexeIsNullOrNotNull() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where sexe is not null
        defaultUtilisateurShouldBeFound("sexe.specified=true");

        // Get all the utilisateurList where sexe is null
        defaultUtilisateurShouldNotBeFound("sexe.specified=false");
    }

    @Test
    @Transactional
    public void getAllUtilisateursByTelephoneIsEqualToSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where telephone equals to DEFAULT_TELEPHONE
        defaultUtilisateurShouldBeFound("telephone.equals=" + DEFAULT_TELEPHONE);

        // Get all the utilisateurList where telephone equals to UPDATED_TELEPHONE
        defaultUtilisateurShouldNotBeFound("telephone.equals=" + UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    public void getAllUtilisateursByTelephoneIsNotEqualToSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where telephone not equals to DEFAULT_TELEPHONE
        defaultUtilisateurShouldNotBeFound("telephone.notEquals=" + DEFAULT_TELEPHONE);

        // Get all the utilisateurList where telephone not equals to UPDATED_TELEPHONE
        defaultUtilisateurShouldBeFound("telephone.notEquals=" + UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    public void getAllUtilisateursByTelephoneIsInShouldWork() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where telephone in DEFAULT_TELEPHONE or UPDATED_TELEPHONE
        defaultUtilisateurShouldBeFound("telephone.in=" + DEFAULT_TELEPHONE + "," + UPDATED_TELEPHONE);

        // Get all the utilisateurList where telephone equals to UPDATED_TELEPHONE
        defaultUtilisateurShouldNotBeFound("telephone.in=" + UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    public void getAllUtilisateursByTelephoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where telephone is not null
        defaultUtilisateurShouldBeFound("telephone.specified=true");

        // Get all the utilisateurList where telephone is null
        defaultUtilisateurShouldNotBeFound("telephone.specified=false");
    }
                @Test
    @Transactional
    public void getAllUtilisateursByTelephoneContainsSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where telephone contains DEFAULT_TELEPHONE
        defaultUtilisateurShouldBeFound("telephone.contains=" + DEFAULT_TELEPHONE);

        // Get all the utilisateurList where telephone contains UPDATED_TELEPHONE
        defaultUtilisateurShouldNotBeFound("telephone.contains=" + UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    public void getAllUtilisateursByTelephoneNotContainsSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where telephone does not contain DEFAULT_TELEPHONE
        defaultUtilisateurShouldNotBeFound("telephone.doesNotContain=" + DEFAULT_TELEPHONE);

        // Get all the utilisateurList where telephone does not contain UPDATED_TELEPHONE
        defaultUtilisateurShouldBeFound("telephone.doesNotContain=" + UPDATED_TELEPHONE);
    }


    @Test
    @Transactional
    public void getAllUtilisateursByAdresseIsEqualToSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where adresse equals to DEFAULT_ADRESSE
        defaultUtilisateurShouldBeFound("adresse.equals=" + DEFAULT_ADRESSE);

        // Get all the utilisateurList where adresse equals to UPDATED_ADRESSE
        defaultUtilisateurShouldNotBeFound("adresse.equals=" + UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    public void getAllUtilisateursByAdresseIsNotEqualToSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where adresse not equals to DEFAULT_ADRESSE
        defaultUtilisateurShouldNotBeFound("adresse.notEquals=" + DEFAULT_ADRESSE);

        // Get all the utilisateurList where adresse not equals to UPDATED_ADRESSE
        defaultUtilisateurShouldBeFound("adresse.notEquals=" + UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    public void getAllUtilisateursByAdresseIsInShouldWork() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where adresse in DEFAULT_ADRESSE or UPDATED_ADRESSE
        defaultUtilisateurShouldBeFound("adresse.in=" + DEFAULT_ADRESSE + "," + UPDATED_ADRESSE);

        // Get all the utilisateurList where adresse equals to UPDATED_ADRESSE
        defaultUtilisateurShouldNotBeFound("adresse.in=" + UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    public void getAllUtilisateursByAdresseIsNullOrNotNull() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where adresse is not null
        defaultUtilisateurShouldBeFound("adresse.specified=true");

        // Get all the utilisateurList where adresse is null
        defaultUtilisateurShouldNotBeFound("adresse.specified=false");
    }
                @Test
    @Transactional
    public void getAllUtilisateursByAdresseContainsSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where adresse contains DEFAULT_ADRESSE
        defaultUtilisateurShouldBeFound("adresse.contains=" + DEFAULT_ADRESSE);

        // Get all the utilisateurList where adresse contains UPDATED_ADRESSE
        defaultUtilisateurShouldNotBeFound("adresse.contains=" + UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    public void getAllUtilisateursByAdresseNotContainsSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where adresse does not contain DEFAULT_ADRESSE
        defaultUtilisateurShouldNotBeFound("adresse.doesNotContain=" + DEFAULT_ADRESSE);

        // Get all the utilisateurList where adresse does not contain UPDATED_ADRESSE
        defaultUtilisateurShouldBeFound("adresse.doesNotContain=" + UPDATED_ADRESSE);
    }


    @Test
    @Transactional
    public void getAllUtilisateursByCinIsEqualToSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where cin equals to DEFAULT_CIN
        defaultUtilisateurShouldBeFound("cin.equals=" + DEFAULT_CIN);

        // Get all the utilisateurList where cin equals to UPDATED_CIN
        defaultUtilisateurShouldNotBeFound("cin.equals=" + UPDATED_CIN);
    }

    @Test
    @Transactional
    public void getAllUtilisateursByCinIsNotEqualToSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where cin not equals to DEFAULT_CIN
        defaultUtilisateurShouldNotBeFound("cin.notEquals=" + DEFAULT_CIN);

        // Get all the utilisateurList where cin not equals to UPDATED_CIN
        defaultUtilisateurShouldBeFound("cin.notEquals=" + UPDATED_CIN);
    }

    @Test
    @Transactional
    public void getAllUtilisateursByCinIsInShouldWork() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where cin in DEFAULT_CIN or UPDATED_CIN
        defaultUtilisateurShouldBeFound("cin.in=" + DEFAULT_CIN + "," + UPDATED_CIN);

        // Get all the utilisateurList where cin equals to UPDATED_CIN
        defaultUtilisateurShouldNotBeFound("cin.in=" + UPDATED_CIN);
    }

    @Test
    @Transactional
    public void getAllUtilisateursByCinIsNullOrNotNull() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where cin is not null
        defaultUtilisateurShouldBeFound("cin.specified=true");

        // Get all the utilisateurList where cin is null
        defaultUtilisateurShouldNotBeFound("cin.specified=false");
    }
                @Test
    @Transactional
    public void getAllUtilisateursByCinContainsSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where cin contains DEFAULT_CIN
        defaultUtilisateurShouldBeFound("cin.contains=" + DEFAULT_CIN);

        // Get all the utilisateurList where cin contains UPDATED_CIN
        defaultUtilisateurShouldNotBeFound("cin.contains=" + UPDATED_CIN);
    }

    @Test
    @Transactional
    public void getAllUtilisateursByCinNotContainsSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where cin does not contain DEFAULT_CIN
        defaultUtilisateurShouldNotBeFound("cin.doesNotContain=" + DEFAULT_CIN);

        // Get all the utilisateurList where cin does not contain UPDATED_CIN
        defaultUtilisateurShouldBeFound("cin.doesNotContain=" + UPDATED_CIN);
    }


    @Test
    @Transactional
    public void getAllUtilisateursByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where email equals to DEFAULT_EMAIL
        defaultUtilisateurShouldBeFound("email.equals=" + DEFAULT_EMAIL);

        // Get all the utilisateurList where email equals to UPDATED_EMAIL
        defaultUtilisateurShouldNotBeFound("email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    public void getAllUtilisateursByEmailIsNotEqualToSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where email not equals to DEFAULT_EMAIL
        defaultUtilisateurShouldNotBeFound("email.notEquals=" + DEFAULT_EMAIL);

        // Get all the utilisateurList where email not equals to UPDATED_EMAIL
        defaultUtilisateurShouldBeFound("email.notEquals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    public void getAllUtilisateursByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where email in DEFAULT_EMAIL or UPDATED_EMAIL
        defaultUtilisateurShouldBeFound("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL);

        // Get all the utilisateurList where email equals to UPDATED_EMAIL
        defaultUtilisateurShouldNotBeFound("email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    public void getAllUtilisateursByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where email is not null
        defaultUtilisateurShouldBeFound("email.specified=true");

        // Get all the utilisateurList where email is null
        defaultUtilisateurShouldNotBeFound("email.specified=false");
    }
                @Test
    @Transactional
    public void getAllUtilisateursByEmailContainsSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where email contains DEFAULT_EMAIL
        defaultUtilisateurShouldBeFound("email.contains=" + DEFAULT_EMAIL);

        // Get all the utilisateurList where email contains UPDATED_EMAIL
        defaultUtilisateurShouldNotBeFound("email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    public void getAllUtilisateursByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where email does not contain DEFAULT_EMAIL
        defaultUtilisateurShouldNotBeFound("email.doesNotContain=" + DEFAULT_EMAIL);

        // Get all the utilisateurList where email does not contain UPDATED_EMAIL
        defaultUtilisateurShouldBeFound("email.doesNotContain=" + UPDATED_EMAIL);
    }


    @Test
    @Transactional
    public void getAllUtilisateursByPasswordIsEqualToSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where password equals to DEFAULT_PASSWORD
        defaultUtilisateurShouldBeFound("password.equals=" + DEFAULT_PASSWORD);

        // Get all the utilisateurList where password equals to UPDATED_PASSWORD
        defaultUtilisateurShouldNotBeFound("password.equals=" + UPDATED_PASSWORD);
    }

    @Test
    @Transactional
    public void getAllUtilisateursByPasswordIsNotEqualToSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where password not equals to DEFAULT_PASSWORD
        defaultUtilisateurShouldNotBeFound("password.notEquals=" + DEFAULT_PASSWORD);

        // Get all the utilisateurList where password not equals to UPDATED_PASSWORD
        defaultUtilisateurShouldBeFound("password.notEquals=" + UPDATED_PASSWORD);
    }

    @Test
    @Transactional
    public void getAllUtilisateursByPasswordIsInShouldWork() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where password in DEFAULT_PASSWORD or UPDATED_PASSWORD
        defaultUtilisateurShouldBeFound("password.in=" + DEFAULT_PASSWORD + "," + UPDATED_PASSWORD);

        // Get all the utilisateurList where password equals to UPDATED_PASSWORD
        defaultUtilisateurShouldNotBeFound("password.in=" + UPDATED_PASSWORD);
    }

    @Test
    @Transactional
    public void getAllUtilisateursByPasswordIsNullOrNotNull() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where password is not null
        defaultUtilisateurShouldBeFound("password.specified=true");

        // Get all the utilisateurList where password is null
        defaultUtilisateurShouldNotBeFound("password.specified=false");
    }
                @Test
    @Transactional
    public void getAllUtilisateursByPasswordContainsSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where password contains DEFAULT_PASSWORD
        defaultUtilisateurShouldBeFound("password.contains=" + DEFAULT_PASSWORD);

        // Get all the utilisateurList where password contains UPDATED_PASSWORD
        defaultUtilisateurShouldNotBeFound("password.contains=" + UPDATED_PASSWORD);
    }

    @Test
    @Transactional
    public void getAllUtilisateursByPasswordNotContainsSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        // Get all the utilisateurList where password does not contain DEFAULT_PASSWORD
        defaultUtilisateurShouldNotBeFound("password.doesNotContain=" + DEFAULT_PASSWORD);

        // Get all the utilisateurList where password does not contain UPDATED_PASSWORD
        defaultUtilisateurShouldBeFound("password.doesNotContain=" + UPDATED_PASSWORD);
    }


    @Test
    @Transactional
    public void getAllUtilisateursByServiceIsEqualToSomething() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);
        Service service = ServiceResourceIT.createEntity(em);
        em.persist(service);
        em.flush();
        utilisateur.setService(service);
        utilisateurRepository.saveAndFlush(utilisateur);
        Long serviceId = service.getId();

        // Get all the utilisateurList where service equals to serviceId
        defaultUtilisateurShouldBeFound("serviceId.equals=" + serviceId);

        // Get all the utilisateurList where service equals to serviceId + 1
        defaultUtilisateurShouldNotBeFound("serviceId.equals=" + (serviceId + 1));
    }


    @Test
    @Transactional
    public void getAllUtilisateursByProfileIsEqualToSomething() throws Exception {
        // Get already existing entity
        Profile profile = utilisateur.getProfile();
        utilisateurRepository.saveAndFlush(utilisateur);
        Long profileId = profile.getId();

        // Get all the utilisateurList where profile equals to profileId
        defaultUtilisateurShouldBeFound("profileId.equals=" + profileId);

        // Get all the utilisateurList where profile equals to profileId + 1
        defaultUtilisateurShouldNotBeFound("profileId.equals=" + (profileId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultUtilisateurShouldBeFound(String filter) throws Exception {
        restUtilisateurMockMvc.perform(get("/api/utilisateurs?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(utilisateur.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM)))
            .andExpect(jsonPath("$.[*].sexe").value(hasItem(DEFAULT_SEXE.toString())))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)))
            .andExpect(jsonPath("$.[*].adresse").value(hasItem(DEFAULT_ADRESSE)))
            .andExpect(jsonPath("$.[*].cin").value(hasItem(DEFAULT_CIN)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)));

        // Check, that the count call also returns 1
        restUtilisateurMockMvc.perform(get("/api/utilisateurs/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultUtilisateurShouldNotBeFound(String filter) throws Exception {
        restUtilisateurMockMvc.perform(get("/api/utilisateurs?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUtilisateurMockMvc.perform(get("/api/utilisateurs/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingUtilisateur() throws Exception {
        // Get the utilisateur
        restUtilisateurMockMvc.perform(get("/api/utilisateurs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUtilisateur() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        int databaseSizeBeforeUpdate = utilisateurRepository.findAll().size();

        // Update the utilisateur
        Utilisateur updatedUtilisateur = utilisateurRepository.findById(utilisateur.getId()).get();
        // Disconnect from session so that the updates on updatedUtilisateur are not directly saved in db
        em.detach(updatedUtilisateur);
        updatedUtilisateur
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .sexe(UPDATED_SEXE)
            .telephone(UPDATED_TELEPHONE)
            .adresse(UPDATED_ADRESSE)
            .cin(UPDATED_CIN)
            .email(UPDATED_EMAIL)
            .password(UPDATED_PASSWORD);
        UtilisateurDTO utilisateurDTO = utilisateurMapper.toDto(updatedUtilisateur);

        restUtilisateurMockMvc.perform(put("/api/utilisateurs").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(utilisateurDTO)))
            .andExpect(status().isOk());

        // Validate the Utilisateur in the database
        List<Utilisateur> utilisateurList = utilisateurRepository.findAll();
        assertThat(utilisateurList).hasSize(databaseSizeBeforeUpdate);
        Utilisateur testUtilisateur = utilisateurList.get(utilisateurList.size() - 1);
        assertThat(testUtilisateur.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testUtilisateur.getPrenom()).isEqualTo(UPDATED_PRENOM);
        assertThat(testUtilisateur.getSexe()).isEqualTo(UPDATED_SEXE);
        assertThat(testUtilisateur.getTelephone()).isEqualTo(UPDATED_TELEPHONE);
        assertThat(testUtilisateur.getAdresse()).isEqualTo(UPDATED_ADRESSE);
        assertThat(testUtilisateur.getCin()).isEqualTo(UPDATED_CIN);
        assertThat(testUtilisateur.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testUtilisateur.getPassword()).isEqualTo(UPDATED_PASSWORD);
    }

    @Test
    @Transactional
    public void updateNonExistingUtilisateur() throws Exception {
        int databaseSizeBeforeUpdate = utilisateurRepository.findAll().size();

        // Create the Utilisateur
        UtilisateurDTO utilisateurDTO = utilisateurMapper.toDto(utilisateur);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUtilisateurMockMvc.perform(put("/api/utilisateurs").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(utilisateurDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Utilisateur in the database
        List<Utilisateur> utilisateurList = utilisateurRepository.findAll();
        assertThat(utilisateurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteUtilisateur() throws Exception {
        // Initialize the database
        utilisateurRepository.saveAndFlush(utilisateur);

        int databaseSizeBeforeDelete = utilisateurRepository.findAll().size();

        // Delete the utilisateur
        restUtilisateurMockMvc.perform(delete("/api/utilisateurs/{id}", utilisateur.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Utilisateur> utilisateurList = utilisateurRepository.findAll();
        assertThat(utilisateurList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
