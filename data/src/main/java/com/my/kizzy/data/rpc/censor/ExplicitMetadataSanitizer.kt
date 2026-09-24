/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * ExplicitMetadataSanitizer.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.data.rpc.censor

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Censors explicit language in media metadata before it is shown on Discord.
 * Runs entirely on-device; the bundled lexicon is loaded once, on first use.
 */
@Singleton
class ExplicitMetadataSanitizer internal constructor(
    detectorProvider: () -> ExplicitLanguageDetector,
) {
    @Inject
    constructor() : this({ ExplicitLanguageDetector(ExplicitLexicon.bundled()) })

    private val detector by lazy(detectorProvider)

    fun sanitize(text: String): String =
        ExplicitTextMasker.mask(text, detector.detect(text))
}
