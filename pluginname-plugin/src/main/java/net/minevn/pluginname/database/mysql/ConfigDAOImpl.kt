package net.minevn.pluginname.database.mysql

import net.minevn.pluginname.database.ConfigDAO

class ConfigDAOImpl : ConfigDAO() {
    override fun isTableExistsScript() = "SHOW TABLES LIKE '$tableName'"

    override fun getScript() = "SELECT * FROM $tableName WHERE `key` = ?"

    override fun setScript() =
        "INSERT INTO $tableName (`key`, `value`) VALUES (?, @value:=?) ON DUPLICATE KEY UPDATE `value` = @value"

    override fun deleteScript() = "DELETE FROM x$tableName WHERE `key` = ?"
}
