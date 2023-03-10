package com.wakeup.hyperion.di

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.wakeup.hyperion.data.local.sharedpfers.SharedPrefs
import com.wakeup.hyperion.data.local.sharedpfers.SharedPrefsImpl
import com.wakeup.hyperion.data.remote.api.middleware.BooleanAdapter
import com.wakeup.hyperion.data.remote.api.middleware.DoubleAdapter
import com.wakeup.hyperion.data.remote.api.middleware.IntegerAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

/**
 * Copyright © 2020 Neolab VN.
 * Created by ThuanPx on 8/7/20.
 */

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideGson(): Gson {
        val booleanAdapter = BooleanAdapter()
        val integerAdapter = IntegerAdapter()
        val doubleAdapter = DoubleAdapter()
        return GsonBuilder()
            .registerTypeAdapter(Boolean::class.java, booleanAdapter)
            .registerTypeAdapter(Int::class.java, integerAdapter)
            .registerTypeAdapter(Double::class.java, doubleAdapter)
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
    }

    @Singleton
    @Provides
    fun provideSharedPrefsApi(app: Application): SharedPrefs {
        return SharedPrefsImpl(app)
    }

    @OptIn(ExperimentalTime::class)
    @Provides
    @Singleton
    fun timeSource(): TimeSource = TimeSource.Monotonic

    @Provides
    @Singleton
    fun activity(): AppCompatActivity = AppCompatActivity()
}

