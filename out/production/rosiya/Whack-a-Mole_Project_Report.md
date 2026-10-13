# Academic Project Report
## Java Swing GUI-Based Whack-a-Mole Game
**Single-File Mini-Project: `WhackAMole.java`**

> Course Micro-App / Time-Attack Educational Game
> Technology: Java SE (JDK 8+), Java Swing / AWT, Event-Driven Architecture

---

## 1. INDEX PAGE

| S. No. | Section Title | Sub-Sections | Page No. |
| :--- | :--- | :--- | :--- |
| 1 | Index Page | — | i |
| 2 | Project Overview | 2.1 Introduction<br>2.2 Project Objectives<br>2.3 Target Audience | 1 |
| 3 | Technical Specifications & Libraries Used | 3.1 `javax.swing.*`<br>3.2 `java.awt.*` & `java.awt.event.*`<br>3.3 `java.util.Random` | 2 |
| 4 | Core Application Logic & Timing Mechanics | 4.1 Game Initialization & Boot Cycle<br>4.2 Dual Timer Threading<br>4.3 Interaction Evaluation Pipeline | 4 |
| 5 | User Interface Interaction & Screenshot Breakdown | Placeholder 1 – Successful Hit State<br>Placeholder 2 – Empty Hole Penalty State<br>Placeholder 3 – Active Game-Loop Session<br>Placeholder 4 – Session Termination State | 6 |
| 6 | Conclusion & Future Scope | 6.1 Conclusion<br>6.2 Future Scope | 11 |

---

## 2. PROJECT OVERVIEW

### 2.1 Introduction

Whack-a-Mole is a classic time-attack arcade reaction game in which targets, referred to as moles, appear at random positions within a fixed grid for a limited duration. The player must identify and strike the active target before it relocates.

This project implements the concept as a desktop micro-application using Java Swing in a single file (`WhackAMole extends JFrame implements ActionListener`). The board is a 3×3 grid of `JButton` cells representing holes. A single mole indicator with text `(O_O)` and a Green background appears on one cell at a time and jumps to a new random position every 800 ms initially. The player competes against a 30-second session countdown timer, maximizing score through accurate clicks (+10) while avoiding penalty clicks on empty holes (−5).

The system is entirely event-driven and operates without manual thread management, relying on Swing's timer event dispatch mechanism for deterministic, thread-safe UI updates on the Event Dispatch Thread (EDT).

### 2.2 Project Objectives

* **Mastering background execution threads via `javax.swing.Timer`:** Demonstrate non-blocking periodic execution on the EDT with two concurrent timers, eliminating explicit `Thread` handling while keeping the UI responsive.
* **Implementing dynamic graphical updates:** Practice real-time mutation of component properties including `JButton.setText()`, `setBackground()`, `setEnabled()`, and `JLabel.setText()` in response to timer ticks and user input.
* **Designing state machinery for multi-timer loops:** Model and coordinate two concurrent state machines — the mole-movement loop and the session-lifecycle loop — including synchronized start, independent tick rates, dynamic speed change, and synchronized termination.
* **Implementing coordinate system grids:** Map a linear array index range 0–8 to a 2-D 3×3 visual grid using `GridLayout(3,3)`, establishing a clear index-to-coordinate model backed by `JButton[9]` and `random.nextInt(9)`.
* **Developing score adjustment state models:** Implement conditional score mutation with boundary protection (`+10` reward, `−5` penalty, floor at `0`) plus advanced difficulty progression (speed-up every 50 points).
* **Handling responsive user interaction:** Implement robust `ActionListener`-based validation that distinguishes valid target hits from invalid empty-cell clicks and reset control clicks.

### 2.3 Target Audience

Designed as an interactive, time-attack educational micro-app for:

1. Undergraduate Computer Science students learning Java GUI programming, event handling, and timer-based concurrency.
2. Instructors needing a minimal, comprehensible example of dual-timer architecture and Model-View interaction in Swing.
3. Novice users seeking a short-duration, high-feedback reaction game demonstrating human-computer interaction principles.

---

## 3. TECHNICAL SPECIFICATIONS & LIBRARIES USED

Platform: Java SE (JDK 8+, verified on JDK 21)
GUI Framework: Java Swing / AWT
Architecture: Single `JFrame`, `BorderLayout`, Event-Driven
Window: `setSize(400,450)`, `setLocationRelativeTo(null)` (centered), `EXIT_ON_CLOSE`
File: `WhackAMole.java` → `WhackAMole.class` (compile: `javac WhackAMole.java`, run: `java WhackAMole`)

