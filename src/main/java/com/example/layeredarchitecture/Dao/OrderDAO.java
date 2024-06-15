package com.example.layeredarchitecture.Dao;

import com.example.layeredarchitecture.db.DBConnection;
import com.example.layeredarchitecture.model.ItemDTO;
import com.example.layeredarchitecture.model.OrderDTO;
import com.example.layeredarchitecture.model.OrderDetailDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface OrderDAO {
    // String lastOrderId() throws SQLException, ClassNotFoundException;

    public String generateNextId(String orderId) throws SQLException, ClassNotFoundException ;
    public ItemDTO searchItem(String code) throws SQLException, ClassNotFoundException ;
    public boolean searchOrder(String orderId) throws SQLException, ClassNotFoundException ;
    boolean checkOrderIdExist(String orderId) throws SQLException, ClassNotFoundException;

    public boolean saveOrder(String orderId, LocalDate orderDate, String customerId) throws SQLException, ClassNotFoundException ;

    boolean placeOrder(String orderId, LocalDate orderDate, String customerId, List<OrderDetailDTO> orderDetails) throws SQLException, ClassNotFoundException;
}
