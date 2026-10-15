# ClueGame

ClueGame is a Java desktop implementation of the classic deduction game Clue. One human player competes with computer-controlled players to identify the person, weapon, and room in a hidden solution. The project combines a configurable board and game model with a graphical interface built in Java Swing.

## What the game includes

- A board loaded from CSV layout and text setup files, including rooms, doors, secret passages, player colors, and starting positions.
- Mouse-based human movement with legal destinations calculated from a dice roll. Occupied walkway cells block movement, and reaching a room can trigger a suggestion.
- A shuffled card deck, hidden solution, and distributed hands. Suggestions are checked against the other players in turn order, who reveal a matching card when they can.
- Computer players that choose reachable rooms they have not seen when possible, make suggestions using unseen person and weapon cards, and can make accusations.
- A Swing interface that draws the board and players and provides turn controls, suggestion results, accusation controls, and a panel for tracking known cards.

## How it is organized

```text
ClueGame/
├── data/             # Board layout and game setup files used at runtime
├── src/
│   ├── clueGame/      # Board, game rules, players, turn flow, and Swing UI
│   ├── experiment/   # Board and cell experiments
│   └── tests/        # JUnit tests for setup, movement, cards, and AI
└── ClueInitFiles/    # Additional initialization and configuration test files
```

`Board` loads and validates the configuration, builds board-cell adjacencies, manages movement targets, and handles cards and suggestions. `TurnManager` coordinates turns and UI interactions. `Player`, `HumanPlayer`, and `ComputerPlayer` hold player state and deduction information; the Swing panels and dialogs display the game and collect player input.

The runtime setup is in `data/ClueSetup.txt` and the grid is in `data/ClueMap.csv`. The setup file defines rooms, spaces, weapons, and players. The CSV tokens describe each board cell, including door directions, room centers, labels, and secret passages. Related valid and malformed fixtures are included for initialization and error-handling checks.

## Run the game

This is an Eclipse Java project. Import the repository as an existing Java project, ensure its configured JUnit dependency is available, and run `clueGame.ClueGame` from `src/clueGame/ClueGame.java`. Launch it with the project root as the working directory so the relative `data/` configuration paths resolve.

JUnit tests are in `src/tests/`; run them from the IDE with the project's JUnit configuration.

## Technologies

- Java
- Java Swing
- JUnit
- CSV and text configuration files

## Contributors

Jake DiVito and AJ Mendes developed this project collaboratively.
