package cz.lumonos.intresteremote.service

abstract class Command() {

    abstract fun getPayload(): String

    abstract fun getEndpoint(): String

}