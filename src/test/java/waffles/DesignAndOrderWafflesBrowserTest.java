package waffles;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class DesignAndOrderWafflesBrowserTest {
  
  private static HtmlUnitDriver browser;
  
  @LocalServerPort
  private int port;
  
  @Autowired
  TestRestTemplate rest;
  
  @BeforeClass
  public static void setup() {
    browser = new HtmlUnitDriver();
    browser.manage().timeouts()
        .implicitlyWait(10, TimeUnit.SECONDS);
  }
  
  @AfterClass
  public static void closeBrowser() {
    browser.quit();
  }
  
  @Test
  public void testDesignAWafflePage_HappyPath() throws Exception {
    browser.get(homePageUrl());
    clickDesignAWaffle();
    assertDesignPageElements();
    buildAndSubmitAWaffle("Basic Waffle", "REG", "CRM", "HUND", "STRW", "CRML");
    clickBuildAnotherWaffle();
    buildAndSubmitAWaffle("Another Waffle", "BEL", "ICRM", "VERM", "BNN", "HNY");
    fillInAndSubmitOrderForm();
    assertEquals(homePageUrl(), browser.getCurrentUrl());
  }
  
  @Test
  public void testDesignAWafflePage_EmptyOrderInfo() throws Exception {
    browser.get(homePageUrl());
    clickDesignAWaffle();
    assertDesignPageElements();
    buildAndSubmitAWaffle("Basic Waffle", "REG", "CRM", "HUND", "STRW", "CRML");
    submitEmptyOrderForm();
    fillInAndSubmitOrderForm();
    assertEquals(homePageUrl(), browser.getCurrentUrl());
  }

  @Test
  public void testDesignAWafflePage_InvalidOrderInfo() throws Exception {
    browser.get(homePageUrl());
    clickDesignAWaffle();
    assertDesignPageElements();
    buildAndSubmitAWaffle("Basic Waffle", "REG", "CRM", "HUND", "STRW", "CRML");
    submitInvalidOrderForm();
    fillInAndSubmitOrderForm();
    assertEquals(homePageUrl(), browser.getCurrentUrl());
  }

  //
  // Browser test action methods
  //
  private void buildAndSubmitAWaffle(String name, String... ingredients) {
    assertDesignPageElements();

    for (String ingredient : ingredients) {
      browser.findElementByCssSelector("input[value='" + ingredient + "']").click();      
    }
    browser.findElementByCssSelector("input#name").sendKeys(name);
    browser.findElementByCssSelector("form").submit();
  }

  private void assertDesignPageElements() {
    assertEquals(designPageUrl(), browser.getCurrentUrl());
    List<WebElement> ingredientGroups = browser.findElementsByClassName("ingredient-group");
    assertEquals(5, ingredientGroups.size());
    
    WebElement wrapGroup = browser.findElementByCssSelector("div.ingredient-group#wraps");
    List<WebElement> wraps = wrapGroup.findElements(By.tagName("div"));
    assertEquals(2, wraps.size());
    assertIngredient(wrapGroup, 0, "REG", "Regular Waffle");
    assertIngredient(wrapGroup, 1, "BEL", "Belgium Waffle");
    
    WebElement proteinGroup = browser.findElementByCssSelector("div.ingredient-group#proteins");
    List<WebElement> proteins = proteinGroup.findElements(By.tagName("div"));
    assertEquals(2, proteins.size());
    assertIngredient(proteinGroup, 0, "CRM", "Cream");
    assertIngredient(proteinGroup, 1, "ICRM", "Ice Cream");

    WebElement cheeseGroup = browser.findElementByCssSelector("div.ingredient-group#cheeses");
    List<WebElement> cheeses = proteinGroup.findElements(By.tagName("div"));
    assertEquals(2, cheeses.size());
    assertIngredient(cheeseGroup, 0, "HUND", "100/1000s");
    assertIngredient(cheeseGroup, 1, "VERM", "Vermicielli");

    WebElement veggieGroup = browser.findElementByCssSelector("div.ingredient-group#veggies");
    List<WebElement> veggies = proteinGroup.findElements(By.tagName("div"));
    assertEquals(2, veggies.size());
    assertIngredient(veggieGroup, 0, "STRW", "Strawberries");
    assertIngredient(veggieGroup, 1, "BNN", "Banana");

    WebElement sauceGroup = browser.findElementByCssSelector("div.ingredient-group#sauces");
    List<WebElement> sauces = proteinGroup.findElements(By.tagName("div"));
    assertEquals(2, sauces.size());
    assertIngredient(sauceGroup, 0, "CRML", "Caramel");
    assertIngredient(sauceGroup, 1, "HNY", "Honey");
  }
  

  private void fillInAndSubmitOrderForm() {
    assertTrue(browser.getCurrentUrl().startsWith(orderDetailsPageUrl()));
    fillField("input#deliveryName", "Ima Hungry");
    fillField("input#deliveryStreet", "1234 Culinary Blvd.");
    fillField("input#deliveryCity", "Foodsville");
    fillField("input#deliveryState", "CO");
    fillField("input#deliveryZip", "81019");
    fillField("input#ccNumber", "4111111111111111");
    fillField("input#ccExpiration", "10/19");
    fillField("input#ccCVV", "123");
    browser.findElementByCssSelector("form").submit();
  }

  private void submitEmptyOrderForm() {
    assertEquals(currentOrderDetailsPageUrl(), browser.getCurrentUrl());
    browser.findElementByCssSelector("form").submit();
    
    assertEquals(orderDetailsPageUrl(), browser.getCurrentUrl());

    List<String> validationErrors = getValidationErrorTexts();
    assertEquals(9, validationErrors.size());
    assertTrue(validationErrors.contains("Please correct the problems below and resubmit."));
    assertTrue(validationErrors.contains("Delivery name is required"));
    assertTrue(validationErrors.contains("Street is required"));
    assertTrue(validationErrors.contains("City is required"));
    assertTrue(validationErrors.contains("State is required"));
    assertTrue(validationErrors.contains("Zip code is required"));
    assertTrue(validationErrors.contains("Not a valid credit card number"));
    assertTrue(validationErrors.contains("Must be formatted MM/YY"));
    assertTrue(validationErrors.contains("Invalid CVV"));    
  }

  private List<String> getValidationErrorTexts() {
    List<WebElement> validationErrorElements = browser.findElementsByClassName("validationError");
    List<String> validationErrors = validationErrorElements.stream()
        .map(el -> el.getText())
        .collect(Collectors.toList());
    return validationErrors;
  }

  private void submitInvalidOrderForm() {
    assertTrue(browser.getCurrentUrl().startsWith(orderDetailsPageUrl()));
    fillField("input#deliveryName", "I");
    fillField("input#deliveryStreet", "1");
    fillField("input#deliveryCity", "F");
    fillField("input#deliveryState", "C");
    fillField("input#deliveryZip", "8");
    fillField("input#ccNumber", "1234432112344322");
    fillField("input#ccExpiration", "14/91");
    fillField("input#ccCVV", "1234");
    browser.findElementByCssSelector("form").submit();
    
    assertEquals(orderDetailsPageUrl(), browser.getCurrentUrl());

    List<String> validationErrors = getValidationErrorTexts();
    assertEquals(4, validationErrors.size());
    assertTrue(validationErrors.contains("Please correct the problems below and resubmit."));
    assertTrue(validationErrors.contains("Not a valid credit card number"));
    assertTrue(validationErrors.contains("Must be formatted MM/YY"));
    assertTrue(validationErrors.contains("Invalid CVV"));    
  }

  private void fillField(String fieldName, String value) {
    WebElement field = browser.findElementByCssSelector(fieldName);
    field.clear();
    field.sendKeys(value);
  }
  
  private void assertIngredient(WebElement ingredientGroup, 
                                int ingredientIdx, String id, String name) {
    List<WebElement> proteins = ingredientGroup.findElements(By.tagName("div"));
    WebElement ingredient = proteins.get(ingredientIdx);
    assertEquals(id, 
        ingredient.findElement(By.tagName("input")).getAttribute("value"));
    assertEquals(name, 
        ingredient.findElement(By.tagName("span")).getText());
  }

  private void clickDesignAWaffle() {
    assertEquals(homePageUrl(), browser.getCurrentUrl());
    browser.findElementByCssSelector("a[id='design']").click();
  }

  private void clickBuildAnotherWaffle() {
    assertTrue(browser.getCurrentUrl().startsWith(orderDetailsPageUrl()));
    browser.findElementByCssSelector("a[id='another']").click();
  }

 
  //
  // URL helper methods
  //
  private String designPageUrl() {
    return homePageUrl() + "design";
  }

  private String homePageUrl() {
    return "http://localhost:" + port + "/";
  }

  private String orderDetailsPageUrl() {
    return homePageUrl() + "orders";
  }

  private String currentOrderDetailsPageUrl() {
    return homePageUrl() + "orders/current";
  }

}
