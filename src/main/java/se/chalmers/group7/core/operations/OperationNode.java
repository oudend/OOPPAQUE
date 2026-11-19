package se.chalmers.group7.core.operations;

public class OperationNode {
  private final Operation operation;
  private OperationNode previous;
  private OperationNode next;

  public OperationNode(OperationNode previous, Operation operation) {
    this.previous = previous;
    this.operation = operation;
  }

  public OperationNode getPrevious() {
    return previous;
  }

  public OperationNode getNext() {
    return next;
  }

  public void setNext(OperationNode next) {
    this.next = next;
  }

  public void setPrevious(OperationNode previous) {
    this.previous = previous;
  }

  public boolean getExecuted() {
    return operation.getExecuted();
  }

  public void redo() {
    operation.redo();
  }

  public void undo() {
    operation.undo();
  }
}
