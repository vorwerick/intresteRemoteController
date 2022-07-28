package cz.lumonos.intresteremote.service


class CancelGame() : Command() {

    override fun getPayload(): String {
        return ""
    }

    override fun getEndpoint(): String {
        return "cancel_game"
    }
}