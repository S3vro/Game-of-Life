import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public final class Tile {

  private final JButton button;
  private State state;
  private final Vector position;
  private State nextState;

  public Tile(Vector position, int size) {
    this.position = position;
    this.button = new JButton();
    this.state = State.DEAD;
    this.nextState = this.state;
    this.button.setBackground(this.state.getColor());
    this.button.setMargin(new Insets(0,0,0,0));
    this.button.setBorder(null);
    ImageIcon icon = new ImageIcon(new BufferedImage(size,size, BufferedImage.TYPE_INT_ARGB));
    this.button.setIcon(icon);
    this.button.addActionListener(e -> {
      this.switchState();
      this.step();
    });
  }


  public void switchState() {
    this.nextState = this.state == State.ALIVE ? State.DEAD : State.ALIVE;
  }

  public JButton getButton() {
    return this.button;
  }

  public State getState() {
    return this.state;
  }

  public Vector getPosition() {
    return this.position;
  }

  public void step() {
    this.state = this.nextState;
    this.button.setBackground(this.state.getColor());
  }

  public void setState(State state) {
    this.nextState = state;
    this.step();
  }


}
