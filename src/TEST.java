import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class TEST {

    Connection conn = null;

    private boolean connectToDataBase(){
        try {
            //Get a properties variable we can pass the username and password to
            // the database.
            Properties info = new Properties();

            //Type in your ID number instead of the XXXXXX
            String username = "u267938";
            String pass = "p267938";
            String schema = "schema267938_store";

            //set the username and password appropriately
            info.put( "user", username );
            info.put( "password", pass );
            //connect to the database
            conn = DriverManager.getConnection("jdbc:mysql://CSDB1901/"+schema,
                    info);
            //if all goes well, this statement should print
            System.out.println("Connection successful!");
            return true;

        } catch (SQLException ex) {
            //if an exception is thrown, display the message so that we know
            // what went wrong.
            System.out.print(ex.getMessage());
            return false;
        }

    }

    void disConnect() throws SQLException {
        if(conn != null){
            conn.close();
        }
    }


    private boolean insertINtoPayments(int payment_id, int client_id, int invoice_id,
                                       Date payment_date, double amount, int payment_method){
        try{
            //generate prepared statemnt
            PreparedStatement pstmt = conn.prepareStatement("" +
                    "insert into payments values (?,?,?,?,?,?)");
            pstmt.setInt(1,payment_id);
            pstmt.setInt(2,client_id);
            pstmt.setInt(3,invoice_id);
            pstmt.setDate(4,payment_date);
            pstmt.setDouble(5,amount);
            pstmt.setInt(6,payment_method);

            int rows = pstmt.executeUpdate(); //rows the num of rows that are updated

            if (rows>0){
                System.out.println("insert success");
                return true;
            }else {
                System.out.println("insert failed");
                return false;
            }

        }catch (SQLException e){
            System.out.println("failed to insert, exception is " + e.getMessage());
            return false;
        }
    }


    private boolean insertIntoProducts (int product_id, String name, int quantity, double price){
        try{
            PreparedStatement pstmt = conn.prepareStatement("" +
                    "insert into products values (?,?,?,?)");
            pstmt.setInt(1,product_id);
            pstmt.setString(2, name);
            pstmt.setInt(3,quantity);
            pstmt.setDouble(4,price);

            int rows = pstmt.executeUpdate(); //rows the num of rows that are updated

            if (rows>0){
                System.out.println("insert success");
                return true;
            }else {
                System.out.println("insert failed");
                return false;
            }

        }catch (SQLException e){
            System.out.println("sql error: " + e.getMessage());
            return false;
        }
    }

    private boolean deleteFromPaymentsTable(int payment_id){
        try{
            PreparedStatement pstmt = conn.prepareStatement("" + "delete from payments where payment_id = ?");
            pstmt.setInt(1,payment_id);

            int rows = pstmt.executeUpdate(); // how many rows have been updated
            if (rows > 0){
                System.out.println("success delete");
                return true;
            } else {
                System.out.println("failed delete");
                return false;
            }
        }
        catch (SQLException e){
            System.out.println("failed to delete due to " + e.getMessage());
            return false;
        }
    }

    private boolean updatePaymentsSetamountWithID(double amount, int payment_id){
        try {
            PreparedStatement pstmt = conn.prepareStatement("" + "update payments set amount = 12 where payment_id = 9");
            pstmt.setDouble(1, amount);
            pstmt.setInt(2,payment_id);

            int rows = pstmt.executeUpdate();

            if (rows > 0){
                System.out.println("success delete");
                return true;
            } else {
                System.out.println("failed delete");
                return false;
            }

        } catch (SQLException e){
            System.out.println("fail");
            return false;
        }
    } // this wrong lol

    private boolean updateProducts(String name, double price, int product_id){
        try {
            PreparedStatement pstmt = conn.prepareStatement("" + "update products set name = ?, unit_price" +
                    " = ? where product_id = ?");
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, product_id);


            int rows = pstmt.executeUpdate();

            if (rows > 0){
                System.out.println("success update");
                return true;
            } else {
                System.out.println("failed update");
                return false;
            }

        } catch (SQLException e){
            System.out.println("fail: " + e.getMessage());
            return false;
        }
    }

    private boolean deleteFromProducts(int product_id){
        try{
            PreparedStatement pstmt = conn.prepareStatement("" + "delete from products where product_id = ?");
            pstmt.setInt(1,product_id);

            int rows = pstmt.executeUpdate(); // how many rows have been updated
            if (rows > 0){
                System.out.println("success delete");
                return true;
            } else {
                System.out.println("failed delete");
                return false;
            }
        }
        catch (SQLException e){
            System.out.println("failed to delete due to " + e.getMessage());
            return false;
        }
    }

    private void selectPaymentsByAmount(double amount) throws SQLException{
        PreparedStatement pstmt = conn.prepareStatement("" + "select * from payments where amount > ?");
        pstmt.setDouble(1, amount);

        ResultSet rst = pstmt.executeQuery();

        while (rst.next()){
            int payment_id = rst.getInt(1); // payment id
            int client_id = rst.getInt(2);
            int invoice_id = rst.getInt(3);
            Date  payment_date = rst.getDate(4);
            double amount_ = rst.getDouble(5);
            int payment_method = rst.getInt(6);

            System.out.printf("%d %d %d %s %.2f %d\n",
                    payment_id, client_id, invoice_id, payment_date.toString(), amount_, payment_method );

        }
    }

    private void selectProductsByStock(int Quantity) throws SQLException{
        PreparedStatement pstmt = conn.prepareStatement("" + "select * from products where quantity_in_stock > ?");
        pstmt.setInt(1, Quantity);

        ResultSet rst = pstmt.executeQuery();

        while (rst.next()){
            int product_id = rst.getInt(1); // payment id
            String name = rst.getString(2);
            int quantity_in_stock = rst.getInt(3);
            double unit_price = rst.getDouble(4);

            System.out.printf("%d %s %d %.2f\n",
                    product_id, name, quantity_in_stock, unit_price );

        }
    }

    private void findCustomersBuyProducts(int product_1, int product_2) throws SQLException{
        PreparedStatement pstmt = conn.prepareStatement("" + "select customer_id from orders join order_items using" +
                " (order_id) where product_id = ? and customer_id in (select distinct customer_id from orders join order_items using" +
                " (order_id) where product_id = ?)");
        pstmt.setInt(1, product_1);
        pstmt.setInt(2, product_2);

        ResultSet rst = pstmt.executeQuery();

        while (rst.next()){
            int customer_id = rst.getInt(1); // payment id


            System.out.printf("%d\n",
                    customer_id );

        }
    }



    public static void main(String[] args) throws SQLException {
        TEST dbtest = new TEST();
        if(dbtest.connectToDataBase()){
            //success

            java.sql.Date cur_date = new java.sql.Date(System.currentTimeMillis());


            dbtest.findCustomersBuyProducts(1,2);
//            dbtest.selectProductsByStock(20);
//            dbtest.insertINtoPayments(27,5,18, cur_date, 20.0, 1);
//            dbtest.insertIntoProducts(11, "Fried Chicken", 24, 3.24);
//            dbtest.deleteFromPaymentsTable(27); // delete thingy
//            dbtest.updateProducts("dave chicken",10.0,11);
//            dbtest.deleteFromProducts(11);
//            dbtest.selectPaymentsByAmount(30.0);

            //always close it baby
            dbtest.disConnect();

        } else {
            System.out.println("\nfailed to connect");
        }
    }




}
