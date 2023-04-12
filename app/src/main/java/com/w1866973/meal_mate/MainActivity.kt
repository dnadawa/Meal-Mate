package com.w1866973.meal_mate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.room.Room
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    private lateinit var appDatabase: AppDatabase
    private lateinit var mealDao: MealDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appDatabase = Room.databaseBuilder(this, AppDatabase::class.java, "meal_mate_db").build()
        mealDao = appDatabase.mealDao()
    }

    fun onAddMealsButtonClicked(view: View) {
        runBlocking {
            launch {
                try {
                    val meal1 = Meal(
                        id = "1",
                        meal = "Sweet and Sour Pork",
                        drinkAlternate = null,
                        category = "Pork",
                        area = "Chinese",
                        instructions = "Preparation\r\n" +
                                "1. Crack the egg into a bowl. Separate the egg white and yolk.\r\n\r\n" +
                                "Sweet and Sour Pork\r\n" +
                                "2. Slice the pork tenderloin into ips.\r\n\r\n" +
                                "3. Prepare the marinade using a pinch of salt, one teaspoon of starch, two teaspoons of light soy sauce, and an egg white.\r\n\r\n" +
                                "4. Marinade the pork ips for about 20 minutes.\r\n\r\n" +
                                "5. Put the remaining starch in a bowl. Add some water and vinegar to make a starchy sauce.\r\n\r\n" +
                                "Sweet and Sour Pork\r\nCooking Inuctions\r\n" +
                                "1. Pour the cooking oil into a wok and heat to 190\u00b0C (375\u00b0F)." +
                                " Add the marinated pork ips and fry them until they turn brown. " +
                                "Remove the cooked pork from the wok and place on a plate.\r\n\r\n" +
                                "2. Leave some oil in the wok. Put the tomato sauce and white sugar into the wok, and heat until the oil and sauce are fully combined.\r\n\r\n" +
                                "3. Add some water to the wok and thoroughly heat the sweet and sour sauce before adding the pork ips to it.\r\n\r\n" +
                                "4. Pour in the starchy sauce. Stir-fry all the ingredients until the pork and sauce are thoroughly mixed together.\r\n\r\n" +
                                "5. Serve on a plate and add some coriander for decoration.",
                        mealThumb = "https://www.themealdb.com/images/media/meals/1529442316.jpg",
                        tags = "Sweet",
                        youtube = "https://www.youtube.com/watch?v=mdaBIhgEAMo",
                        ingredients = arrayListOf(
                            Ingredient("Pork", "200g"),
                            Ingredient("Egg", "1"),
                            Ingredient("Water", "Dash"),
                            Ingredient("Salt", "1/2 tsp"),
                            Ingredient("Sugar", "1 tsp "),
                            Ingredient("Soy Sauce", "10g"),
                            Ingredient("Starch", "10g"),
                            Ingredient("Tomato Puree", "30g"),
                            Ingredient("Vinegar", "10g"),
                            Ingredient("Coriander", "Dash")
                        ),
                        source = null,
                        imageSource = null,
                        creativeCommonsConfirmed = null,
                        dateModified = null
                    )
                    val meal2 = Meal(
                        id = "2",
                        meal = "Chicken Marengo",
                        drinkAlternate = null,
                        category = "Chicken",
                        area = "French",
                        instructions = "Heat the oil in a large flameproof casserole dish and stir-fry the mushrooms until they start to soften." +
                                " Add the chicken legs and cook briefly on each side to colour them a little.\r\n" +
                                "Pour in the passata, crumble in the stock cube and stir in the olives. Season with black pepper \u2013 you shouldn\u2019t need salt." +
                                " Cover and simmer for 40 mins until the chicken is tender." +
                                " Sprinkle with parsley and serve with pasta and a salad, or mash and green veg, if you like.",
                        mealThumb = "https://www.themealdb.com/images/media/meals/qpxvuq1511798906.jpg",
                        tags = null,
                        youtube = "https://www.youtube.com/watch?v=U33HYUr-0Fw",
                        ingredients = arrayListOf(
                            Ingredient("Olive Oil", "1 tbs"),
                            Ingredient("Mushrooms", "300g"),
                            Ingredient("Chicken Legs", "4"),
                            Ingredient("Passata", "500g"),
                            Ingredient("Chicken Stock Cube", "1"),
                            Ingredient("Black Olives", "100g"),
                            Ingredient("Parsley", "Chopped")
                        ),
                        source = "https://www.bbcgoodfood.com/recipes/3146682/chicken-marengo",
                        imageSource = null,
                        creativeCommonsConfirmed = null,
                        dateModified = null
                    )

                    val meal3 = Meal(
                        id = "3",
                        meal = "Beef Banh Mi Bowls with Sriracha Mayo, Carrot & Pickled Cucumber",
                        drinkAlternate = null,
                        category = "Beef",
                        area = "Vietnamese",
                        instructions = "Add'l ingredients: mayonnaise, siracha\r\n\r\n1\r\n\r\n" +
                                "Place rice in a fine-mesh sieve and rinse until water runs clear." +
                                " Add to a small pot with 1 cup water (2 cups for 4 servings) and a pinch of salt." +
                                " Bring to a boil, then cover and reduce heat to low. Cook until rice is tender, 15 minutes." +
                                " Keep covered off heat for at least 10 minutes or until ready to serve." +
                                "\r\n\r\n2\r\n\r\nMeanwhile, wash and dry all produce. Peel and finely chop garlic." +
                                " Zest and quarter lime (for 4 servings, zest 1 lime and quarter both). Trim and halve cucumber lengthwise; thinly slice crosswise into half-moons." +
                                " Halve, peel, and medium dice onion. Trim, peel, and grate carrot.\r\n\r\n3\r\n\r\nIn a medium bowl," +
                                " combine cucumber, juice from half the lime, \u00bc tsp sugar (\u00bd tsp for 4 servings), and a pinch of salt." +
                                " In a small bowl, combine mayonnaise, a pinch of garlic, a squeeze of lime juice, and as much sriracha as you\u2019d like." +
                                " Season with salt and pepper.\r\n\r\n4\r\n\r\nHeat a drizzle of oil in a large pan over medium-high heat." +
                                " Add onion and cook, stirring, until softened, 4-5 minutes. Add beef, remaining garlic, and 2 tsp sugar (4 tsp for 4 servings)." +
                                " Cook, breaking up meat into pieces, until beef is browned and cooked through, 4-5 minutes." +
                                " Stir in soy sauce. Turn off heat; taste and season with salt and pepper.\r\n\r\n5\r\n\r\nFluff rice with a fork; stir in lime zest and 1 TBSP butter." +
                                " Divide rice between bowls. Arrange beef, grated carrot, and pickled cucumber on top. Top with a squeeze of lime juice. Drizzle with sriracha mayo.",
                        mealThumb = "https://www.themealdb.com/images/media/meals/z0ageb1583189517.jpg",
                        tags = null,
                        youtube = "",
                        ingredients = arrayListOf(
                            Ingredient(name = "Rice", measure = "White"),
                            Ingredient(name = "Onion", measure = "1"),
                            Ingredient(name = "Lime", measure = "1"),
                            Ingredient(name = "Garlic Clove", measure = "3"),
                            Ingredient(name = "Cucumber", measure = "1"),
                            Ingredient(name = "Carrots", measure = "3 oz "),
                            Ingredient(name = "Ground Beef", measure = "1 lb"),
                            Ingredient(name = "Soy Sauce", measure = "2 oz "),
                        ),
                        source = "",
                        imageSource = null,
                        creativeCommonsConfirmed = null,
                        dateModified = null,
                    )

                    val meal4 = Meal(
                        id = "4",
                        meal = "Leblebi Soup",
                        drinkAlternate = null,
                        category = "Vegetarian",
                        area = "Tunisian",
                        instructions = "Heat the oil in a large pot. Add the onion and cook until translucent." +
                                "\r\nDrain the soaked chickpeas and add them to the pot together with the vegetable stock." +
                                " Bring to the boil, then reduce the heat and cover. Simmer for 30 minutes.\r\n" +
                                "In the meantime toast the cumin in a small ungreased frying pan, then grind them in a mortar." +
                                " Add the garlic and salt and pound to a fine paste.\r\n" +
                                "Add the paste and the harissa to the soup and simmer until the chickpeas are tender, about 30 minutes." +
                                "\r\nSeason to taste with salt, pepper and lemon juice and serve hot.",
                        mealThumb = "https://www.themealdb.com/images/media/meals/x2fw9e1560460636.jpg",
                        tags = "Soup",
                        youtube = "https://www.youtube.com/watch?v=BgRifcCwinY",
                        ingredients = arrayListOf(
                            Ingredient("Olive Oil", "2 tbs"),
                            Ingredient("Onion", "1 medium finely diced"),
                            Ingredient("Chickpeas", "250g"),
                            Ingredient("Vegetable Stock", "1.5L"),
                            Ingredient("Cumin", "1 tsp"),
                            Ingredient("Garlic", "5 cloves"),
                            Ingredient("Salt", "1/2 tsp"),
                            Ingredient("Harissa Spice", "1 tsp"),
                            Ingredient("Pepper", "Pinch"),
                            Ingredient("Lime", "1/2")
                        ),
                        source = "http://allrecipes.co.uk/recipe/43419/leblebi--tunisian-chickpea-soup-.aspx",
                        imageSource = null,
                        creativeCommonsConfirmed = null,
                        dateModified = null
                    )

                    mealDao.insertMeals(meal1, meal2, meal3, meal4)

                    Toast.makeText(
                        applicationContext,
                        "Meals successfully added!",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (exception: Exception) {
                    println(exception)
                    Toast.makeText(applicationContext, "Something went wrong!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}