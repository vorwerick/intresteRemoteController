package cz.lumonos.intresteremote.service


class GetCurrentGame() : Command() {

    override fun getPayload(): String {
        return ""
    }

    override fun getEndpoint(): String {
        return "get_current_game"
    }
}