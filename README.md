<a href="https://github.com/krossovochkin/FiberyUnofficial/actions?query=workflow%3Apipeline" target="_blank"><img src="https://github.com/krossovochkin/FiberyUnofficial/blob/master/docs/app_icon.png?raw=true" width="50"/> ![](https://github.com/krossovochkin/FiberyUnofficial/workflows/pipeline/badge.svg)</a> <a href="https://github.com/krossovochkin/FiberyUnofficial/releases/tag/v0.13.0" target="_blank">![](https://img.shields.io/badge/version-0.13.0-yellow)</a>

# Fibery Unofficial
Android companion application for [Fibery](https://fibery.io).<br>
Fibery is a work management platform that adapts to companies and grows with them.

## Features
### Login
<img src="https://github.com/krossovochkin/FiberyUnofficial/blob/master/docs/login.png?raw=true" width="200"/>

### Application List
<img src="https://github.com/krossovochkin/FiberyUnofficial/blob/master/docs/app_list.jpg?raw=true" width="200"/>

### Entity Type List
<img src="https://github.com/krossovochkin/FiberyUnofficial/blob/master/docs/entity_type_list.jpg?raw=true" width="200"/>

 - per application
 
 ### Entity List
<img src="https://github.com/krossovochkin/FiberyUnofficial/blob/master/docs/entity_list.png?raw=true" width="200"/>

  - per entity type
  - for particular entity (child entities)
  - pagination support
  - raw filter/sort support
  - create entity
  - add entity relation (pick or create on inner Entity List)
  - remove entity relation (on inner Entity List)
    
### Entity Details
<img src="https://github.com/krossovochkin/FiberyUnofficial/blob/master/docs/entity_details.jpg?raw=true" width="200"/>

  - Entity name (read-only)
  - Description (markdown, read-only)
  - Rich Text field (markdown, read-only)
  - Text fields (read-only)
  - URL fields (read-only, openable)
  - Email fields (read-only, openable)
  - Number fields (read-only, int, decimal, unit support, precision support)
  - Checkbox fields (read-only)
  - Date-Time fields (read-only, date, date-time, date-range, date-time-range support)
  - Single select fields (update supported)
  - Multi select fields (update supported)
  - One-to-one relation fields (update supported)
  - One-to-many relation fields (update supported)
  - Many-to-many relation fields (update supported)
  
### Create Entity
<img src="https://github.com/krossovochkin/FiberyUnofficial/blob/master/docs/entity_create.png?raw=true" width="200"/>

  - Entity name
  - Auto-attach parent entity (if created from inner list)
  
### Extensions

  - Workflow (single-select like)
  - Assignments (many-to-many relation with User entity like)

### Miscellaneous

  - Dark Theme support
  - Material Design Transitions
  - Edge-to-Edge support

## Missing Features
- Views
    - Table
    - Board
    - Timeline
    - Whiteboard
    - Chart
    - etc.
- Extensions
    - Files
    - Avatar
    - Comments
    - etc.

## Login
In order to login one need do the following steps:
- Open app (https://fibery.io will be loaded)
- Login via fibery.io (e.g. login with Google account)
- Wait till the fibery loaded (you should see your boards)
- At the bottom click button "Connect" - it will run JS script to extract your API token, which will be used
- Profit

## Development
[Fibery API](https://api.fibery.io/)

# License

Copyright 2020 Vasya Drobushkov

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Fibery ©
