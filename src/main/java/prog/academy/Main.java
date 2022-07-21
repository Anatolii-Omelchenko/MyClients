package prog.academy;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        try (Connection conn = ConnectionFactory.getConnection()) {

            ClientDaoImpl dao = new ClientDaoImpl(conn, "Clients");

            dao.dropTable(Client.class);
            dao.createTable(Client.class);

            Client testClient = new Client("Andrew", 27);
            dao.add(testClient);

            List<Client> clientList = dao.getAll(Client.class);
            for (Client cl : clientList) {
                System.out.println(cl);
            }

            testClient = clientList.get(0);
            testClient.setAge(32);
            testClient.setName("Boris");

            dao.update(testClient);

            List<Client> clientList2 = dao.getAll(Client.class);
            for (Client cl : clientList2) {
                System.out.println(cl);
            }

            dao.delete(testClient);

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
