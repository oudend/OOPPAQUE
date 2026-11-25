package se.chalmers.group7.core.elements.BarSlider;

import static com.raylib.Raylib.*;

import se.chalmers.group7.core.ui.controller.Input;
import se.chalmers.group7.core.ui.model.Action;

public class ASliderInput implements Action {
  private final BarSlider slider;
  private final boolean debug;

  public ASliderInput(BarSlider slider) {
    this.slider = slider;
    debug = false;
  }

  public ASliderInput(BarSlider slider, boolean debug) {
    this.slider = slider;
    this.debug = debug;
  }

  public void executeAction(Input input) {
    slider.moveSliderBar(input);
  }
}
