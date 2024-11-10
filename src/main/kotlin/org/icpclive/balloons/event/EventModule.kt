package org.icpclive.balloons.event

import org.icpclive.cds.cli.CdsCommandLineOptions
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun eventModule(cdsSettings: CdsCommandLineOptions) = module {
    singleOf(::EventStream)
    single { CDSFetcher(get(), cdsSettings) }
}