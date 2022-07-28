package cz.lumonos.intresteremote.service

import com.beust.klaxon.Json


class ObjectGameConfig(
    @Json("timeout") val timeout: Int,
    @Json("hitPoints") val hitPoints: Int,
    @Json("missesPoints") val missesPoints: Int
)
