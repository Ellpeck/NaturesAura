# Editing Nature's Aura recipes with CraftTweaker
A few notes that apply for most of the recipe types:
- If you don't know how the CraftTweaker syntax works, [read up on it](https://docs.blamejared.com/en/#Getting_Started/) first.
- `name` is the name of a recipe. It mostly doesn't matter, however there are some names that are important for the Book of Natural Aura. The names of the recipes which are important can be found in the [raw patchouli documentation](https://github.com/Ellpeck/NaturesAura/tree/master/src/main/resources/assets/naturesaura/patchouli_books/book/en_us/entries) which should be easy enough to read through even without knowledge of its exact structure.
- `IIngredient` is any kind of ingredient, meaning either an OreDictionary entry or an item
- `IItemStack` is an item
- `aura` is the amount of Aura required and represents the total amount required for the completion of the recipe (for reference, 1,000,000 is the default amount of Aura present in the world and 2,000,000 is the amount that is required for the Environmental Eye's bar to fill up fully)
- `time` is the time processes take in ticks
- For most removal recipes, `output` is the output of the recipe that should be removed. All recipes with the given outupt will be removed.


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