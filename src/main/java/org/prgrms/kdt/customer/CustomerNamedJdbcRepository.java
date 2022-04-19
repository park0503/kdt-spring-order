package org.prgrms.kdt.customer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.*;

@Repository
public class CustomerNamedJdbcRepository implements CustomerRepository {
    private static final Logger logger = LoggerFactory.getLogger(CustomerJdbcRepository.class);

    private final NamedParameterJdbcTemplate jdbcTemplate;

//    private final PlatformTransactionManager transactionManager;

    private static final RowMapper<Customer> customerRowMapper = (resultSet, rowNum) -> {
        var customerId = toUUID(resultSet.getBytes("customer_id"));
        var customerName = resultSet.getString("name");
        var email = resultSet.getString("email");
        var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
        var lastLoginAt = resultSet.getTimestamp("last_login_at") != null ? resultSet.getTimestamp("last_login_at").toLocalDateTime() : null;
        return new Customer(customerId, customerName, email, lastLoginAt, createdAt);
    };

    private Map<String, Object> toParamMap(Customer customer) {
        return new HashMap<String, Object>() {{
            put("customerId", customer.getCustomerId().toString().getBytes());
            put("name", customer.getName());
            put("email", customer.getEmail());
            put("createdAt", Timestamp.valueOf(customer.getCreatedAt()));
            put("lastLoginAt", customer.getLastLoginAt() != null ? Timestamp.valueOf(customer.getLastLoginAt()) : null);
        }};
    }

    public CustomerNamedJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate1) {
        this.jdbcTemplate = jdbcTemplate1;
    }

    @Override
    public Customer insert(Customer customer) {
        var paramMap = toParamMap(customer);
        var update = jdbcTemplate.update("insert into customers(customer_id, name, email, created_at) values (UNHEX(REPLACE(:customerId, '-', '')), :name, :email, :createdAt);", paramMap);
        if (update != 1) {
            throw new RuntimeException("Noting was inserted");
        }
        return customer;
    }

    @Override
    public Customer update(Customer customer) {
        var paramMap = toParamMap(customer);
        int update = jdbcTemplate.update("update customers set name = :name, last_login_at = :lastLoginAt where customer_id = UNHEX(REPLACE(:customerId, '-', ''));",paramMap);
        if (update < 1) {
            throw new RuntimeException("Noting was inserted");
        }
        return customer;
    }

    @Override
    public int count() {
        return jdbcTemplate.queryForObject("select count(*) from customers;", Collections.emptyMap(),Integer.class);
    }

    @Override
    public List<Customer> findAll() {
        return jdbcTemplate.query("select * from customers;", customerRowMapper);
    }

    @Override
    public Optional<Customer> findByName(String name) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("select * from customers where name=:name;", Collections.singletonMap("name", name), customerRowMapper));
        } catch (EmptyResultDataAccessException e) {
            logger.error("Got empty result", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Customer> findById(UUID customerId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("select * from customers where customer_id=UNHEX(REPLACE(:customerId, '-', ''));",Collections.singletonMap("customerId", customerId.toString().getBytes()), customerRowMapper));
        } catch (EmptyResultDataAccessException e) {
            logger.error("Got empty result", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject("select * from customers where email=:email;", Collections.singletonMap("email", email), customerRowMapper));
        } catch (EmptyResultDataAccessException e) {
            logger.error("Got empty result", e);
            return Optional.empty();
        }
    }

//    public void testTransaction(Customer customer) {
//        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
//            @Override
//            protected void doInTransactionWithoutResult(TransactionStatus status) {
//                jdbcTemplate.update("update customers set name = :name where customer_id = UNHEX(REPLACE(:customerId, '-', ''));", toParamMap(customer));
//                jdbcTemplate.update("update customers set email = :email where customer_id = UNHEX(REPLACE(:customerId, '-', ''));", toParamMap(customer));
//            }
//        });
//    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("delete from customers;", Collections.emptyMap());
    }

    private static UUID toUUID(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }
}
