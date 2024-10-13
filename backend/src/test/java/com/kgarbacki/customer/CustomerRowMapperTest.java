package com.kgarbacki.customer;

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
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
        when(rs.getString("gender")).thenReturn("MALE");
        when(rs.getString("password")).thenReturn("password");
        when(rs.getString("profile_image_id")).thenReturn("22222");

        Customer actual =  customerRowMapper.mapRow(rs, 1);

        Customer expected = new Customer(
                1L,
                "Kamil",
                "kamil@gmail.com",
                "password",
                21,
                Gender.MALE,
                "22222");

        assertThat(actual).isEqualTo(expected);
    }
}