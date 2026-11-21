package se.chalmers.group7.core.tools;

import java.util.HashMap;
import java.util.Map;
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

  private Tool getActiveTool() {
    return tools.get(active);
  }

  public void onInput(Input input) {
    getActiveTool().onInput(input);
  }

  //    public void onSelected() {
  //        getActiveTool().onSelected();
  //    }
  //
  //    public void deactivate() {
  //        getActiveTool().deactivate();
  //    }
}
