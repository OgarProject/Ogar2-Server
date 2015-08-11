# Contributing
Contributions are appreciated in the form of pull requests. However, to maintain code readability and maintainability, some guidelines have been set. Although most of the below guidelines will automatically be applied by formatter-maven-plugin when you build the project, please do what you can to make sure your code follows the guidelines. *Your pull request will likely be rejected if it does not merge these guidelines, so please read them carefully.*

### Style
* Tabs should be replaced with 4 spaces.
* Unix-style line endings should be used (`\n`).
* Please leave a blank line at the end of each file.
* Conditional/loop statements (`if`, `for`, `while`, etc.) should always use braces, and the opening brace should be placed on the same line as the statement.
* There should be a space after a conditional/loop statement and before the condition, as well as a space after the condition and before the brace. Example:
  ```java
  // Good
  if (condition) {
      ...
  }
  
  // Bad
  if(condition) {
      ...
  }
  
  if(condition){
      ...
  }
  ```
* *Always build the project before making a pull request/contributing.* The formatter plugin will run and format most of the code properly, increasing the chances that your PR will be accepted.
