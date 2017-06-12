#### Whats working :

###### Hub
- Can librarify a file
- Can accept clients
- Can keep track of connected players in real time
- Can inform other players when someone join or leave
- Can send list of connected players when asked to

###### Player
- Can process a libr file
- Can extract neccessary data from libr file
- Can create dummy file with the size of the original file to copy
- Can connect to Hub
- Can extract connected players from Hub
- Can send back an indexed BOOK
- Can verify sha1 of a received BOOK
- Can write received BOOK to dummy file

#### Difficulties faced :
- Processing mixed String headers with serializable object in the communication messages
- Avoiding infinite loops
- Dependecy injection

#### Lessons learned :
- Do not reinvent the wheel
- Start with major parts, then leave minor tweaks to the end

#### What would be done differently :


