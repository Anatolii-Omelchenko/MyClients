package prog.academy;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        try (Connection conn = ConnectionFactory.getConnection()) {
            ClientDaoImpl dao = new ClientDaoImpl(conn,"Clients");
            dao.dropTable(Client.class);
            dao.createTable(Client.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
