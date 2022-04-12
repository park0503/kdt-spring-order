package org.prgrms.kdt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JdbcCustomerRepository {

    private static final Logger logger = LoggerFactory.getLogger(JdbcCustomerRepository.class);
    private final String SELECT_ALL_SQL = "select * from customers;";
    private final String INSERT_SQL = "insert into customers(customer_id, name, email) values (UUID_TO_BIN(?), ?, ?);";
    private final String DELETE_ALL_SQL = "delete from customers";

    public List<UUID> findAllIds() {
        List<UUID> uuids = new ArrayList<>();

        try(var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root", "1q2w3e4r!");
            var statement = connection.prepareStatement(SELECT_ALL_SQL);
            var resultSet = statement.executeQuery();
        ){
            while (resultSet.next()) {
                var customerName = resultSet.getString("name");
                var customerId = toUUID(resultSet.getBytes("customer_id"));
                var created_at = resultSet.getTimestamp("created_at").toLocalDateTime();
                logger.info("customer id -> {}, name -> {}, created_at -> {}", customerId, customerName, created_at);
                uuids.add(customerId);
            }
        } catch (SQLException e) {
            logger.error("Got error while closing connection", e);
        }
        return uuids;
    }

    public int insertCustomer(UUID customerId, String name, String email) {
        try(
                var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root", "1q2w3e4r!");
                var statement = connection.prepareStatement(INSERT_SQL);
        )
        {
            statement.setBytes(1, customerId.toString().getBytes());
            statement.setString(2, name);
            statement.setString(3, email);
            return statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Got error while closing connection", e);
        }
        return 0;
    }

    public int deleteAllCustomers() {
        try(
                var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root", "1q2w3e4r!");
                var statement = connection.prepareStatement(DELETE_ALL_SQL);
        )
        {
            return statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Got error while closing connection", e);
        }
        return 0;
    }

    static UUID toUUID(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }

    public static void main(String[] args) throws SQLException {
        var customerRepository = new JdbcCustomerRepository();

        var count = customerRepository.deleteAllCustomers();
        logger.info("deleted count -> {}", count);

        var customerId = UUID.randomUUID();
        logger.info("created customerId -> {}", customerId);
        logger.info("created UUID version -> {}", customerId.version());

        customerRepository.insertCustomer(customerId,"new-user", "new-user@gmail.com");
        customerRepository.findAllIds().forEach(v -> logger.info("Found customerId: {} and version: {}", v, v.version()));

    }
}
