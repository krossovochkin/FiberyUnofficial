<img src="https://github.com/krossovochkin/FiberyUnofficial/blob/master/docs/app_icon.png?raw=true" width="50"/>

![](https://github.com/krossovochkin/FiberyUnofficial/workflows/.github/workflows/main.yml/badge.svg)

# Fibery Unofficial
Android companion application for [Fibery](https://fibery.io).<br>
Fibery is a work management platform that adapts to companies and grows with them.

## Features
### Application List
<img src="https://github.com/krossovochkin/FiberyUnofficial/blob/master/docs/app_list.jpg?raw=true" width="200"/>

### Entity Type List
<img src="https://github.com/krossovochkin/FiberyUnofficial/blob/master/docs/entity_type_list.jpg?raw=true" width="200"/>

 - per application
 
 ### Entity List
<img src="https://github.com/krossovochkin/FiberyUnofficial/blob/master/docs/entity_list.jpg?raw=true" width="200"/>

  - per entity type
  - for particular entity (child entities)
    
### Entity Details
<img src="https://github.com/krossovochkin/FiberyUnofficial/blob/master/docs/entity_details.jpg?raw=true" width="200"/>

  - Entity name
  - Description (markdown)
  - Text fields
  - Number fields
  - Date fields
  - Single select fields
  - Parent entity fields (relation, non-collection)
  - Child entities fields (relation, collection)

## Missing Features
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

## Setup
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

## Building application
To build the application (and get .apk file) one need to install [Android Studio](https://developer.android.com/studio).
Open the project and run it.

## Development
[Fibery API](https://api.fibery.io/)

# License
Fibery ©<br>
Application: Apache 2.0
