/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * UploadGalleryImageUseCase.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.domain.use_case.upload_galleryImage

import android.content.Context
import android.net.Uri
import com.my.kizzy.domain.repository.KizzyRepository
import com.my.kizzy.utils.getFileName
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class UploadGalleryImageUseCase @Inject constructor(
    private val kizzyRepository: KizzyRepository,
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(uri: Uri): String? {
        return try {
            val filename = context.getFileName(uri)
            val file = filename?.let { File(context.cacheDir, it) }
            if (file != null) {
                val inputStream = context.contentResolver.openInputStream(uri)
                inputStream?.use { input ->
                    file.outputStream().use { out ->
                        input.copyTo(out)
                    }
                }
                file.deleteOnExit()
                kizzyRepository.uploadImage(file)
            } else
                null
        } catch (ex: Exception){
            null
        }
    }
}