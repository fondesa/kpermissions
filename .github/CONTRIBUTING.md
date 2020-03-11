Contributing
======
Considering that this library is actively maintained, contributions of all types are welcome.

Asking Questions
-------
Before asking any question, read the [wiki](https://github.com/Fondesa/RecyclerViewDivider/wiki).
It may contains the answer to your questions.
If not, or if the wiki isn't clear, open a new issue. The issue will be labeled with _question_.


Opening issues
-------
Open a new issue when:
- you notice an unwanted behavior
- you want a new feature implemented
- you have just some doubts 

To open a new issue, please use the provided issue templates and fill them out as much as possible.
If you are interested to an existing issue, feel free to comment the issue or subscribe to it.


Submitting pull requests
-------
If you want to fix a bug or implement a new feature, feel free to submit a new pull request.
To submit a pull request, you have to fork this repository and fill the PR template.
When you want to submit a pull request, remember to:
- follow this library's code style
- write tests for each new public API
- write the **KDoc** on each not-private API
- write inline comments for features that aren't so clear

You may want to run `./gradlew check` which executes the same lint checks of the CI before opening a new PR. 

### Code style
The first rule is to write everything in **Kotlin** (for sources).
For **Kotlin** files follow the official [coding conventions](https://kotlinlang.org/docs/reference/coding-conventions.html).

#### Imports
The static imports are allowed only in tests.
