package com.example.layeredarchitecture.Dao;

import com.example.layeredarchitecture.model.OrderDetailDTO;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public interface OrderDetailDAO {
    boolean addOrderDetails(String orderId, List<OrderDetailDTO> orderDetails) throws SQLException, ClassNotFoundException;
    // public boolean saveOrderDetail(String id , OrderDetailDTO orderDetailDTO) throws SQLException ;
    public boolean saveDetails(OrderDetailDTO detailDTO, String orderId) throws SQLException, ClassNotFoundException ;
}