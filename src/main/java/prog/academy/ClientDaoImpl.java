package prog.academy;

import java.sql.Connection;

public class ClientDaoImpl extends AbstractDAO<Client>{

    public ClientDaoImpl(Connection conn, String table) {
        super(conn, table);
    }
}
