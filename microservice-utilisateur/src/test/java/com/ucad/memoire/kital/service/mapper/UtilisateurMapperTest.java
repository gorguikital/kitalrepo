package com.ucad.memoire.kital.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class UtilisateurMapperTest {

    private UtilisateurMapper utilisateurMapper;

    @BeforeEach
    public void setUp() {
        utilisateurMapper = new UtilisateurMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(utilisateurMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(utilisateurMapper.fromId(null)).isNull();
    }
}
