package com.example.layeredarchitecture.Dao;

import com.example.layeredarchitecture.db.DBConnection;
import com.example.layeredarchitecture.model.ItemDTO;
import com.example.layeredarchitecture.model.OrderDTO;
import com.example.layeredarchitecture.model.OrderDetailDTO;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class OrderDAOImpl implements OrderDAO {


    OrderDetailDAO orderDetailsDAO = new OrderDetailDAOImpl();

    public OrderDAOImpl() throws SQLException, ClassNotFoundException {
    }

    public ResultSet generateNewOrderID() throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getDbConnection().getConnection();
        Statement stm = connection.createStatement();
        ResultSet rst = stm.executeQuery("SELECT oid FROM `Orders` ORDER BY oid DESC LIMIT 1;");
        return rst;
    }

    /* public String lastOrderId() throws SQLException, ClassNotFoundException {
         Connection connection = DBConnection.getDbConnection().getConnection();
         Statement stm = connection.createStatement();
         ResultSet rst = stm.executeQuery("SELECT oid FROM `Orders` ORDER BY oid DESC LIMIT 1;");

         return rst.next() ? String.format("OID-%O3d", (Integer.parseInt(rst.getString("Oid").replace("OID-", "")) + 1)) : "OID-001";
     }*/
    public String generateNextId(String orderId) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getDbConnection().getConnection();
        Statement stm = connection.createStatement();
        ResultSet rst = stm.executeQuery("SELECT oid FROM `Orders` ORDER BY oid DESC LIMIT 1;");

        return rst.next() ? String.format("OID-%03d", (Integer.parseInt(rst.getString("oid").replace("OID-", "")) + 1)) : "OID-001";

    }

    public ItemDTO searchItem(String code) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getDbConnection().getConnection();
        PreparedStatement pstm = connection.prepareStatement("SELECT * FROM Item WHERE code=?");
        pstm.setString(1, code);
        ResultSet rst = pstm.executeQuery();
        rst.next();

        return new ItemDTO(code,rst.getString("description"),rst.getBigDecimal("unitPrice"),rst.getInt("qtyOnHand"));

    }


    /* public boolean checkOrderIdExist(String orderId) throws SQLException, ClassNotFoundException {
          Connection connection=DBConnection.getDbConnection().getConnection();
          PreparedStatement stm=connection.prepareStatement("SELECT oid FROM 'orders' WHERE oid=?");
           stm.setString(1, orderId);
           return stm.executeQuery().next();
     }*/
    public boolean checkOrderIdExist(String orderId) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getDbConnection().getConnection();
        PreparedStatement stm = connection.prepareStatement("SELECT oid FROM `Orders` WHERE oid=?");
        stm.setString(1, orderId);
        return stm.executeQuery().next();
    }




    public boolean save(OrderDTO orderDTO, List<OrderDetailDTO> orderDetails) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getDbConnection().getConnection();
        connection.setAutoCommit(false);
        PreparedStatement stm = connection.prepareStatement("INSERT INTO `Orders` (oid, date, customerID) VALUES (?,?,?)");
        stm.setString(1, orderDTO.getOrderId());
        stm.setDate(2, Date.valueOf(orderDTO.getOrderDate()));
        stm.setString(3, orderDTO.getCustomerId());


        if (stm.executeUpdate() == 1) {

            //OrderDetailDAO orderDetailsDAO = new OrderDetailDAOImpl();

            if (orderDetailsDAO.addOrderDetails(orderDTO.getOrderId(), orderDetails)) {

                connection.commit();
                connection.setAutoCommit(true);
                return true;

            }

            connection.rollback();
            connection.setAutoCommit(true);


        }
        connection.rollback();
        connection.setAutoCommit(true);
        return false;
    }

    /* public boolean saveOrder(String orderId, LocalDate orderDate, String customerId) throws SQLException, ClassNotFoundException {
         Connection connection = DBConnection.getDbConnection().getConnection();
         PreparedStatement stm = connection.prepareStatement("INSERT INTO `Orders` (oid, date, customerID) VALUES (?,?,?)");
         stm.setString(1, orderId);
         stm.setDate(2, Date.valueOf(orderDate));
         stm.setString(3, customerId);

         return stm.executeUpdate() == 1;

        /* if (stm.executeUpdate() == 1) {

             if (orderDetailsDAO.addOrderDetails(orderDTO.getOrderId(), orderDetails)) {

                 connection.commit();
                 connection.setAutoCommit(true);
                 return true;

             }

             connection.rollback();
             connection.setAutoCommit(true);


         }connection.rollback();
         connection.setAutoCommit(true);
         return false;*/
    public boolean saveOrder(String orderId, LocalDate orderDate, String customerId) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getDbConnection().getConnection();
        PreparedStatement stm = connection.prepareStatement("INSERT INTO `Orders` (oid, date, customerID) VALUES (?,?,?)");
        stm.setString(1, orderId);
        stm.setDate(2, Date.valueOf(String.valueOf(orderDate)));
        stm.setString(3, customerId);

        return stm.executeUpdate() > 0;
    }
    public boolean searchOrder(String orderId) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getDbConnection().getConnection();
        PreparedStatement stm = connection.prepareStatement("SELECT oid FROM `Orders` WHERE oid=?");
        stm.setString(1, orderId);

        return stm.executeQuery().next();
    }

    public boolean placeOrder(String orderId, LocalDate orderDate, String customerId, List<OrderDetailDTO> orderDetails) throws SQLException {
        Connection connection = null;
        try {
            connection = DBConnection.getDbConnection().getConnection();

            OrderDAO orderDAO = new OrderDAOImpl();

            boolean isExist = orderDAO.searchOrder(orderId);

            if (isExist) {

            }
            connection.setAutoCommit(false);

            boolean isSaved = orderDAO.saveOrder(orderId,orderDate,customerId);

            if (!isSaved) {
                connection.rollback();
                connection.setAutoCommit(true);
                return false;
            }
            for(OrderDetailDTO detailDTO : orderDetails) {

                OrderDetailDAO orderDetailDAO = new OrderDetailDAOImpl();
                boolean isDetailSaved = orderDetailDAO.saveDetails(detailDTO, orderId);

                if (!isDetailSaved) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    return false;
                }

                ItemDAOImpl itemDAO = new ItemDAOImpl();
                ItemDTO itemDTO = itemDAO.searchItem(detailDTO.getItemCode());
                itemDTO.setQtyOnHand(itemDTO.getQtyOnHand() - detailDTO.getQty());

                boolean isUpdated = itemDAO.updateItem(itemDTO);

                if (!isUpdated) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    return false;
                }

            }
            connection.commit();
            connection.setAutoCommit(true);
            return true;

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


}

