package com.teamtreehouse.giflib.web.controller;

import com.teamtreehouse.giflib.model.Category;
import com.teamtreehouse.giflib.service.CategoryService;
import com.teamtreehouse.giflib.web.Color;
import com.teamtreehouse.giflib.web.FlashMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Index of all categories (GET request)
    @RequestMapping("")
    public String listCategories(Model model) {
        // TODO: Get all categories
        List<Category> categories = categoryService.findAll();
        System.out.println("CategoryController.listCategories");
        model.addAttribute("categories",categories);
        return "category/index";
    }

    // Add a category (POST request)
    @RequestMapping(value = "", method = RequestMethod.POST)
    // @Valid annotation triggers validation
    public String addCategory(@Valid Category category, BindingResult result,RedirectAttributes redirectAttributes) {
        // redirectAttributes will survive exactly one redirection
        // operates on form.html
        // TODO: Add category if valid data was received
        // If there are validation errors
        if(result.hasErrors()){
            // Include validation errors upon redirect
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.category",result);
            // Spring is able to combine above and below
            // Add category if invalid data was received
            // flashAttribute survives exactly 1 redirection
            redirectAttributes.addFlashAttribute("category",category);
            redirectAttributes.addFlashAttribute("flash",new FlashMessage("Category could not be added!", FlashMessage.Status.FAILURE));
            // redirect back to the "/add". category parametes, that were invalid would be saved
            // for user to finish/fix them (look formNewCategory() )
            return "redirect:/categories/add";
        }


        // Everything OK
        categoryService.save(category);
        redirectAttributes.addFlashAttribute("flash",new FlashMessage("Category successfully added!", FlashMessage.Status.SUCCESS));
        // TODO: Redirect browser to /categories
        // will redirect to localhost:8080/categories as a get request
        // listCategories controller method will catch it afterwards
        return "redirect:/categories";
    }

    // Update an existing category
    @RequestMapping(value = "/{categoryId}", method = RequestMethod.POST)
    // category will be passed from form submission
    public String updateCategory(@Valid Category category, BindingResult result, RedirectAttributes redirectAttributes) {
        // TODO: Update category if valid data was received
        if(result.hasErrors()){
            // Include validation errors upon redirect
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.category",result);
            // Spring is able to combine above and below
            // Add category if invalid data was received
            // flashAttribute survives exactly 1 redirection
            redirectAttributes.addFlashAttribute("category",category);
            redirectAttributes.addFlashAttribute("flash",new FlashMessage("Category could not be updated!", FlashMessage.Status.FAILURE));

            return String.format("redirect:/categories/%s/edit",category.getId());
        }


        // Everything OK
        categoryService.save(category);
        redirectAttributes.addFlashAttribute("flash",new FlashMessage("Category successfully Updated!", FlashMessage.Status.SUCCESS));

        // will redirect to localhost:8080/categories as a get request
        // listCategories controller method will catch it afterwards
        return "redirect:/categories";

    }

    // Single category page
    @RequestMapping("/{categoryId}")
    public String category(@PathVariable Long categoryId, Model model) {
        // TODO: Get the category given by categoryId
        Category category = null;

        model.addAttribute("category", category);
        return "category/details";
    }

    // Form for adding a new category
    @RequestMapping("/add")
    public String formNewCategory(Model model) {
        // TODO: Add model attributes needed for new form

        // check if user has already submitted category form, but was redirected by validation
        if(!model.containsAttribute("category"))
            // thus override category attribute only if it is empty
            model.addAttribute("category", new Category());

        model.addAttribute("colors", Color.values());
        // see category/form.html
        model.addAttribute("action","/categories"); //where to redirect after clicking submit
        model.addAttribute("heading","New Category"); // Heading text
        model.addAttribute("submit","Add"); // button text


        return "category/form";
    }

    // Form for editing an existing category
    @RequestMapping("/{categoryId}/edit")
    public String formEditCategory(@PathVariable Long categoryId, Model model) {
        // TODO: Add model attributes needed for edit form
        // Add selected category to model, thus filling form with current properties
        if(!model.containsAttribute("category"))
            // thus override category attribute only if it is empty
            model.addAttribute("category", categoryService.findById(categoryId));

        model.addAttribute("colors", Color.values());
        model.addAttribute("action",String.format("/categories/%s", categoryId));
        model.addAttribute("heading","Edit Category");
        model.addAttribute("submit","Update");

        return "category/form";
    }

    // Delete an existing category
    @RequestMapping(value = "/{categoryId}/delete", method = RequestMethod.POST)
    public String deleteCategory(@PathVariable Long categoryId, RedirectAttributes redirectAttributes) {
        Category cat = categoryService.findById((categoryId));

        // TODO: Delete category if it contains no GIFs
        // FIXME move this check into a service and throw exception
        if(cat.getGifs().size() > 0){
            redirectAttributes.addFlashAttribute("flash",new FlashMessage("Only empty categories can be deleted.", FlashMessage.Status.FAILURE ));
            return String.format("redirect:/categories/%s/edit", categoryId);
        }
        // Everything OK at this point
        categoryService.delete(cat);
        redirectAttributes.addFlashAttribute("flash",new FlashMessage("Category deleted!", FlashMessage.Status.SUCCESS ));

        // TODO: Redirect browser to /categories
        // listCategories
        return "redirect:/categories";
    }
}
