package cz.lumonos.intresteremote.service


class CancelSortPanels() : Command() {

    override fun getPayload(): String {
        return ""
    }

    override fun getEndpoint(): String {
        return "cancel_sort_panels"
    }
}