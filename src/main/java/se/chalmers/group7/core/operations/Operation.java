package se.chalmers.group7.core.operations;

public abstract class Operation {

  private boolean executed = false;
  private final long timestamp;

  public Operation() {
    this.timestamp = System.currentTimeMillis();
  }

  public long getTimestamp() {
    return this.timestamp;
  }

  public boolean getExecuted() {
    return this.executed;
  }

  public void redo() {
    if (executed) throw new OperationException("Operation already done");
    executed = true;
  }

  public void undo() {
    if (!executed) throw new OperationException("Operation already undone");
    executed = false;
  }
}
