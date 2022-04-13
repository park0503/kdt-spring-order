package org.prgrms.kdt.customer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CustomerJdbcRepository implements CustomerRepository{
    private static final Logger logger = LoggerFactory.getLogger(CustomerJdbcRepository.class);

    private final DataSource dataSource;

    public CustomerJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Customer insert(Customer customer) {
        try(
                var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("insert into customers(customer_id, name, email, created_at) values (UUID_TO_BIN(?), ?, ?, ?);");
                ) {
            statement.setBytes(1, customer.getCustomerId().toString().getBytes());
            statement.setString(2, customer.getName());
            statement.setString(3, customer.getEmail());
            statement.setTimestamp(4, Timestamp.valueOf(customer.getCreatedAt()));
            var executeUpdate = statement.executeUpdate();
            if (executeUpdate != 1) {
                throw new RuntimeException("Noting was inserted");
            }
            return customer;
        } catch (SQLException e) {
            logger.error("Got error while closing connection", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Customer update(Customer customer) {
        try(
                var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("update customers set name = ?, last_login_at = ? where customer_id = UUID_TO_BIN(?);");
        ) {
            statement.setString(1, customer.getName());
            statement.setTimestamp(2, customer.getLastLoginAt() != null ? Timestamp.valueOf(customer.getLastLoginAt()) : null);
            statement.setBytes(3, customer.getCustomerId().toString().getBytes());
            var executeUpdate = statement.executeUpdate();
            if (executeUpdate < 1) {
                throw new RuntimeException("Noting was updated");
            }
            return customer;
        } catch (SQLException e) {
            logger.error("Got error while closing connection", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();
        try (
                var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("select * from customers");
                var resultSet = statement.executeQuery();
                ) {
            mapToCustomer(customers, resultSet);
        } catch (SQLException e) {
            logger.error("Got error while closing connection", e);
            throw new RuntimeException(e);
        }
        return customers;
    }

    @Override
    public Optional<Customer> findByName(String name) {
        List<Customer> customers = new ArrayList<>();
        try (
                var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("select * from customers where name=?");
        ) {
            statement.setString(1, name);
            var resultSet = statement.executeQuery();
            mapToCustomer(customers, resultSet);
        } catch (SQLException e) {
            logger.error("Got error while closing connection", e);
            throw new RuntimeException(e);
        }
        return customers.stream().findFirst();
    }

    @Override
    public Optional<Customer> findById(UUID customerId) {
        List<Customer> customers = new ArrayList<>();
        try (
                var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("select * from customers where customer_id=UUID_TO_BIN(?)");
        ) {
            statement.setBytes(1, customerId.toString().getBytes());
            var resultSet = statement.executeQuery();
            mapToCustomer(customers, resultSet);
        } catch (SQLException e) {
            logger.error("Got error while closing connection", e);
            throw new RuntimeException(e);
        }
        return customers.stream().findFirst();
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        List<Customer> customers = new ArrayList<>();
        try (
                var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("select * from customers where email=?");
        ) {
            statement.setString(1, email);
            var resultSet = statement.executeQuery();
            mapToCustomer(customers, resultSet);
        } catch (SQLException e) {
            logger.error("Got error while closing connection", e);
            throw new RuntimeException(e);
        }
        return customers.stream().findFirst();
    }

    @Override
    public void deleteAll() {
        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement("delete from customers");)
        {
            var executeUpdate = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private UUID toUUID(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }

    private void mapToCustomer(List<Customer> customers, ResultSet resultSet) throws SQLException {
        while(resultSet.next()) {
            var customerId = toUUID(resultSet.getBytes("customer_id"));
            var customerName = resultSet.getString("name");
            var email = resultSet.getString("email");
            var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
            var lastLoginAt = resultSet.getTimestamp("last_login_at") != null ? resultSet.getTimestamp("last_login_at").toLocalDateTime() : null;
            customers.add(new Customer(customerId, customerName, email, lastLoginAt, createdAt));
        }
    }
}
