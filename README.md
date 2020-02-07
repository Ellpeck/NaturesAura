# NaturesAura
Nature's Aura is a mod about collecting, using and replenishing the Aura naturally present in the world to create useful devices and unique mechanics.

## Maven
To add this project as a dependency (for using the [API](https://github.com/Ellpeck/NaturesAura/tree/master/src/main/java/de/ellpeck/naturesaura/api), for example), put this into your `build.gradle` file:
```
repositories {
    maven {
        url 'https://pkgs.dev.azure.com/Ellpeck/Public/_packaging/NaturesAura/maven/v1'
    }
}

dependencies {
    compile "de.ellpeck.naturesaura:NaturesAura:VERSION"
}
```
Replace VERSION with the version you want to use. You can find a list of versions by going to the [maven artifact](https://dev.azure.com/Ellpeck/NaturesAura/_packaging).