### 3.1 `javax.swing.*` — Structural Container Controls, Button Click Canvas Systems, and Background Tick Event Intervals

| Class | Role in This Project | Why Imported |
| :--- | :--- | :--- |
| `JFrame` (`WhackAMole extends JFrame`) | Top-level window. Hosts all panels, manages paint, layout, close operation, centering, visibility. | Structural container for entire session. |
| `JPanel` | Sub-containers: `topPanel` (status), `gridPanel` (board), `bottomPanel` (reset). | Segregates score/timer display from 3×3 canvas and reset control. |
| `JButton[9]` (`holes[0..8]`) | 9 hole cells. Visual canvas + input sensor. Text/color encodes mole vs. empty. Default `LIGHT_GRAY`, mole `GREEN` + `(O_O)`. `setBorderPainted(false)`, `setFocusPainted(false)`, `setFocusable(false)` for clean canvas, `setEnabled(false)` on game over. | Core interactive component. |
| `JButton` (`playAgainButton`) | SOUTH `Play Again` reset. `setEnabled(false)` while active, `true` on game over. | Lifecycle control. |
| `JLabel` (`scoreLabel`, `timeLabel`) | Status indicators: `"Score: 0"`, `"Time Left: 30s"`. Updated on every hit/miss and every second. Bold 16pt, centered. | Non-editable real-time feedback. |
| `JOptionPane` | Terminal `showMessageDialog`: `"Game Over!\nFinal Score: X"`. | Modal session-end reporting. |
| `Timer` (`countdownTimer`, `moleTimer`) | Background tick generators. `countdownTimer(1000)`, `moleTimer(800 → 300)`. Both use `this` as `ActionListener`, run on EDT. `start()`, `stop()`, `restart()`, `setDelay()`. | EDT-safe periodic callbacks; no manual synchronization needed. |

`javax.swing.Timer` (not `java.util.Timer`) was chosen because callbacks execute on the EDT, so all `setText()` / `setBackground()` / `setEnabled()` calls are thread-safe.

### 3.2 `java.awt.*` & `java.awt.event.*` — Grid Structures, Structural Formatting Configurations, and Responsive User Click Handlers

| Class / Interface | Role in This Project | Why Imported |
| :--- | :--- | :--- |
| `BorderLayout` | Root frame layout: `NORTH=topPanel`, `CENTER=gridPanel`, `SOUTH=bottomPanel`. | Fixed header + expanding play area + footer control; responsive resizing. |
| `GridLayout(1,2)` | `topPanel`: 1 row, 2 cols for score + time labels. | Equal-width status split. |
| `GridLayout(3,3,5,5)` | `gridPanel`: 3×3 equal cells with 5px gaps + `EmptyBorder(10,10,10,10)`. | Coordinate grid; uniform holes. Index mapping: 0=R0C0 … 8=R2C2. |
| `FlowLayout` | `bottomPanel`: centered `Play Again`. | Standard footer button alignment. |
| `Font("Arial",BOLD,16/20)` | Labels 16pt, holes 20pt for `(O_O)` legibility. | Readability of state. |
| `Color` | `LIGHT_GRAY` = empty, `GREEN` = mole. `setOpaque(true)` ensures fill visible. | Instant visual state encoding without image assets. |
| `ActionListener` + `actionPerformed(ActionEvent e)` | Single handler for 9 holes + 2 timers + Play Again. Branches on `e.getSource()`. | Unified responsive click + tick handling. |
| `ActionEvent` | Carries `getSource()` identity. | Distinguishes hit vs. miss vs. timer vs. reset. |
| `SwingConstants`, `BorderFactory`, `SwingUtilities` | Centering labels, padding, `invokeLater()` launch. | Correct EDT boot. |

### 3.3 `java.util.Random` — Background Engine Logic for Target Selection

Single instance `private Random random = new Random();` used only by `moveMole()`:

```java
currentMoleIndex = random.nextInt(9); // 0 to 8 inclusive
```

Each mole-timer tick (and each successful hit) clears the old cell and activates the new random index. Unpredictable displacement is the core difficulty mechanic. Range `0–8` maps directly to `holes[]` array positions.

---

## 4. CORE APPLICATION LOGIC & TIMING MECHANICS

### 4.1 Game Initialization & Boot Cycle

Boot sequence in constructor + `setupTimers()`:

