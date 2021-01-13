package ppj;

public class StatePair {
    private int leftState;
    private int rightState;

    public StatePair(int leftState, int rightState) {
        this.leftState = leftState;
        this.rightState = rightState;
    }

    public int getLeftState() {
        return leftState;
    }

    public void setLeftState(int leftState) {
        this.leftState = leftState;
    }

    public int getRightState() {
        return rightState;
    }

    public void setRightState(int rightState) {
        this.rightState = rightState;
    }
}
