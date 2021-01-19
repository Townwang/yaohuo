package com.townwang.yaohuo.repo.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserInfo(
    @PrimaryKey
    val touserid: String,
    var avatarUrl: String,
    val level: Int,
    var sex: String
) {
    override fun equals(other: Any?): Boolean {
        return other != null &&
                other is UserInfo &&
                avatarUrl == other.avatarUrl &&
                level == other.level &&
                touserid == other.touserid &&
                sex == other.sex
    }

    override fun hashCode(): Int {
        var result = touserid.hashCode()
        result = 31 * result + avatarUrl.hashCode()
        result = 31 * result + level.hashCode()
        result = 31 * result + sex.hashCode()
        return result
    }
}