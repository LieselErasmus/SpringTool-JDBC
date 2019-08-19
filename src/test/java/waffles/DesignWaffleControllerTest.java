package waffles;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import waffles.Ingredient.Type;
import waffles.data.IngredientRepository;
import waffles.data.OrderRepository;
import waffles.data.WaffleRepository;
import waffles.web.DesignWaffleController;

@RunWith(SpringRunner.class)
@WebMvcTest(DesignWaffleController.class)
public class DesignWaffleControllerTest {

  @Autowired
  private MockMvc mockMvc;

  private List<Ingredient> ingredients;

  private Waffle design;

  @MockBean
  private IngredientRepository ingredientRepository;

  @MockBean
  private WaffleRepository designRepository;

  @MockBean
  private OrderRepository orderRepository;

  @Before
  public void setup() {
    ingredients = Arrays.asList(
      new Ingredient("REG", "Regular Waffle", Type.WRAP),
      new Ingredient("BEL", "Belgium Waffle", Type.WRAP),
      new Ingredient("CRM", "Cream", Type.PROTEIN),
      new Ingredient("ICRM", "Ice Cream", Type.PROTEIN),
      new Ingredient("STRW", "Strawberries", Type.VEGGIES),
      new Ingredient("BNN", "Banana", Type.VEGGIES),
      new Ingredient("HUND", "100/1000s", Type.CHEESE),
      new Ingredient("VERM", "Vermicielli", Type.CHEESE),
      new Ingredient("CRML", "Caramel", Type.SAUCE),
      new Ingredient("HNY", "Honey", Type.SAUCE)
    );

    when(ingredientRepository.findAll())
        .thenReturn(ingredients);

    when(ingredientRepository.findById("REG")).thenReturn(new Ingredient("REG", "Regular Waffle", Type.WRAP));
    when(ingredientRepository.findById("CRM")).thenReturn(new Ingredient("CRM", "Cream", Type.PROTEIN));
    when(ingredientRepository.findById("HUND")).thenReturn(new Ingredient("HUND", "100/1000s", Type.CHEESE));

    design = new Waffle();
    design.setName("Test Waffle");

    design.setIngredients(
        Arrays.asList(
            new Ingredient("REG", "Regular Waffle", Type.WRAP),
            new Ingredient("CRM", "Cream", Type.PROTEIN),
            new Ingredient("HUND", "100/1000s", Type.CHEESE)));

  }

  @Test
  public void testShowDesignForm() throws Exception {
    mockMvc.perform(get("/design"))
        .andExpect(status().isOk())
        .andExpect(view().name("design"))
        .andExpect(model().attribute("wrap", ingredients.subList(0, 2)))
        .andExpect(model().attribute("protein", ingredients.subList(2, 4)))
        .andExpect(model().attribute("veggies", ingredients.subList(4, 6)))
        .andExpect(model().attribute("cheese", ingredients.subList(6, 8)))
        .andExpect(model().attribute("sauce", ingredients.subList(8, 10)));
  }

  @Test
  public void processDesign() throws Exception {
    when(designRepository.save(design))
        .thenReturn(design);

    mockMvc.perform(post("/design")
        .content("name=Test+Waffle&ingredients=REG,CRM,HUND")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
        .andExpect(status().is3xxRedirection())
        .andExpect(header().stringValues("Location", "/orders/current"));
  }

}
