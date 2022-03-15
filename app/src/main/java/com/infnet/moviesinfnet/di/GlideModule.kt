package com.infnet.moviesinfnet.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.infnet.moviesinfnet.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object GlideModule
{
    @Provides
    @Singleton
    fun provideGlide(
        @ApplicationContext context: Context
    ): RequestManager =
            Glide.with(context).setDefaultRequestOptions(
                RequestOptions()
                    .error(R.drawable.splash_image)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            )

}