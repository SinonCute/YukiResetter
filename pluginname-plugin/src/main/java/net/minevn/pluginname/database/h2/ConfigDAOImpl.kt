package net.minevn.pluginname.database.h2

import net.minevn.pluginname.database.ConfigDAO

class ConfigDAOImpl : ConfigDAO() {

    override fun isTableExistsScript() =
        "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = '$tableName' and TABLE_SCHEMA = 'PUBLIC'"

    override fun getScript() = """SELECT * FROM "$tableName" WHERE "key" = ?"""

    override fun setScript() = """MERGE INTO "$tableName"("key", "value") KEY("key") VALUES (?, ?)"""

    override fun deleteScript() = """DELETE FROM "$tableName" WHERE "key" = ?"""
}
