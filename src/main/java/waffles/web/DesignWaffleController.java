package waffles.web;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import waffles.Waffle;
import waffles.Ingredient;
import waffles.Ingredient.Type;
import waffles.Order;
import waffles.data.WaffleRepository;
import waffles.data.IngredientRepository;

// tag::classShell[]
@Controller
@RequestMapping("/design")
@SessionAttributes("order")
public class DesignWaffleController {
  
//end::classShell[]

//tag::bothRepoProperties[]
//tag::ingredientRepoProperty[]
  private final IngredientRepository ingredientRepo;
  
//end::ingredientRepoProperty[]
  private WaffleRepository designRepo;

//end::bothRepoProperties[]
  
  /*
// tag::ingredientRepoOnlyCtor[]
  @Autowired
  public DesignWaffleController(IngredientRepository ingredientRepo) {
    this.ingredientRepo = ingredientRepo;
  }
// end::ingredientRepoOnlyCtor[]
   */

  //tag::bothRepoCtor[]
  @Autowired
  public DesignWaffleController(
        IngredientRepository ingredientRepo, 
        WaffleRepository designRepo) {
    this.ingredientRepo = ingredientRepo;
    this.designRepo = designRepo;
  }

  //end::bothRepoCtor[]
  
  // tag::modelAttributes[]
  @ModelAttribute(name = "order")
  public Order order() {
    return new Order();
  }
  
  @ModelAttribute(name = "waffle")
  public Waffle waffle() {
    return new Waffle();
  }

  // end::modelAttributes[]
  // tag::showDesignForm[]
  
  @GetMapping
  public String showDesignForm(Model model) {
    List<Ingredient> ingredients = new ArrayList<>();
    ingredientRepo.findAll().forEach(i -> ingredients.add(i));
    
    Type[] types = Ingredient.Type.values();
    for (Type type : types) {
      model.addAttribute(type.toString().toLowerCase(), 
          filterByType(ingredients, type));      
    }

    return "design";
  }
//end::showDesignForm[]

  //tag::processDesign[]
  @PostMapping
  public String processDesign(
      @Valid Waffle design, Errors errors, 
      @ModelAttribute Order order) {

    if (errors.hasErrors()) {
      return "design";
    }

    Waffle saved = designRepo.save(design);
    order.addDesign(saved);

    return "redirect:/orders/current";
  }
  //end::processDesign[]
  
  private List<Ingredient> filterByType(
      List<Ingredient> ingredients, Type type) {
    return ingredients
              .stream()
              .filter(x -> x.getType().equals(type))
              .collect(Collectors.toList());
  }

  /*
//tag::classShell[]

  ...

//end::classShell[]
   */
//tag::classShell[]

}
//end::classShell[]
