package com.example.gameoflife

class GameBoard(width: Int, height: Int) {
    private var boardState: HashMap<Pair<Int, Int>, Boolean> = HashMap()

    private var xOffSet = 0
    private var yOffSet = 0

    fun getState(x: Int, y: Int): Boolean {
        return getStateRaw(x + xOffSet, y + yOffSet)
    }

    fun getStateRaw(x: Int, y: Int): Boolean {
        val isAlive = this.boardState[Pair(x, y)];
        if (isAlive != null) {
            return isAlive
        }
        return false
    }

    fun toggleState(x: Int, y: Int) {
        val index = Pair(x + xOffSet, y + yOffSet)
        val isAlive = this.boardState[index]
        if (isAlive == null) {
            this.boardState[index] = true
        }else {
            this.boardState.remove(index)
        }
    }

    fun clearState() {
        this.xOffSet = 0
        this.yOffSet = 0
        this.boardState = HashMap()
    }

    fun moveLeft() {
        yOffSet -= 1
    }

    fun moveRight() {
        yOffSet += 1
    }

    fun moveUp() {
        xOffSet -= 1
    }

    fun moveDown() {
        xOffSet += 1
    }

    fun progressBoard() {
        val newBoard: HashMap<Pair<Int, Int>, Boolean> = HashMap();
        val countBoard = generateCountBoard()
        for (index in countBoard.keys) {
            val isAlive = getStateRaw(index.first, index.second)
            val count = countBoard[index]
            if (count == null){
                println("found null count")
                continue
            }
            if (isAlive) {
                if (count == 2 || count == 3) {
                    newBoard[index] = true
                }
            }else {
                if(count == 3) {
                    newBoard[index] = true
                }
            }
        }
        boardState = newBoard
    }

    private fun generateCountBoard(): HashMap<Pair<Int, Int>, Int> {
        val countBoard: HashMap<Pair<Int, Int>, Int> = HashMap()
        for (alive in boardState.keys) {
            for (yd in -1..1) {
                for (xd in -1..1) {
                    if (xd == 0 && yd == 0) continue
                    val index = Pair(alive.first + xd, alive.second + yd)
                    val cur = countBoard[index]
                    if (cur != null) {
                        countBoard[index] = cur + 1
                    } else {
                        countBoard[index] = 1
                    }
                }
            }
        }
        return countBoard
    }
}
