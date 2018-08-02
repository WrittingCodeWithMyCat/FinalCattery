package by.epam.cattery.dao;

import by.epam.cattery.dao.connection.ConnectionPoolException;
import by.epam.cattery.dao.exception.DAOException;
import by.epam.cattery.dao.connection.ConnectionProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Добавить CHECK STATUS из OFFER и CAT
public abstract class BaseDAO<T> implements GenericDAO<T> {
    private static final Logger logger = LogManager.getLogger(BaseDAO.class);

    protected final ConnectionProvider connectionProvider = ConnectionProvider.getInstance();


    @Override
    public void save(T obj) throws DAOException {
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = connectionProvider.obtainConnection();
            ps = connection.prepareStatement(getCreateQuery());

            executeCreateQuery(ps, obj);
            ps.executeUpdate();

        } catch (ConnectionPoolException | SQLException e) {
            logger.error("Saving object to the database failed", e);
            throw new DAOException(e);

        } finally {
            connectionProvider.close(connection);
            connectionProvider.closeStatement(ps);
        }
    }


    @Override
    public void update(T obj) throws DAOException {
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = connectionProvider.obtainConnection();
            ps = connection.prepareStatement(getUpdateQuery());

            executeUpdateQuery(ps, obj);

            int i = ps.executeUpdate();

            if (i != 1) {
                throw new DAOException("Couldn't update object from the database");
            }

        } catch (ConnectionPoolException | SQLException e) {
            logger.error("Updating object failed", e);
            throw new DAOException(e);

        } finally {
            connectionProvider.close(connection);
            connectionProvider.closeStatement(ps);
        }
    }


    @Override
    public void updateStatusById(String status, int id) throws DAOException {
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = connectionProvider.obtainConnection();
            ps = connection.prepareStatement(getUpdateStatusQuery());

            executeUpdateStatusQuery(ps, status, id);

            int i = ps.executeUpdate();

            if (i != 1) {
                throw new DAOException("Couldn't update status of object from the database");
            }

        } catch (ConnectionPoolException | SQLException e) {
            logger.error("Updating status failed", e);
            throw new DAOException(e);

        } finally {
            connectionProvider.close(connection);
            connectionProvider.closeStatement(ps);
        }
    }


    @Override
    public void delete(int id) throws DAOException {
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = connectionProvider.obtainConnection();
            ps = connection.prepareStatement(getDeleteQuery());

            executeDeleteQuery(ps, id);

            int i = ps.executeUpdate();

            if (i != 1) {
                throw new DAOException("Couldn't update while deleting object from the database");
            }

        } catch (ConnectionPoolException | SQLException e) {
            logger.error("Deleting object from the database failed", e);
            throw new DAOException(e);

        } finally {
            connectionProvider.close(connection);
            connectionProvider.closeStatement(ps);
        }
    }


    @Override
    public List<T> loadAll() throws DAOException {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<T> allObjects = new ArrayList<>();

        try {
            connection = connectionProvider.obtainConnection();
            ps = connection.prepareStatement(getQueryForAllObjects());

            rs = ps.executeQuery();

            while (rs.next()) {
                allObjects.add(readResultSet(rs));
            }

        } catch (ConnectionPoolException | SQLException e) {
            logger.error("Loading all objects from the database to list failed", e);
            throw new DAOException(e);

        } finally {
            connectionProvider.close(connection);
            connectionProvider.closeResultSetAndStatement(rs, ps);
        }

        return allObjects;
    }


    @Override
    public List<T> loadAllById(int id) throws DAOException {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<T> allObjects = new ArrayList<>();

        try {
            connection = connectionProvider.obtainConnection();
            ps = connection.prepareStatement(getQueryForAllObjectsById());

            ps.setInt(1, id);

            rs = ps.executeQuery();

            while (rs.next()) {
                allObjects.add(readResultSet(rs));
            }

        } catch (ConnectionPoolException | SQLException e) {
            logger.error("Loading all objects by ID from the database to list failed", e);
            throw new DAOException(e);

        }  finally {
            connectionProvider.close(connection);
            connectionProvider.closeResultSetAndStatement(rs, ps);
        }

        return allObjects;
    }


    @Override
    public List<T> loadAllByStatus(String status) throws DAOException {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<T> allObjects = new ArrayList<>();

        try {
            connection = connectionProvider.obtainConnection();
            ps = connection.prepareStatement(getQueryForAllObjectsByStatus());

            ps.setString(1, status);
            rs = ps.executeQuery();

            while (rs.next()) {
                allObjects.add(readResultSet(rs));
            }

        } catch (ConnectionPoolException | SQLException e) {
            logger.error("Loading all objects by status from the database to list failed", e);
            throw new DAOException(e);

        } finally {
            connectionProvider.close(connection);
            connectionProvider.closeResultSetAndStatement(rs, ps);
        }

        return allObjects;
    }


    @Override
    public T getById(int id) throws DAOException {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = connectionProvider.obtainConnection();
            ps = connection.prepareStatement(getQueryForSingleObject());

            ps.setInt(1, id); // Отдельно вынести констант ID
            rs = ps.executeQuery();
            rs.next();

            return readResultSet(rs);


        } catch (ConnectionPoolException | SQLException e) {
            logger.error("Loading object from the database by ID failed", e);
            throw new DAOException(e);

        }  finally {
            connectionProvider.close(connection);
            connectionProvider.closeResultSetAndStatement(rs, ps);
        }
    }

    // не паблик?
    public abstract void executeCreateQuery(PreparedStatement ps, T object) throws SQLException;

    public abstract void executeUpdateQuery(PreparedStatement ps, T object) throws SQLException;

    public abstract void executeUpdateStatusQuery(PreparedStatement ps, String status, int id) throws SQLException;

    public abstract void executeDeleteQuery(PreparedStatement ps, int id) throws SQLException;

    public abstract T readResultSet(ResultSet rs) throws SQLException;

    public abstract String getCreateQuery();

    public abstract String getUpdateQuery();

    public abstract String getUpdateStatusQuery();

    public abstract String getDeleteQuery();

    public abstract String getQueryForAllObjects();

    public abstract String getQueryForAllObjectsByStatus();

    public abstract String getQueryForAllObjectsById();

    public abstract String getQueryForSingleObject();
}