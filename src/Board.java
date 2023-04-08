import java.awt.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

public final class Board {
  private static final java.util.List<Vector> NEIGHBOURS = List.of(
    new Vector(0,1),
    new Vector(1,0),
    new Vector(1,1),
    new Vector(-1,0),
    new Vector(0,-1),
    new Vector(-1,-1),
    new Vector(1,-1),
    new Vector(-1,1)
  );
  private static final JFrame MAIN_FRAME = new JFrame();
  private static final JButton STEP_BUTTON = new JButton("STEP");
  private static final JButton RUN_BUTTON = new JButton("RUN");
  private static final JButton STOP_BUTTON = new JButton("STOP");
  private static final JButton CLEAR_BUTTON = new JButton("CLEAR");

  private final int tileSize;
  private final int boardSize;
  private final Tile[][] tiles;

  private ScheduledExecutorService currentExecutor;


  public Board(String boardName, int boardSize, int tileSize) {
    MAIN_FRAME.setTitle(boardName);
    this.boardSize = boardSize;
    this.tileSize = tileSize;
    this.tiles = new Tile[boardSize][boardSize];
    this.createBoard();
  }


  private void createBoard() {
    for (int x = 0; x < this.boardSize; x++) {
      for (int y = 0; y < this.boardSize; y++) {
        this.tiles[x][y] = new Tile(new Vector(x,y), this.tileSize);
      }
    }
  }

  public void show() {
    JPanel menu = new JPanel(new BorderLayout(3,3));
    JToolBar toolBar = new JToolBar();
    toolBar.setFloatable(false);
    MAIN_FRAME.add(toolBar, BorderLayout.PAGE_START);
    STEP_BUTTON.addActionListener(e -> this.step());
    RUN_BUTTON.addActionListener(e -> this.runAuto());
    STOP_BUTTON.addActionListener(e -> this.stopAuto());
    CLEAR_BUTTON.addActionListener(e -> this.clear());
    toolBar.add(STEP_BUTTON);
    toolBar.add(RUN_BUTTON);
    toolBar.add(STOP_BUTTON);
    toolBar.add(CLEAR_BUTTON);
    JPanel panel = new JPanel(new GridLayout(0, this.boardSize));
    for (Tile[] row : tiles) {
      for (Tile tile : row) {
        panel.add(tile.getButton());
      }
    }
    menu.add(panel);
    MAIN_FRAME.add(menu);
    MAIN_FRAME.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    MAIN_FRAME.pack();
    MAIN_FRAME.setVisible(true);
    STOP_BUTTON.setEnabled(false);

  }

  private void step() {
    /*Write next state for all cells*/
    for (Tile[] row : this.tiles) {
      for (Tile tile : row) {
        long count = this.getAliveNeighbourCount(tile);
        if (count == 3L && tile.getState() == State.DEAD) {
          tile.switchState();
          continue;
        }
        if ((count < 2 || count > 3) && tile.getState() == State.ALIVE) {
          tile.switchState();
        }
      }
    }

    /*Update State for all Cells*/
    for (Tile[] row : this.tiles) {
      for (Tile tile : row) {
        tile.step();
      }
    }
  }

  private long getAliveNeighbourCount(Tile tile) {
    return NEIGHBOURS.stream()
      .filter(n -> tile.getPosition().x() + n.x() < this.boardSize && tile.getPosition().x() + n.x() > 0)
      .filter(n -> tile.getPosition().y() + n.y() < this.boardSize && tile.getPosition().y() + n.y() > 0)
      .map(n -> this.tiles[tile.getPosition().x() + n.x()][tile.getPosition().y() + n.y()])
      .filter(t -> t.getState() == State.ALIVE).count();
  }

  private void runAuto() {
    RUN_BUTTON.setEnabled(false);
    STOP_BUTTON.setEnabled(true);
    CLEAR_BUTTON.setEnabled(false);
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    this.currentExecutor = executor;
    executor.scheduleAtFixedRate(this::step, 0, 250L, TimeUnit.MILLISECONDS);
  }

  private void stopAuto() {
    RUN_BUTTON.setEnabled(true);
    STOP_BUTTON.setEnabled(false);
    CLEAR_BUTTON.setEnabled(true);
    this.currentExecutor.shutdownNow();
  }

  private void clear() {
    for (Tile[] row : this.tiles) {
      for (Tile tile : row) {
        tile.setState(State.DEAD);
      }
    }
  }
}
