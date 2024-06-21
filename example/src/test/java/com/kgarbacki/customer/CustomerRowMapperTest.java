package com.kgarbacki.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerRowMapperTest {

    @Test
    void mapRow() throws SQLException {
        CustomerRowMapper customerRowMapper = new CustomerRowMapper();

        ResultSet rs = mock(ResultSet.class);

        when(rs.getLong("id")).thenReturn(1L);
        when(rs.getString("name")).thenReturn("Kamil");
        when(rs.getString("email")).thenReturn("kamil@gmail.com");
        when(rs.getInt("age")).thenReturn(21);

        Customer actual =  customerRowMapper.mapRow(rs, 0);

        Customer expected = new Customer(
                1L, "Kamil", "kamil@gmail.com", 21
        );

        assertThat(actual).isEqualTo(expected);
    }
}