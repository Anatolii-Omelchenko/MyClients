package prog.academy;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AbstractDAO<T> {
    private final Connection conn;
    private final String table;

    public AbstractDAO(Connection conn, String table) {
        this.conn = conn;
        this.table = table;
    }

    public void dropTable(Class<T> cls){
        try(Connection conn = ConnectionFactory.getConnection();
            Statement st = conn.createStatement()){

            String sql = "DROP TABLE IF EXISTS " + table;
            st.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTable(Class<T> cls) {
        Field[] fields = cls.getDeclaredFields();
        Field id = getPrimaryKeyField(fields);

        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ")
                .append(table)
                .append("(");

        sql.append(id.getName())
                .append(" ")
                .append(" INT NOT NULL AUTO_INCREMENT PRIMARY KEY,");

        for (Field f : fields) {
            if (f != id) {
                f.setAccessible(true);

                sql.append(f.getName()).append(" ");

                if (f.getType() == int.class) {
                    sql.append("INT,");
                } else if (f.getType() == String.class) {
                    sql.append("VARCHAR(100),");
                } else {
                    throw new RuntimeException("Wrong type!");
                }
            }
        }

        sql.deleteCharAt(sql.length() - 1);
        sql.append(")");

        try (Statement st = conn.createStatement()) {
            st.execute(sql.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
    }

    public void add(T t) {
        Field[] fields = t.getClass().getDeclaredFields();
        Field id = getPrimaryKeyField(fields);

        StringBuilder names = new StringBuilder();
        StringBuilder values = new StringBuilder();

        try {
            for (Field f : fields) {
                if (f != id) {
                    f.setAccessible(true);
                    names.append(f.getName()).append(",");
                    values.append('"').append(f.get(t)).append("\",");
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        names.deleteCharAt(names.length() - 1);
        values.deleteCharAt(values.length() - 1);

        String sql = "INSERT INTO " + table + "(" + names + ") VALUES (" + values + ")";

        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(T t) {
        Field[] fields = t.getClass().getDeclaredFields();
        Field id = getPrimaryKeyField(fields);

        StringBuilder sb = new StringBuilder();
        String sql = "";
        try {
            for (Field f : fields) {
                if (f != id) {
                    f.setAccessible(true);
                    sb.append(f.getName())
                            .append(" = \"")
                            .append(f.get(t))
                            .append('"')
                            .append(",");
                }
            }

            sb.deleteCharAt(sb.length() - 1);
            sql = "UPDATE " + table + " SET " + sb + " WHERE " + id.getName() + "=" + id.get(t);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(T t) {
        Field[] fields = t.getClass().getDeclaredFields();
        Field id = getPrimaryKeyField(fields);

        String sql = null;
        try {
            sql = "DELETE from " + table + " WHERE " + id.getName() + "=" + id.get(t);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<T> getAll(Class<T> cls) {
        List<T> result = new ArrayList<>();

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM " + table)) {
            ResultSetMetaData md = rs.getMetaData();

            while(rs.next()){
                T t = cls.newInstance(); //!!!
                for(int i = 1; i<md.getColumnCount();i++){
                    String columnName = md.getColumnName(i);
                    Field field = cls.getField(columnName);
                    field.setAccessible(true);
                    field.set(t, rs.getObject(columnName));
                }
                result.add(t);
            }

        } catch (Exception ex) {
            throw new RuntimeException();
        }

        return  result;

    }


    private Field getPrimaryKeyField(Field[] fields) {

        for (Field f : fields) {
            if (f.isAnnotationPresent(Id.class)) {
                f.setAccessible(true);
                return f;
            }
        }
        throw new RuntimeException("Primary key was not found!");
    }
}
