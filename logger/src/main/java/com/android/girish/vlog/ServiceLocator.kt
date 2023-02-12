package com.android.girish.vlog

/*
*  A service locator to provide dependencies.
* */
internal object ServiceLocator {

    private var mVlogRepository: VlogRepository? = null
    private var mContentViewModel: ContentViewModel? = null

    fun provideContentViewModel(): ContentViewModel {
        synchronized(this) {
            if (mContentViewModel == null) {
                mContentViewModel = ContentViewModel(provideVlogRepository())
            }
            return mContentViewModel!!
        }
    }

    fun provideVlogRepository(): VlogRepository {
        synchronized(this) {
            if (mVlogRepository == null) {
                mVlogRepository = VlogRepository()
            }
            return mVlogRepository!!
        }
    }
}
