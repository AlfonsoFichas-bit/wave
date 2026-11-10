package com.wave.wallpaper

class FreeList<T : Any>(private val clazz: Class<T>, private val size: Int) {
    private val pool = ArrayDeque<T>(size)
    init { repeat(size) { try { pool.addLast(clazz.getDeclaredConstructor().newInstance()) } catch (_: Exception) {} } }
    fun checkOut(): T? = if (pool.isEmpty()) null else pool.removeFirst()
    fun checkIn(item: T) { if (pool.size < size) pool.addLast(item) }
}

class SwipeData : Runnable {
    var x = 0f
    var actionUp = false
    private var freeList: FreeList<SwipeData>? = null
    private var renderer: WaveRenderer? = null

    fun setX(v: Float): SwipeData { x = v; return this }
    fun setActionUp(v: Boolean): SwipeData { actionUp = v; return this }
    fun setFreeList(fl: FreeList<SwipeData>): SwipeData { freeList = fl; return this }
    fun setRenderer(r: WaveRenderer): SwipeData { renderer = r; return this }

    override fun run() {
        renderer?.handleSwipe()
        freeList?.checkIn(this)
    }
}
PY