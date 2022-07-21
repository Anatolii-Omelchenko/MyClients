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
            System.out.println("Before:");
            System.out.println(testClient);
            dao.add(testClient);

            System.out.println("From select (name,id)");
            List<Client> clientList = dao.getAll(Client.class, "name", "id");
            for (Client cl : clientList) {
                System.out.println(cl);
            }

            System.out.println("After:");
            System.out.println(testClient);


        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
