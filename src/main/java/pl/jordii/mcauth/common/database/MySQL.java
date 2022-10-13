package pl.jordii.mcauth.common.database;

import com.google.common.collect.Sets;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.*;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Singleton
public class MySQL implements DatabaseConnection {

    private final MySQLCredentials credentials;
    private final ExecutorService executorService;
    private final Set<PreparedStatement> preparedStatements = Sets.newHashSet();
    private Connection connection;

    @Inject
    public MySQL(MySQLCredentials credentials, ExecutorService executorService) {
        this.credentials = credentials;
        this.executorService = executorService;
    }

    @Override
    public boolean connect() {
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            this.connection = DriverManager.getConnection(
                    String.format("jdbc:mysql://%1$s:%2$s/%3$s?autoReconnect=true&useSSL=false",
                            this.credentials.getHost(),
                            this.credentials.getPort(),
                            this.credentials.getDatabase()),
                    this.credentials.getUsername(),
                    this.credentials.getPassword()
            );
            return true;

        } catch (SQLException | ClassNotFoundException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    @Override
    public void disconnect() {
        if (!this.isConnected()) {
            return;
        }

        try {
            for (PreparedStatement statement : preparedStatements) {
                statement.close();
            }
            this.connection.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public boolean isConnected() {
        try {
            if (this.connection == null || !this.connection.isValid(10) || this.connection.isClosed()) {
                return false;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return true;
    }

    public int syncQueryUpdate(String queryStatement) throws SQLException {
        this.checkConnection();
        Statement statement = this.connection.createStatement();
        statement.executeUpdate(queryStatement, Statement.RETURN_GENERATED_KEYS);
        int autoIncrementKey = -1;
        ResultSet autoIncrementResult = statement.getGeneratedKeys();
        if (autoIncrementResult.next()) {
            autoIncrementKey = autoIncrementResult.getInt(1);
            autoIncrementResult.close();
        }
        statement.close();
        return autoIncrementKey;
    }

    public int syncQueryUpdate(PreparedStatement preparedStatement) throws SQLException {
        this.checkConnection();
        preparedStatement.executeUpdate();
        int autoIncrementKey = -1;
        ResultSet autoIncrementResult = preparedStatement.getGeneratedKeys();
        if (autoIncrementResult.next()) {
            autoIncrementKey = autoIncrementResult.getInt(1);
            autoIncrementResult.close();
        }
        return autoIncrementKey;
    }

    public void asyncQueryUpdate(String queryStatement, Callback<Integer> autoIncrementId) {
        this.executorService.execute(() -> {
            try {
                int id = syncQueryUpdate(queryStatement);
                if (autoIncrementId != null) {
                    autoIncrementId.accept(id);
                }
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        });
    }

    public void asyncQueryUpdate(PreparedStatement preparedStatement, Callback<Integer> autoIncrementId) {
        this.executorService.execute(() -> {
            try {
                int id = syncQueryUpdate(preparedStatement);
                if (autoIncrementId != null) {
                    autoIncrementId.accept(id);
                }
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        });
    }

    public void asyncQueryUpdate(String queryStatement) {
        this.asyncQueryUpdate(queryStatement, null);
    }

    public void asyncQueryUpdate(PreparedStatement preparedStatement) {
        this.asyncQueryUpdate(preparedStatement, null);
    }

    public ResultSet syncQuery(String queryStatement) throws SQLException {
        this.checkConnection();
        try (Statement statement = this.connection.createStatement()) {
            return statement.executeQuery(queryStatement);
        }
    }

    public ResultSet syncQuery(PreparedStatement preparedStatement) throws SQLException {
        this.checkConnection();
        return preparedStatement.executeQuery();
    }

    public void asyncQuery(String queryStatement, Callback<ResultSet> result) {
        try {
            PreparedStatement preparedStatement = this.prepareStatement(queryStatement);
            this.asyncQuery(preparedStatement, result);
        } catch (SQLException sqlException) {
            result.onFailure(sqlException);
        }
    }

    public void asyncQuery(PreparedStatement preparedStatement, Callback<ResultSet> result) {
        Future<ResultSet> future = this.executorService.submit(() -> {
            try {
                return syncQuery(preparedStatement);
            } catch (SQLException exception) {
                result.onFailure(exception);
                return null;
            }
        });

        try (ResultSet resultSet = future.get()) {
            if (resultSet != null) {
                result.accept(resultSet);
            }
        } catch (InterruptedException | ExecutionException | SQLException e) {
            e.printStackTrace();
        }
    }

    public PreparedStatement prepareStatement(String template) throws SQLException {
        this.checkConnection();
        PreparedStatement preparedStatement = this.connection.prepareStatement(template, Statement.RETURN_GENERATED_KEYS);
        preparedStatements.add(preparedStatement);
        return preparedStatement;
    }

    private void checkConnection() throws SQLException {
        if (!this.isConnected()) {
            throw new SQLException("Cannot perform database action: no database connected!");
        }
    }

    private void checkConnection(String errorMessage) throws SQLException {
        if (!this.isConnected()) {
            throw new SQLException(errorMessage);
        }
    }

}
