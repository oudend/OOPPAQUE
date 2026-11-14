package se.chalmers.group7.core.operations;

public class OperationManager {
  private OperationNode operationRoot;

  private OperationNode active;

  public OperationManager(OperationNode operationRoot) {
    this.operationRoot = operationRoot;
    this.active = operationRoot;
  }

  public OperationManager() {
    this(null);
  }

  public void addOperation(Operation operation) {
    if (active != null && active.getPrevious() == null && !active.getExecuted()) active = null;

    OperationNode node = new OperationNode(active, operation);

    if (active == null) {
      operationRoot = node;
      active = operationRoot;
      return;
    }

    active.setNext(node);

    active = node;
  }

  public void redo() {
    if (active == null) return;
    if (active.getNext() == null && active.getExecuted()) return;

    if (active.getExecuted()) active = active.getNext();

    active.redo();
  }

  public void undo() {
    if (active == null) return;
    if (active.getPrevious() == null && !active.getExecuted()) return;

    active.undo();

    if (active.getPrevious() != null) active = active.getPrevious();
  }
}
