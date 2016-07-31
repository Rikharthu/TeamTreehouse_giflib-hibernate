package com.teamtreehouse.giflib.dao;

import com.teamtreehouse.giflib.model.Category;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

// annotate the class so that's it's picked up with component scanning by Spring
@Repository
public class CategoryDaoImpl implements CategoryDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Category> findAll() {
        // Open a session
        Session session = sessionFactory.openSession();

        // Get all categories with a Hobernate criteria
        // createCriteria(Category.class) will fetch only those objects of Category class
        @SuppressWarnings("unchecked")
        List<Category> categories = session.createCriteria(Category.class).list();

        // Close session
        session.close();

        return categories;
    }

    @Override
    public Category findById(Long id) {
        Session session = sessionFactory.openSession();
        Category category = session.get(Category.class, id);
        // Make sure that gifs collection is initialized before session is closed
        Hibernate.initialize(category.getGifs());
        session.close();
        return category;
    }

    @Override
    public void save(Category category) {
        // Open a session
        Session session = sessionFactory.openSession();

        // Begin a transaction ( ensure that edit are complete before commiting to the database)
        session.beginTransaction();

        // Save the category
        // or update. .save always saves. saveOrUpdate checks existing id
         session.saveOrUpdate(category);

        // Commit the transaction
        session.getTransaction().commit();

        // Close the session
        session.close();

    }

    @Override
    public void delete(Category category) {
        // Open a session
        Session session = sessionFactory.openSession();

        // Begin a transaction ( ensure that edit are complete before commiting to the database)
        session.beginTransaction();

        // Save the category
        // or update. .save always saves. saveOrUpdate checks existing id
        session.delete(category);

        // Commit the transaction
        session.getTransaction().commit();

        // Close the session
        session.close();
    }
}
