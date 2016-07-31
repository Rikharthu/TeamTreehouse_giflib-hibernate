package com.teamtreehouse.giflib.web.controller;

import com.teamtreehouse.giflib.model.Gif;
import com.teamtreehouse.giflib.service.CategoryService;
import com.teamtreehouse.giflib.service.GifService;
import com.teamtreehouse.giflib.web.FlashMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class GifController {

    @Autowired
    private GifService gifService;

    @Autowired
    private CategoryService categoryService;

    // Home page - index of all GIFs
    @RequestMapping("/")
    public String listGifs(Model model) {
        // TODO: Get all gifs
        List<Gif> gifs = gifService.findAll();

        model.addAttribute("gifs", gifs);
        return "gif/index";
    }

    // Single GIF page
    @RequestMapping("/gifs/{gifId}")
    public String gifDetails(@PathVariable Long gifId, Model model) {
        // TODO: Get gif whose id is gifId
        Gif gif = gifService.findById(gifId);

        model.addAttribute("gif", gif);
        return "gif/details";
    }

    // GIF image data
    @RequestMapping("/gifs/{gifId}.gif")
    @ResponseBody // will return actual data, instead of thymeleaf template (we dont thymeleaf to render dat)
    public byte[] gifImage(@PathVariable Long gifId) {
        // TODO: Return image data as byte array of the GIF whose id is gifId
        // this byte array will be rendered by browser
        return gifService.findById(gifId).getBytes();
    }

    // Favorites - index of all GIFs marked favorite
    @RequestMapping("/favorites")
    public String favorites(Model model) {
        // TODO: Get list of all GIFs marked as favorite
        List<Gif> faves = new ArrayList<>();

        model.addAttribute("gifs",faves);
        model.addAttribute("username","Chris Ramacciotti"); // Static username
        return "gif/favorites";
    }

    // Upload a new GIF
    @RequestMapping(value = "/gifs", method = RequestMethod.POST)
    // A representation of an uploaded file received in a multipart request.
    // MultipartFile is used to directly capture file data from a POST request
    public String addGif(@Valid Gif gif,BindingResult result, @RequestParam MultipartFile file, RedirectAttributes redirectAttributes) {
        // TODO: Upload new GIF if data is valid
        System.out.println("\nGOTCHA\n");
        if(result.hasErrors()){
            // TODO add multipartfile!!!
            System.out.println("\nHAS ERROR\n");
            // Include validation errors upon redirect
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.gif",result);
            // Spring is able to combine above and below
            // Add category if invalid data was received
            // flashAttribute survives exactly 1 redirection
            redirectAttributes.addFlashAttribute("gif",gif);
            redirectAttributes.addFlashAttribute("file",file);
            redirectAttributes.addFlashAttribute("flash",new FlashMessage("Gif could not be updated!", FlashMessage.Status.FAILURE));

            return "redirect:/upload";
        }
        gifService.save(gif,file);

        // Include flash message for success
        redirectAttributes.addFlashAttribute("flash", new FlashMessage("GIF successfully uploaded!", FlashMessage.Status.SUCCESS));

        // TODO: Redirect browser to new GIF's detail view
        return String.format("redirect:/gifs/%s",gif.getId());
    }

    // Update an existing GIF
    @RequestMapping(value = "/gifs/{gifId}", method = RequestMethod.POST)
    public String updateGif(@Valid Gif gif, BindingResult result, @RequestParam MultipartFile file, RedirectAttributes redirectAttributes) {
        // TODO: Update GIF if data is valid
        if(result.hasErrors()){
            // TODO add multipartfile!!!
            System.out.println("\nHAS ERROR\n");
            // Include validation errors upon redirect
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.gif",result);
            // Spring is able to combine above and below
            // Add category if invalid data was received
            // flashAttribute survives exactly 1 redirection
            redirectAttributes.addFlashAttribute("gif",gif);
            redirectAttributes.addFlashAttribute("file",file);
            redirectAttributes.addFlashAttribute("flash",new FlashMessage("Gif could not be updated!", FlashMessage.Status.FAILURE));

            return "redirect:/gifs/{gifId}/edit";
        }
        gifService.save(gif,file);

        // Include flash message for success
        redirectAttributes.addFlashAttribute("flash", new FlashMessage("GIF successfully uploaded!", FlashMessage.Status.SUCCESS));

        // TODO: Redirect browser to updated GIF's detail view
        // отправит уже как GET ("это был POSt")
        return String.format("redirect:/gifs/%s",gif.getId());
    }

    // Form for uploading a new GIF
    @RequestMapping("/upload")
    public String formNewGif(Model model) {
        // TODO: Add model attributes needed for new GIF upload form
        // TODO: Look in CategoryController.java for explanation
        if(!model.containsAttribute("gif")){
            model.addAttribute("gif", new Gif());
        }
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("action", "/gifs");
        model.addAttribute("heading", "Upload");
        model.addAttribute("submit", "Add");

        return "gif/form";
    }

    // Form for editing an existing GIF
    @RequestMapping(value = "/gifs/{gifId}/edit")
    public String formEditGif(@PathVariable Long gifId, Model model) {
        // TODO: Add model attributes needed for edit form
        if(!model.containsAttribute("gif")){
            model.addAttribute("gif", gifService.findById(gifId));
        }
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("action", String.format("/gifs/%s", gifId));
        model.addAttribute("heading", "Edit GIF");
        model.addAttribute("submit", "Update");

        return "gif/form";
    }


    // Delete an existing GIF
    @RequestMapping(value = "/gifs/{gifId}/delete", method = RequestMethod.POST)
    public String deleteGif(@PathVariable Long gifId, RedirectAttributes redirectAttributes) {
        // TODO: Delete the GIF whose id is gifId
        Gif gif = gifService.findById(gifId);
        gifService.delete(gif);
        redirectAttributes.addFlashAttribute("flash", new FlashMessage("GIF deleted!",FlashMessage.Status.SUCCESS));
        // TODO: Redirect to app root
        return "redirect:/";
    }

    // Mark/unmark an existing GIF as a favorite
    @RequestMapping(value = "/gifs/{gifId}/favorite", method = RequestMethod.POST)
    public String toggleFavorite(@PathVariable Long gifId, HttpServletRequest request) {
        // HttpServletRequest allows to operate on http headers
        // TODO: With GIF whose id is gifId, toggle the favorite field
        Gif gif = gifService.findById(gifId);
        gifService.toggleFavorite(gif);
        // TODO: Redirect to GIF's detail view
        // request.getHeader("referer") allows to see won what url user was on when http request was made
        // i.e. allows to see where this request was made from
        // thus redirect back to the page, where it came from (be it details or index)
        /*  HOWEVER IT IS DANGEROUS check teacher notes for common web vulnerabilities */
        return String.format("redirect:%s", request.getHeader("referer"));
    }

    // Search results
    @RequestMapping("/search")
    public String searchResults(@RequestParam String q, Model model) {
        // TODO: Get list of GIFs whose description contains value specified by q
        System.out.println("GifController.searchResults");
        List<Gif> gifs = gifService.findAll();

        // FIXME move to service layer
        gifs = gifs.stream().filter( gif -> gif.getDescription().toLowerCase().contains(q.toLowerCase())).collect(Collectors.toList());

        model.addAttribute("gifs",gifs);
        return "gif/index";
    }
}