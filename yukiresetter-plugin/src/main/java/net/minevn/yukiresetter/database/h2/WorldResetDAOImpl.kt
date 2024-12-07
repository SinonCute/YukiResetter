package net.minevn.yukiresetter.database.h2

import net.minevn.yukiresetter.database.WorldResetDAO

class WorldResetDAOImpl : WorldResetDAO() {
    override fun isTableExistsScript(): String {
        return "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = '$tableName' and TABLE_SCHEMA = 'PUBLIC'"
    }

    override fun createTableScript(): String {
        return """
        CREATE TABLE IF NOT EXISTS "$tableName" (
            "id" VARCHAR(36) NOT NULL,
            "serverId" VARCHAR(36) NOT NULL,
            "worldDisplayName" VARCHAR(255) NOT NULL,
            "worldName" VARCHAR(255) NOT NULL,
            "worldBorderSize" DOUBLE NOT NULL,
            "resetInterval" BIGINT NOT NULL,
            "lastReset" BIGINT NOT NULL,
            "nextReset" BIGINT NOT NULL,
            PRIMARY KEY ("id")
        )
    """
    }

    override fun getScript(): String {
        return """SELECT * FROM "$tableName" WHERE "id" = ?"""
    }

    override fun getScriptAll(): String {
        return """SELECT * FROM "$tableName""""
    }

    override fun setScript(): String {
        return """
        MERGE INTO "$tableName" (
            "id", "serverId", "worldDisplayName", "worldName", "worldBorderSize", "resetInterval", "lastReset", "nextReset"
        )
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """
    }


    override fun deleteScript(): String {
        return """DELETE FROM "$tableName" WHERE "id" = ?"""
    }

    override fun deleteScriptIfNotExists(worldNamesCount: Int): String {
        val placeholders = List(worldNamesCount) { "?" }.joinToString(", ")
        return """DELETE FROM "$tableName" WHERE "serverId" = ? AND "worldName" NOT IN ($placeholders)"""
    }
}