package app.task1.util;

public class LoadingBar {
    private int current;
    private int all;

    public LoadingBar(int all) {
        this.all = all;
        this.current = 0;
    }

    public void loadingOutOf() {
        this.current++;
        System.out.print("\t" + current + " out of " + all + "\r");
    }

    public void loadingProcess() {
        this.current++;
        System.out.print("\t|");
        for (int i = 0; i < current; i++) {
            System.out.print("=");
        }
        for (int i = current; i < all; i++) {
            System.out.print(" ");
        }
        System.out.print("|\r");
    }
}
