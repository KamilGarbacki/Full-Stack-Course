package com.kgarbacki.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCAccessService implements CustomerDao{

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {

        var sql = "SELECT id, name, email, age FROM customer";

        List<Customer> customers = jdbcTemplate.query(sql, customerRowMapper);

        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Long customerId) {
        var sql = "SELECT id, name, email, age FROM customer WHERE id = ?";

        return jdbcTemplate.query(sql, customerRowMapper, customerId).stream().findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        var sql = "Insert INTO customer(name, email, age) VALUES (?, ?, ?)";
        int result = jdbcTemplate.update(
                sql,
                customer.getName(),
                customer.getEmail(),
                customer.getAge()
        );
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        var sql = "SELECT count(id) FROM customer WHERE email = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, email) > 0;
    }

    @Override
    public boolean existsPersonWithId(Long customerId) {
        var sql = "SELECT count(id) FROM customer WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, customerId) > 0;
    }

    @Override
    public void deleteCustomerById(Long customerId) {
        var sql = "DELETE FROM customer WHERE id = ?";
        jdbcTemplate.update(sql, customerId);
    }

    @Override
    public void updateCustomer(Customer update) {
        if(update.getName() != null) {
            String sql = "UPDATE customer SET name = ? WHERE id = ?";
            jdbcTemplate.update(
                    sql,
                    update.getName(),
                    update.getId()
            );
        }
        if(update.getEmail() != null) {
            String sql = "UPDATE customer SET email = ? WHERE id = ?";
            jdbcTemplate.update(sql, update.getEmail(), update.getId());
        }
        if(update.getAge() != null) {
            String sql = "UPDATE customer SET age = ? WHERE id = ?";
            jdbcTemplate.update(sql, update.getAge(), update.getId());
        }
    }
}
