package se.chalmers.group7.core.operations;

public class OperationManager {
  private OperationNode operationRoot;
  private OperationNode active;
  private int undoLimit;

  private int operationCount = 0;
  private int operationsAhead = 0;

  public OperationManager(OperationNode operationRoot, int undoLimit) {
    this.operationRoot = operationRoot;
    this.active = operationRoot;
    this.undoLimit = undoLimit;
  }

  public OperationManager(OperationNode operationRoot) {
    this(operationRoot, 256);
  }

  public OperationManager() {
    this(null, 256);
  }

  private void deleteRootOperation() {
    operationCount--;

    if (operationRoot.getNext() == null) {
      operationRoot = null;
      return;
    }

    operationRoot = operationRoot.getNext();
    operationRoot.setPrevious(null);
  }

  public void addOperation(Operation operation) {
    if (active != null && active.getPrevious() == null && !active.getExecuted()) active = null;

    OperationNode node = new OperationNode(active, operation);

    operationCount -= operationsAhead;

    operationCount++;

    if (operationCount > undoLimit) deleteRootOperation();

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

    operationsAhead--;

    active.redo();
  }

  public void undo() {
    if (active == null) return;
    if (active.getPrevious() == null && !active.getExecuted()) return;

    active.undo();

    operationsAhead++;

    if (active.getPrevious() != null) active = active.getPrevious();
  }
}
