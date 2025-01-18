# Testing

An attempt was made to write tests however due to the way the program was written it was very difficult to properly test things.
As such at least a list of tests has been created detailing what should be tested. If these tests pass then the program should
work correctly and without issue.

## What should be tested

### Adding prints
- When adding a print it should be assigned to a printer that can physically print it that has a matching spool.
- When adding a print it should be assigned to a printer that can physically print it and a spool change is initiated.
- When adding a print and there is no printer available that can physically print it is added to the queue.
- When a printer is marked as ready a print is selected that fits and uses the same spool.
- Starting the queue will attempt to fill all printers.
- When adding multiple prints in the order of Blue, Red, Blue and a correct printer has a Blue spool. 
  It will select the third print when it is marked ready.
- 

## Print and Printer fit Truth table

The following tables show which print can be printed by which printer.

| PLA and PETG                 | Enterprise | Serenity | Red Dwarf | Heart of Gold | Tardis | Rocinante | Bebop |
|------------------------------|------------|----------|-----------|---------------|--------|-----------|-------|
| Acoustic Guitar Cooky Cutter | V          | V        | V         | V             | V      | V         | V     |
| Stegosaurus Pickholder       | V          | V        | V         | V             | V      | V         | X     |
| Collapsing Jian              | V          | V        | V         | V             | X      | V         | X     |
| Earth Globe                  | X          | X        | X         | V             | X      | X         | X     |
| Moon Lamp                    | X          | X        | V         | X             | X      | X         | X     |
| Cathedral                    | X          | X        | X         | V             | X      | X         | X     |
| Fucktopus                    | V          | V        | V         | V             | V      | V         | V     |
| Lizard                       | X          | X        | X         | V             | X      | X         | X     |
| Tree Frog                    | X          | X        | X         | V             | X      | X         | X     |

| ABS                          | Enterprise | Serenity | Red Dwarf | Heart of Gold | Tardis | Rocinante | Bebop |
|------------------------------|------------|----------|-----------|---------------|--------|-----------|-------|
| Acoustic Guitar Cooky Cutter | X          | V        | X         | X             | X      | V         | V     |
| Stegosaurus Pickholder       | X          | V        | X         | X             | X      | V         | X     |
| Collapsing Jian              | X          | V        | X         | X             | X      | V         | X     |
| Earth Globe                  | X          | X        | X         | X             | X      | X         | X     |
| Moon Lamp                    | X          | X        | X         | X             | X      | X         | X     |
| Cathedral                    | X          | X        | X         | X             | X      | X         | X     |
| Fucktopus                    | X          | V        | X         | X             | X      | V         | V     |
| Lizard                       | X          | X        | X         | X             | X      | X         | X     |
| Tree Frog                    | X          | X        | X         | X             | X      | X         | X     |