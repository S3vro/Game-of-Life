import java.awt.*;

public enum State {
  DEAD(Color.white),
  ALIVE(Color.black);

  private final Color color;

  State(Color color) {
    this.color = color;
  }

  public Color getColor() {
    return this.color;
  }
}