1. **Frame config:** `super("Whack-a-Mole")`, `setSize(400,450)`, `EXIT_ON_CLOSE`, `setLocationRelativeTo(null)`, `setLayout(BorderLayout)`.
2. **NORTH build:** `JPanel(GridLayout(1,2))` + `scoreLabel="Score: 0"`, `timeLabel="Time Left: 30s"`.
3. **CENTER build:** `JPanel(GridLayout(3,3,5,5))` + 9× `JButton("")`, `LIGHT_GRAY`, opaque, no border/focus, bold font, `addActionListener(this)`.
4. **SOUTH build:** `JPanel(FlowLayout.CENTER)` + `Play Again`, `setEnabled(false)`.
5. **`setupTimers()`:** `countdownTimer=new Timer(1000,this)`, `moleTimer=new Timer(moleDelay=800,this)`, both `start()` simultaneously, then immediate `moveMole()` so first paint already shows a mole.
6. **`main()`:** `SwingUtilities.invokeLater(() -> new WhackAMole().setVisible(true))` for EDT-safe launch.

State reset fields: `score=0`, `timeLeft=30`, `currentMoleIndex=-1`, `moleDelay=800`, `nextSpeedUpAt=50`, all buttons cleared to gray and enabled.

### 4.2 Dual Timer Threading — Separation of Roles

Both are `javax.swing.Timer` (EDT logical threads), independent periods and duties:

**A. Mole-Movement Timer — Grid Index Jump Engine**

* Interval: initial `800 ms`, dynamic down to `300 ms`.
* Tick (`src==moleTimer`): calls `moveMole()`:
  1. Clear previous: `holes[currentMoleIndex].setText("")`, `setBackground(LIGHT_GRAY)`.
  2. New index: `currentMoleIndex=random.nextInt(9)`.
  3. Activate: `setText("(O_O)")`, `setBackground(GREEN)`.
* Purpose: real-time random movement trigger; defines reaction window.

**B. Session Lifecycle Countdown Timer — Game Session Limiter**

* Interval: `1000 ms` (once per second).
* Tick (`src==countdownTimer`):
  1. `timeLeft--`, `timeLabel.setText("Time Left: "+timeLeft+"s")`.
  2. If `timeLeft<=0` → `gameOver()`.
* Purpose: enforces 30 s time-attack limit; drives decrement display; halts all loops.

Separation allows tuning speed vs. duration independently. Difficulty progression only touches `moleTimer.setDelay()` / `setInitialDelay()`.

Difficulty rule implemented in hit branch:

```java
if (score >= nextSpeedUpAt) {
  nextSpeedUpAt += 50;
  moleDelay = Math.max(300, moleDelay - 100);
  moleTimer.setDelay(moleDelay);
  moleTimer.setInitialDelay(moleDelay);
}
```

So at 50→700 ms, 100→600 ms, 150→500 ms, 200→400 ms, 250+→300 ms floor.

### 4.3 Interaction Evaluation Pipeline — `actionPerformed` Conditional Validation

All events route to one `actionPerformed(ActionEvent e)`:

1. **Source ID:** `Object src=e.getSource()`.
2. **Timer branches first:** `countdownTimer` → decrement/check; `moleTimer` → `moveMole()`; `playAgainButton` → `resetGame()`; else scan `holes[i]`.
3. **Guard:** `if (!holes[i].isEnabled()) return;` ignores post-game clicks.
4. **HIT — `i==currentMoleIndex && text.equals("(O_O)")`:**
   * `score+=10`, `scoreLabel` update.
   * Difficulty check (above).
   * `moveMole()` + `moleTimer.restart()` — instantaneous displacement + full interval reset prevents double-farming same mole.
5. **MISS — else (blank gray button):**
   * `score-=5; if (score<0) score=0;`, `scoreLabel` update.
   * No mole move — mole stays, spam-clicking penalized.
6. **Repaint:** automatic on EDT.

---

## 5. USER INTERFACE INTERACTION & SCREENSHOT BREAKDOWN

> Paste your runtime screenshots over each placeholder box before submission. Tables describe expected component states and logic to verify against.

### Screenshot Placeholder 1: Successful Hit / Mole Whacked State

> **[PASTE SCREENSHOT 1 HERE: cursor on GREEN “(O_O)” cell, Score e.g. 10/20, mole visible at a NEW cell in next frame]**

| Action | Component Effect | State Change |
| :--- | :--- | :--- |
| Click active `(O_O)` button | Old cell cleared to `""` + `LIGHT_GRAY` | Text layout index map cleared at `currentMoleIndex` |
| Validation `clicked==currentMole+text match` | Score label `+10` instantly | `score+=10`, e.g. 0→10→20 |
| Auto-displacement | New random cell shows `(O_O)`+`GREEN` immediately | `moveMole()` + `moleTimer.restart()` forces jump, resets 800 ms window |
| Difficulty (if ≥50) | No visual change, but next jumps faster | `setDelay(-100 ms)` toward 300 ms floor |

