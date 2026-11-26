package se.chalmers.group7.core.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import se.chalmers.group7.core.rendering.Camera;
import se.chalmers.group7.core.rendering.LayerPreview;
import se.chalmers.group7.core.ui.controller.Input;

public class ToolManager {

  String active;

  Map<String, Tool> tools = new HashMap<>();

  public ToolManager() {}

  public void addTool(Tool tool, String id) {
    if (active == null) active = id;

    tools.put(id, tool);
  }

  public void setActive(String id) {
    if (!tools.containsKey(id)) throw new RuntimeException("tool id not defined");

    if (active != null) getActiveTool().deactivate();

    active = id;

    getActiveTool().onSelected();
  }

  public String getActive() {
    return active;
  }

  // TODO: very very temporary change below
  public Tool getActiveTool() {
    return tools.get(active);
  }

  public void onInput(Input input) {
    getActiveTool().onInput(input);
  }

  public Optional<LayerPreview> getActiveLayerPreview() {
    Tool activeTool = getActiveTool();
    return activeTool != null ? activeTool.getLayerPreview() : Optional.empty();
  }

  public void renderOverlay(Camera camera) {
    Tool activeTool = getActiveTool();
    if (activeTool != null) {
      activeTool.getScreenOverlay().ifPresent(overlay -> overlay.render(camera));
    }
  }
}
