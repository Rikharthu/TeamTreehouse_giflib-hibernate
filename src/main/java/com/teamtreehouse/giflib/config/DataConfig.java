package com.teamtreehouse.giflib.config;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;

@Configuration
@PropertySource("app.properties")
public class DataConfig {

    // Spring will load all the properties from 'app.properties' file
    // to this Environment object
    /* Interface representing the environment in which the current application
    is running. Models two key aspects of the application environment:
    profiles and properties */
    /* Using the @Autowired annotation in Spring defaults to finding
    exactly one class that implements the interface of the object marked as @Autowired. */
    @Autowired
    private Environment env;

    /** Spring equivalent to SessionFactory from hibernate basics course
     * we will use Spring to scan for JPA-annotated entities, instead of
     * adding them to hibernate.cfg.xml manually.
     * in other words, we do not need this property anymore:
     * <mapping class="com.teamtreehouse.contactmgr.model.Contact" />
     * this is done with sessionFactory.setPackagesToScan() method
      */

    // Spring will create object from methods, annotated with @Bean
    /* By including a method annotated with @Bean whose return value is of type LocalSessionFactoryBean,
    a SessionFactory is now a candidate for autowiring. Notice the <SessionFactory> listed as the
    type parameter of FactoryBean<SessionFactory>. The FactoryBean interface has three methods that
    Spring uses to autowire. */
    @Bean
    public LocalSessionFactoryBean sessionFactory(){
        // will refer to our hibernate.cfg.xml
        // hibernate configuration
        Resource config = new ClassPathResource("hibernate.cfg.xml");

        // FactoryBean that creates a Hibernate SessionFactory
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();

        // Set the location of a single Hibernate XML config file,
        // for example as classpath resource "classpath:hibernate.cfg.xml".
        // according to the factory pattern, we pass configurationfile to the factory
        // so that factory will know which object to create and how to do it
        sessionFactory.setConfigLocation(config);

        // Specify packages to search for autodetection of your entity classes in the classpath.
        // This is analogous to Spring's component-scan feature
        // see resources/app.properties ,  where we have this 'key=value' pair:
        // 'giflib.entity.package= com.teamtreehouse.giflib.model'
        sessionFactory.setPackagesToScan(env.getProperty("giflib.entity.package"));

        //
        sessionFactory.setDataSource(dataSource());

        return sessionFactory;
    }

    /**A factory for connections to the physical data source that this
     * DataSource object represents.
     *
     * @return
     */
    @Bean
    // Bean must be public. Beans are POJOs managed and instantited by Spring container
    // DataSource is an interface. Spring doesnt know that our current implementation will be BasicDataSource class
    // A factory for connections to the physical data source that this DataSource object represents.
    // An alternative for the DriverManager
    public DataSource dataSource() {
        // Basic implementation of javax.sql.DataSource that is configured via JavaBeans properties.
        BasicDataSource ds = new BasicDataSource();

        // Driver class name
        ds.setDriverClassName(env.getProperty("giflib.db.driver"));

        // Set URL
        ds.setUrl(env.getProperty("giflib.db.url"));

        // Set username & password
        ds.setUsername(env.getProperty("giflib.db.username"));
        ds.setPassword(env.getProperty("giflib.db.password"));

        return ds;
    }

}
