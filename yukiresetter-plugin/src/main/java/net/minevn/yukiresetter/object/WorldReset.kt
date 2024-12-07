package net.minevn.yukiresetter.`object`

data class WorldReset(
    val id: String,
    val serverId: String,
    var worldDisplayName: String,
    val worldName: String,
    val worldBorderSize: Double,
    val resetInterval: Long,
    var lastReset: Long,
    var nextReset: Long,
)