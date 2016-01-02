package ch.grisu118.pfcs.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Benjamin Leber
 */
public class Simulator implements Runnable {

    private Set<Animatable> animatables;
    private long oldTime;
    private volatile boolean pause = false;

    public Simulator() {
        animatables = new HashSet<>();
        oldTime = System.currentTimeMillis();
    }

    public Simulator(Animatable[] a) {
        animatables = new HashSet<>(Arrays.asList(a));
        oldTime = System.currentTimeMillis();
    }

    public void addAnimatable(Animatable a) {
        animatables.add(a);
    }

    public void removeAnimatable(Animatable a) {
        animatables.remove(a);
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public boolean isPause() {
        return pause;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            final long currentTime = System.currentTimeMillis();
            if (!pause) {
                animatables.forEach(a -> a.update((currentTime - oldTime) / 1_000.0));
            }
            oldTime = currentTime;
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {
            }
        }
    }
}
