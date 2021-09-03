package com.wakeup.hyperion.di

import android.app.Application
import com.wakeup.hyperion.data.local.sharedpfers.SharedPrefsImpl
import com.wakeup.hyperion.data.repository.DefaultSharedPrefsRepository
import com.wakeup.hyperion.data.repository.SharedPrefsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Copyright © 2020 Neolab VN.
 * Created by ThuanPx on 8/7/20.
 */

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideSharedPrefsRepository(app: Application): SharedPrefsRepository {
        return DefaultSharedPrefsRepository(SharedPrefsImpl(app))
    }
}
