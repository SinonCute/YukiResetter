package net.minevn.yukiresetter.`object`

data class ResetSchedule(
    val id: String,
    val serverId: String,
    val worldDisplayName: String,
    val worldName: String,
    val resetInterval: Long,
    var lastReset: Long,
    var nextReset: Long,
)
