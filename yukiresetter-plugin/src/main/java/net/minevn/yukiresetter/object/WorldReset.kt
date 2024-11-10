package net.minevn.yukiresetter.`object`

data class WorldReset(
    val id: String,
    val worldDisplayName: String,
    val worldName: String,
    val worldBorderSize: Double,
    val resetInterval: Long,
)