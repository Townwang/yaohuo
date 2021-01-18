package com.townwang.yaohuo.repo.enum
class Level{
    enum class LevelEnum(val string: String) {
        ONE("1级"),
        TWO("2级"),
        THREE("3级"),
        FOUR("4级"),
        FIVE("5级"),
        SIX("6级"),
        SEVEN("7级"),
        EIGHT("8级"),
        NINE("9级"),
        TEN("10级"),
        ELEVEN("11级"),
        TWELVE("12级");
    }
    companion object {
        fun getLevel(e: String): Int {
            return when (e) {
                LevelEnum.ONE.string -> 1
                LevelEnum.TWO.string -> 2
                LevelEnum.THREE.string -> 3
                LevelEnum.FOUR.string -> 4
                LevelEnum.FIVE.string -> 5
                LevelEnum.SIX.string -> 6
                LevelEnum.SEVEN.string -> 7
                LevelEnum.EIGHT.string -> 8
                LevelEnum.NINE.string -> 9
                LevelEnum.TEN.string -> 10
                LevelEnum.ELEVEN.string -> 11
                LevelEnum.TWELVE.string -> 12
                else -> 0
            }
        }
    }

}