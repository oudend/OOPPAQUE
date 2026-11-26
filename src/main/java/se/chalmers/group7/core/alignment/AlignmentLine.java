package se.chalmers.group7.core.alignment;

public record AlignmentLine(float x, float y, AlignmentLineType type) {
  public float angle() {
    return type == AlignmentLineType.HORIZONTAL ? 0f : 90f;
  }
}
