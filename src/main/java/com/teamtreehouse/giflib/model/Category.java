package com.teamtreehouse.giflib.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Category {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    // if you use @Id annotation, every other non-transient field
    // will be annotated as @Column by default

    // validation annotations
    @NotNull
    // message is not mandatory parameter, othwerwise default message will be displayed
    // however, it's better to externalize messages
    // look in AppConfig, to see how to tell Spring to use message.properties file we created in resources
    @Size(min=3, max=12, message="The category name must be {min} to {max} characters in length")
    private String name;

    @NotNull
    @Pattern(regexp = "#[0-9a-fA-F]{6}")
    // start with '#', contain digits from 0-9 or letters from a to f (both upper and lower case. repeat it 6 times
    private String colorCode;

    // one category object can be associated with many gifs
    // this is caled OneToMany realtionship
    // Category field in Gif.java is annotated as @ManyToOne accordingly
    // means that it is the 'category' field in the gif entity
    // that will be used to map the category of each gif
    // FIXME need to understand that
    /* Annotation @OneToMany by default sets lazy initialization,
    * in other words, this collection is configured to not to initialize when a category entity is fetched from a database
    * thus avoiding fetching all the gifs without the need (additional queries) when not referred to
    * Solution 1: set fetch type to EAGER (fetch = FetchType.EAGE)
    * Solution 2: see category controller delete() and findById() in categoryDaoImpl*/
    @OneToMany(mappedBy = "category")
    private List<Gif> gifs = new ArrayList<>();


    public Category(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public List<Gif> getGifs() {
        return gifs;
    }

}
