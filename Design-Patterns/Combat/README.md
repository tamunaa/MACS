# Assignment 1

## Intro

The assignment is inspired by the 2008 life simulation RTS game [Spore](https://www.spore.com/). Spore allows you to evolve creatures with different characteristics such as claws, sharp teeth, wings, and many many more.

## Simulation

Your task is to model a simple simulation of the interaction between two creatures Predator and Pray. For now, let's imagine that the shape of the world we are modeling is an infinite ray. It starts at position 0 and goes on and on as shown below.

```
012345678...
------------
```

Provide the simulation of the interaction between two creatures as follows

Evolution phase:

- Evolve a random creature at a 0 location (log characteristics)
  This creature will play the role of the predator in the simulation

- Evolve a random creature at a random location between 0 and 1000 (log characteristics)
  This creature will play the role of the pray in the simulation

Chase Phase:

- Predator chases pray, pray runs away until:
  * Predator runs out of `stamina`. (log message: "Pray ran into infinity")
  * Predator catches pray. In this case, they enter the fight

Fight Phase:

- In the fight, both creatures attack until:
  * Predator runs out of `health`. (log message: "Pray ran into infinity")
  * Pray runs out of `health`. (log message: "Some R-rated things have happened") :D

You need to do 100 of these simulations described above and report result after each one of them as described.

Note: During the chase phase it is up to you to decide how smart or desperate you want your creatures to be.
You might decide on a greedy approach (always choose the fastest movement available), but it should be easy to change this
behavior in future development if there is a need.

## Functional requirements

- Ability to evolve `Legs`. Creature needs
  * At least one leg to `Hop`
  * At least two legs to `Walk` or `Run`

- Ability to evolve `Wings`. Creature needs
  * At least two wings to `Fly`

- Ability to evolve `Claws`
  * Small claws multiply creatures attacking `power` x2
  * Medium claws multiply creatures attacking `power` x3
  * Big claws multiply creatures attacking `power` x4

- Ability to evolve mouth with `Teeth`
  * Different degree of sharpness with +3, +6, +9 boost to attacking power

- Ability to `move` the creature in the world
  * Creatures with no limbs or wings can Crawl

- Ability to `attack` other creatures

Movement | requires stamina | uses stamina | speed |
---------|------------------|--------------|-------|
Crawling | 0+               | 1            | 1     |
Hopping  | 20+              | 2            | 3     |
Walking  | 40+              | 2            | 4     |
Running  | 60+              | 4            | 6     |
Flying   | 80+              | 4            | 8     |

## Linting/formatting

- Format and lint your code using `ruff`
- Check your static types with `mypy`

You can find configuration for these tools in playground project developed during sessions.

## Grading

We will not grade solutions:
  - without decomposition
  - with needlessly long methods or classes
  - with code duplications
In all these cases you will automatically get 0% so, we sincerely ask you to 
not make a mess of your code and not put us in an awkward position.

- 20%: It works!
- 20%: It is easy to change.
- 20%: It demonstrates an understanding of design patterns.
- 20%: It demonstrates an understanding of single responsibility and open-closed principles.
- 20%: Linting/formatting.

## Disclaimer

We reserve the right to resolve ambiguous requirements (if any) as we see fit just like a real-life stakeholder would.
So, do not assume anything, ask for clarifications.
assignment1.md
Displaying assignment1.md.