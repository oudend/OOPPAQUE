package se.chalmers.group7.core.operations;

import java.util.Arrays;
import java.util.List;

public class CompositeOperation extends Operation {
  private List<Operation> operations;

  public CompositeOperation(Operation[] operations) {
    super();
    this.operations = Arrays.asList(operations);

    redo();
  }

  @Override
  public void redo() {
    super.redo();
    for (Operation operation : operations) {
      operation.redo();
    }
  }

  @Override
  public void undo() {
    super.undo();
    for (int i = operations.size() - 1; i >= 0; i--) {
      Operation operation = operations.get(i);

      operation.undo();
    }
  }
}
