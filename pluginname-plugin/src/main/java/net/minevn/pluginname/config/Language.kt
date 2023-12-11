package net.minevn.pluginname.config

class Language : FileConfig("messages") {

    val updateLatest = get("update-latest")
    val updateAvailable = get("update-available")
    val updateAvailableLink = get("update-available-link")
    val errorUnknown = get("error-unknown")

}
