package com.example.layeredarchitecture.Dao;

import com.example.layeredarchitecture.model.CustomerDTO;

import java.sql.SQLException;
import java.util.ArrayList;

public interface CustomerDAO {

    public ArrayList<CustomerDTO> getAllCustomer() throws SQLException, ClassNotFoundException;

    public void saveAllCustomer(CustomerDTO customer) throws SQLException, ClassNotFoundException ;

    public void UpdateCustomer(CustomerDTO customer) throws SQLException, ClassNotFoundException;

    public void deleteCustomer(String id) throws SQLException, ClassNotFoundException;

    public boolean existCustomer(String id) throws SQLException, ClassNotFoundException;

    public String generateID() throws SQLException, ClassNotFoundException ;


    CustomerDTO findCustomer(String newValue) throws SQLException, ClassNotFoundException;
}