package eu.happybe.openapi.task;

import cn.nukkit.scheduler.Task;

import java.util.function.Consumer;

public class ClosureTask extends Task {

    private final Consumer<Integer> closure;

    public ClosureTask(Consumer<Integer> closure) {
        this.closure = closure;
    }

    @Override
    public void onRun(int i) {
        this.closure.accept(i);
    }
}
