# Fibery Unofficial
Android companion application for [Fibery](https://fibery.io).<br>
Fibery is a work management platform that adapts to companies and grows with them.

# Features
- Application list
![](https://github.com/krossovochkin/FiberyUnofficial/tree/master/docs/app_list.jpg)
- Entity Types list
    - per application
- Entity List
    - per entity type
    - for particular entity (child entities)
- Entity Details
    - Entity name
    - Description (markdown)
    - Text fields
    - Number fields
    - Date fields
    - Single select fields
    - Parent entity fields (relation, non-collection)
    - Child entities fields (relation, collection)

# Missing Features
- Login
- Updating information (all screens are read-only)
- Views
    - Table
    - Board
    - Timeline
    - Whiteboard
    - Chart
    - etc.
- Extensions
    - Files
    - Assignments
    - Avatar
    - Comments
    - Workflow
    - Comments
    - etc.
- Filtering lists (stub filter can be added at compile time)
- Sorting lists (stub sorting can be added at compile time)
- Pagination

# Setup
Because of missing Login in order to use application it is needed to add your API token to the app at compile time.<br>
Inside:
```
app/src/main/java/by/krossovochkinfiberyunofficial/Secrets.kt
```
one need to add account name and Fibery API Token.
```
object Secrets {

    const val API_ACCOUNT = "abc"
    const val API_TOKEN = "xxx"
}
```
If your account url is `https://abc.fibery.io`, then you'll need to paste `"abc"`.<br>
How to get Fibery API Token one can find following [these instructions](https://api.fibery.io/?shell#getting-started)

# Development
[API](https://api.fibery.io/)

# License
Fibery ©<br>
Application: Apache 2.0
