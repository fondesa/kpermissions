Contributing
======
Considering that this library is actively maintained, contributions of all types are welcome.

Asking Questions
-------
Before asking any question, read the [wiki](https://github.com/Fondesa/KPermissions/wiki).
It may contains the answer to your questions.
If not, or if the wiki isn't clear, open a new issue. The issue will be labeled with _question_.


Opening issues
-------
Open a new issue when:
- you notice an unwanted behavior
- you want a new feature implemented
- you have just some doubts 

To open a new issue, please use the provided issue template and fill it out as much as possible.
If you are interested to an existing issue, feel free to comment the issue or subscribe to it.


Submitting pull requests
-------
If you want to fix a bug or implement a new feature, feel free to submit a new pull request.
To submit a pull request, you have to fork this repository and fill the PR template.
When you want to submit a pull request, remember to:
- follow this library's code style
- run `./gradlew clean build`
- write tests for each new public API
- write the **KDoc** on each API
- write inline comments for features that aren't so clear

### Code style
The first rule is to write everything in **Kotlin** (for sources) and **Groovy** (for plugins).

For **Kotlin** files follow the official [coding conventions](https://kotlinlang.org/docs/reference/coding-conventions.html).
There are also some others conventions in this project to follow when writing **Kotlin** files.

#### Root classes / interfaces / objects
Don't write more than one root class/interface/object in the same file.

Nested and inner classes/interfaces/objects are allowed.

The file's name must be equal to the name of the root class/interface/object.

#### Extensions
All the public extensions are contained under the package `$.extension`.

The name of the file that contains a group of public extensions must follow the 
pattern `{x}Extensions.kt` where `x` is the name of the class the extensions are added to.

#### Type aliases
All the public type aliases are contained under the package `$.alias`.

The name of the file that contains a group of public aliases must follow the 
pattern `{x}Aliases.kt` where `x` is the name of the class/functionality the aliases are related to.

#### Tests
All the tests files must follow the pattern `{package}.{x}Test` where `package` is the package
of the class/object that is tested and `x` is the name of the class/object or the name of the 
extensions' file that is tested.

Avoid to write the annotation `@Test` inlined to the function's declaration.

The only style allowed is lower camel case without underscores or backticks.

_Example_
```kotlin
@Test
fun thisIsValid() {
    assertTrue(true)
}
```

#### Imports
The static imports are allowed only in tests.

Leave a blank line between the imports and the documentation.