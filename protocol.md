## P2P project, Protocol specification, M1 WI 15/16
[Description](http://learn.heeere.com/2016-net-09db/more-projects.html)

##### Version 2.0 14/03/16

#### 1. Meta team
Team H | | Team E | |
:--- | :--- | :--- | :---
AMYAR Amine | [amyaramine](https://github.com/amyaramine) | DA COSTA VAZ Julien | [julio42](https://github.com/julio42)
BAAZIZ Hamza | [7mza](https://github.com/7mza) | ESCALLE Dimitri | [zooff](https://github.com/zooff)
BENDARI Yassin | [YassineBendari](https://github.com/YassineBendari) | TICHIT Ludovic | [lutchit](https://github.com/lutchit)

#### 2. Project terms
Term | Details
:--- | :---
**Stuff** | actual data to share, file with `name.extension`, will be divided in multiple **Books**
**Library** | (torrent file), description of the **Stuff** to exchange between **Players**, `.libr` extension
**Book** | part of a **Stuff**, unit of exchange between **Players**
**Player** | (torrent peer), program exchanging **Books**, communicate directly with other connected **Players**
**Hub** | (torrent tracker), central server that will hold a list of connected **Players**
**CPList** | list of connected **Players** (HashSet to prevent duplication)
**Periodic** | executed every X ms
**Sync** | synchronous, must wait for an answer before doing anything else
**Async** | asynchronous, does not need an answer
**Zombie** | **Player** that was disconnected, without **Hub** knowing it (after an error)
**BList** | list of all **Books** that a **Player** has

#### 3. Messages description
This is a general overview. When implementing we can combine multiple messages using Enumerations.

##### 3.1. From Player to Hub
Message | Type | Description | Content
:--- | :--- | :--- | :---
**hello** | sync request | open tcp connection with **Hub** |
**update_me** | periodic async request | ask **Hub** for **CPList** |
**bye** | sync request | ask **Hub** to close tcp connection | |

##### 3.2. From Hub to Player
Message | Type | Description | Content
:--- | :--- | :--- | :---
**welcome** | async response | respond to **hello**, inform **Player** that it's connection was accepted |  Information, non critical message
**sorry** | async response | respond to **hello**, inform **Player** that it's connection was refused (limits reached) | Information, non critical message
**take_care** | async response | respond to **bye**, close **Player** tcp connection | Information, non critical message
**take_that** | async response | respond to **update_me**, send updated **CPList** | **CPList** : hub!!take_that!!/ip:port/ip:port/

##### 3.3 From Player to Player
Message | Type | Description | Content
:--- | :--- | :--- | :---
**do_you_have_book** | Async request | ask another **Player** if he have a **Book** | **Book** SHA1
**i_do** | Async response | affirmative response to **do_you_have_book** |
**i_dont** | Async response | negative response to **do_you_have_book** |
**gimme_your_books** | Async request | ask another **Player** for his **BList** |
**take_my_books** | Async response | respond to **gimme_your_books** | **BList**
**gimme_book** | Async request | ask another **Player** to send a **Book** | **Book** SHA1
**take_book** | Async response | respond to **gimme_book**, start sending the actual **Book** | **Book**


#### 5. Limits ( TBD )
- Maximum connection on each **Player** : 10
- Maximum connection on **Hub** : 10
- Maximum length of **CPList** : 10

