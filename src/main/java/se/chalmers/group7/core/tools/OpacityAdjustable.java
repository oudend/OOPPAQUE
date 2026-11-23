package se.chalmers.group7.core.tools;

public interface OpacityAdjustable {
    NumericSetting getOpacity();

    default void increaseOpacity(int amount) {
        getOpacity().increase(amount);
    }

    default void decreaseOpacity(int amount) {
        getOpacity().decrease(amount);
    }
}
