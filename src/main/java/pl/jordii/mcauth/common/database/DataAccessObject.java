package pl.jordii.mcauth.common.database;

import java.sql.SQLException;

public interface DataAccessObject {

    void prepare() throws SQLException;

}