Logic: only rewarded path. Erase-then-relocate gives instant feedback and prevents double-count within one mole interval.

### Screenshot Placeholder 2: Empty Hole Penalty Click State

> **[PASTE SCREENSHOT 2 HERE: click on blank gray cell, Score decreased or held at 0, mole unchanged]**

| Action | Component Effect | State Change |
| :--- | :--- | :--- |
| Click blank gray button | Press flash only; stays `""`+`LIGHT_GRAY` | Mole remains at `currentMoleIndex` |
| Validation `clicked!=currentMole` | Score `−5` | `score-=5` + `scoreLabel` update |
| Boundary guard | Never negative | `if (score<0) score=0;` e.g. 10→5→0→0 |

Logic: discourages spam-clicking. No `moveMole()` / no timer reset on miss.

### Screenshot Placeholder 3: Active Game-Loop Session Progression

> **[PASTE SCREENSHOT 3 HERE: mid-game, e.g. Time Left: 18s, Score: 35, mole at different coordinate vs. Shots 1–3]**

| Region | Visual | Logic |
| :--- | :--- | :--- |
| Time label | Decremented, e.g. `Time Left: 18s` | `countdownTimer` 1000 ms `timeLeft--` updates |
| Score label | Accumulated hits−misses | Live `actionPerformed` mutations |
| Grid | Mole color swap at new coordinate | `moleTimer` 800 ms (or faster if sped up) clear-old/paint-new |
| Controls | All holes enabled, Play Again disabled | Steady-state loop; no freeze |

Logic: proves concurrency — 1 s countdown vs. faster mole jumps run together. Player must track swaps continuously.

### Screenshot Placeholder 4: Session Termination / Terminal Screen State

> **[PASTE SCREENSHOT 4 HERE: Time Left: 0s, all-gray disabled grid, modal “Game Over! Final Score: XX” dialog, enabled Play Again]**

| Trigger | Effect | Code |
| :--- | :--- | :--- |
| `timeLeft` hits `0` | Both loops halt | `countdownTimer.stop(); moleTimer.stop();` |
| Freeze | All 9 cleared to `""`+`LIGHT_GRAY`, `setEnabled(false)` | No further clicks register; score locked |
| Label | `Time Left: 0s` | Final decrement paint |
| Dialog | `JOptionPane` summary modal | `showMessageDialog(this,"Game Over!\nFinal Score: "+score,...)` |
| Reset enable | `Play Again` becomes clickable | `playAgainButton.setEnabled(true)` |
| Reset click | `score=0,time=30,delay=800`, re-enable, restart | `resetGame()`: labels reset, `setDelay(800)`, both `start()`, `moveMole()`, Play Again disabled again |

Logic: deterministic shutdown preserves final-score integrity. Restart fully restores initial conditions including speed floor reset and `nextSpeedUpAt=50`.

---

## 6. CONCLUSION & FUTURE SCOPE

### 6.1 Conclusion

The Swing Whack-a-Mole implementation demonstrates structural stability of the Swing UI framework for real-time micro-games. A `JFrame`/`JPanel` hierarchy with `BorderLayout` + `GridLayout` coordinate mapping, dual `javax.swing.Timer` loops, and a single `ActionListener` pipeline delivers responsive, EDT-safe updates without explicit multithreading. The `+10 / −5` score model with zero-floor guard, threshold-based acceleration, and `timer.stop()` + `setEnabled(false)` shutdown ensures state integrity from boot through termination dialog to `Play Again` reset. The project validates event-driven programming, dynamic component mutation, and multi-timer coordination in under 250 lines of single-file code.

### 6.2 Future Scope

* **Dynamically accelerating mole speed (already initiated):** Extend to exponential curve or level-based `setDelay()` plus visual speed indicator.
* **High-score persistence:** Save top scores to local text file via `FileWriter`/`BufferedReader`, load leaderboard on boot.
* **Media audio triggers:** Add `javax.sound.sampled.Clip` hit/miss sounds in respective `actionPerformed` branches.
* **Further evolution:** Multiple simultaneous moles, `ImageIcon` mole sprites, pause/resume button, difficulty selector (Easy/Normal/Hard initial delays), and countdown progress bar.

---

## Appendix: How to Build & Run (for Evaluator)

```
javac WhackAMole.java
java WhackAMole
```

* File: `E:\rosiya\WhackAMole.java` (this repo)
* Requires: JDK 8+ (tested JDK 21), no external libraries.
* Controls: Click GREEN `(O_O)` = +10; click gray = −5 (min 0); 30 s limit; `Play Again` after Game Over.
