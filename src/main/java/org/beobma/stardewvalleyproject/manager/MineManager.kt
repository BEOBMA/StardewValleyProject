package org.beobma.stardewvalleyproject.manager

interface MineHandler {
    fun reset()
}

class DefaultMineManager : MineHandler {
    override fun reset() {
        // 하루가 넘어가거나, 기타 이유로 광산 내부를 리셋해야할 경우
    }
}

object MineManager {
    private val converter: MineHandler = DefaultMineManager()

    fun reset() {
        converter.reset()
    }
}