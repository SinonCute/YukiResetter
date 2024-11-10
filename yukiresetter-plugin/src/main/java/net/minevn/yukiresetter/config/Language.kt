package net.minevn.yukiresetter.config

class Language : FileConfig("messages") {
    val errorUnknown = get("error-unknown")

    val resetChatMessage = get("reset-chat-message")
    val resetCompleteChatMessage = get("reset-complete-chat-message")
    val warningChatMessage = get("warning-chat-message")
    val warningTitle = get("warning-title")
    val warningSubtitle = get("warning-subtitle")
    val teleportingMessage = get("teleporting-message")
}
