package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;


//add new MenuController Class
@Controller
@RequestMapping("menu")
public class MenuController {

//Declare instances of dao
    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

//write index handler
    @RequestMapping(value="")
    public String index(Model model) {

        model.addAttribute("title", "Menus");
        model.addAttribute("menus", menuDao.findAll());

        return "menu/index";
    }

//Displays Add Menu
    @RequestMapping(value="add", method= RequestMethod.GET)
    public String AddAMenu(Model model) {

        model.addAttribute(new Menu());
        model.addAttribute("title", "Add Menu");
        model.addAttribute("menus", menuDao.findAll());

        return "menu/add";

    }

    //Process Add Menu
    @RequestMapping(value="add", method=RequestMethod.POST)
    public String processAddMenu(Model model, @ModelAttribute @Valid Menu menu, Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(menu);
        return "redirect:view/" + menu.getId();
    }

    //the view menu
    @RequestMapping(value="view/{id}", method=RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable int id) {
        Menu menu = menuDao.findOne(id);
        model.addAttribute("title", menu.getName());
        model.addAttribute("menu", menu);

        return "menu/view";
    }

//add and render menu items
    @RequestMapping(value="add-item/{id}", method=RequestMethod.GET)
    public String addItem(Model model, @PathVariable int id) {
        Menu menu = menuDao.findOne(id);
        model.addAttribute("title", "Add Item to Menu: " + menu.getName());
        AddMenuItemForm form = new AddMenuItemForm(menu, cheeseDao.findAll());
        model.addAttribute("form", form);
        return "menu/add-item";
    }

//Process the form
    @RequestMapping(value="add-item/{id}", method=RequestMethod.POST)
    public String addItem(Model model, @Valid AddMenuItemForm form, @PathVariable int id,
                          @RequestParam int cheeseId, Errors errors) {

        if (errors.hasErrors()) {
            return "menu/add-item";
        }

        Menu menu = menuDao.findOne(id);
        Cheese cheese = cheeseDao.findOne(cheeseId);

        menu.addItem(cheese);
        menuDao.save(menu);
        return "redirect:../view/" + menu.getId();
    }


}
