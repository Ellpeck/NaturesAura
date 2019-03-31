# Editing Nature's Aura recipes with CraftTweaker
Note that both [CraftTweaker](https://minecraft.curseforge.com/projects/crafttweaker) and [MTLib](https://minecraft.curseforge.com/projects/mtlib) are required for this compatibility.

A few notes that apply for most of the recipe types:
- If you don't know how the CraftTweaker syntax works, [read up on it](https://docs.blamejared.com/en/#Getting_Started/) first.
- `name` is the name of a recipe. Read on for more information about this.
- `aura` is the amount of Aura required and represents the total amount required for the completion of the recipe (for reference, 1,000,000 is the default amount of Aura present in the world and 2,000,000 is the amount that is required for the Environmental Eye's bar to fill up fully)
- `time` is the time processes take in ticks
- For most removal recipes, `output` is the output of the recipe that should be removed. All recipes with the given outupt will be removed.

## On the Importance of Recipe Names
When replacing an existing recipe with a new one, the `name` variable of the recipe matters greatly, both for Nature's Aura's custom recipe types and for [vanilla crafting recipes](https://crafttweaker.readthedocs.io/en/latest/#Vanilla/Recipes/Crafting/Recipes_Crafting_Table/), if the replacement recipe should be displayed correctly in the Book of Natural Aura in place of the original recipe.  
__The replacement recipe that is added for any given item inside of Nature's Aura needs to be named after the item id of the item that is being crafted.__ 

As an example, the following piece of code will remove the existing recipe of the Imperceptible Builder and replace it with a new one. Checking its Book of Natural Aura entry will then also display the new recipe correctly without errors.  
```
recipes.remove(<naturesaura:placer>);
recipes.addShapeless("placer", <naturesaura:placer>, [<naturesaura:infused_iron>, <minecraft:piston>]);
```
Note that the name of the recipe is supplied as `placer` because the item id of the Imperceptible Builder is `naturesaura:placer`. Not doing this would lead to the Book of Natural Aura not displaying the new recipe.

_When adding a new recipe without replacing an existing one, the name of the newly added recipe does not matter._

## Natural Altar
`mods.naturesaura.Altar.addRecipe(String name, IIngredient input, IItemStack output, IIngredient catalyst, int aura, int time)`

- `catalyst` is the catalyst block that is placed on one of the four corner blocks, can be `null`

`mods.naturesaura.Altar.removeRecipe(IItemStack output)`

## Altar of Birthing
`mods.naturesaura.AnimalSpawner.addRecipe(String name, String entity, int aura, int time, IIngredient[] ingredients)`
- `entity` is the registry name of the entity that you want to spawn

`mods.naturesaura.AnimalSpawner.removeRecipe(String name)`
- `entity` is the registry name of the entity whose spawning recipe should be removed

## Offering to the Gods
`mods.naturesaura.Offering.addRecipe(String name, IIngredient input, int inputAmount, IIngredient startItem, IItemStack output)`
- `inputAmount` is the amount of items required for the input. Note that this means that the amount of the `input` variable is ignored
- `startItem` is the item required to start the offering, should pretty much always be `naturesaura:calling_spirit`

`mods.naturesaura.Offering.removeRecipe(IItemStack output)`

## Ritual of the Forest
`mods.naturesaura.TreeRitual.addRecipe(String name, IIngredient saplingType, IItemStack result, int time, IIngredient[] items)`
- `saplingType` is an item representation of the sapling that needs to be placed and grown into a tree
- `items` are the items that need to be placed on the wooden stands

`mods.naturesaura.TreeRitual.removeRecipe(IItemStack output)`