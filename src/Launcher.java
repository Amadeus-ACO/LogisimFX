import LogisimFX.Main;

public class Launcher {
    public static void main(String[] args) {

        System.setProperty("quantum.multithreading", "false");
        System.setProperty("javafx.animation.fullspeed", "false");
        System.setProperty("javafx.animation.pulse", "60");
        System.setProperty("javafx.animation.framerate", "60");

        Main.main(args);

    }
}