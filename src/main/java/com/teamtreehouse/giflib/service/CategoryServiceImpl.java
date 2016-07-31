package com.teamtreehouse.giflib.service;

import com.teamtreehouse.giflib.dao.CategoryDao;
import com.teamtreehouse.giflib.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{

    // Spring will find the only implementation of this interface
    // and link (wire) it to this variable
    @Autowired
    CategoryDao categoryDao;

    public List<Category> findAll() {
        return categoryDao.findAll();
    }

    public Category findById(Long id) {
        return categoryDao.findById(id);
    }

    public void save(Category category) {
        // here we just redicret to categoryDay
        // howeverm any additional business logic should reside here
        categoryDao.save(category);
    }

    public void delete(Category category) {
        categoryDao.delete(category);
    }
}
