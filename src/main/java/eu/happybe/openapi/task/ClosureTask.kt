package eu.happybe.openapi.task

import cn.nukkit.scheduler.Task
import java.util.function.Consumer

class ClosureTask(private val closure: Consumer<Int>) : Task() {
    override fun onRun(i: Int) {
        closure.accept(i)
    }
}