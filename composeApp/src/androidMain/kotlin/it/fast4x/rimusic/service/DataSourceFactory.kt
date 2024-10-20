package it.fast4x.rimusic.service

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.ResolvingDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
import it.fast4x.rimusic.utils.defaultDataSourceFactory
import it.fast4x.rimusic.utils.isConnectionMetered
import it.fast4x.rimusic.utils.okHttpDataSourceFactory
import kotlinx.coroutines.runBlocking
import me.knighthat.appContext
import java.io.IOException

@OptIn(UnstableApi::class)
internal fun PlayerService.createDataSourceFactory(): DataSource.Factory {
    return ResolvingDataSource.Factory(
        CacheDataSource.Factory()
            .setCache(downloadCache)
            .setUpstreamDataSourceFactory(
                CacheDataSource.Factory()
                    .setCache(cache)
                    .setUpstreamDataSourceFactory(
                        appContext().defaultDataSourceFactory
                        //appContext().okHttpDataSourceFactory
                    )
            )
            .setCacheWriteDataSinkFactory(null)
            .setFlags(FLAG_IGNORE_CACHE_ON_ERROR)
    ) { dataSpec: DataSpec ->
        try {
            return@Factory runBlocking {
                dataSpecProcess(dataSpec, applicationContext, applicationContext.isConnectionMetered())
                    /*
                    .also {
                    //loudnessEnhancer?.update(current_song, context)
                    }
                     */
            }
        }
        catch (e: Throwable) {
            println("PlayerService DataSourcefactory Error: ${e.message}")
            throw IOException(e)
        }
    }
}


@OptIn(UnstableApi::class)
internal fun MyDownloadHelper.createDataSourceFactory(): DataSource.Factory {
    return ResolvingDataSource.Factory(
        CacheDataSource.Factory()
            .setCache(getDownloadCache(appContext())).apply {
                setUpstreamDataSourceFactory(
                    //appContext().defaultDataSourceFactory
                    appContext().okHttpDataSourceFactory
                )
                setCacheWriteDataSinkFactory(null)
            }
    ) { dataSpec: DataSpec ->
        try {
            return@Factory runBlocking {
                dataSpecProcess(dataSpec, appContext(), appContext().isConnectionMetered())
                /*
                .also {
                //loudnessEnhancer?.update(current_song, context)
                }
                 */
            }
        }
        catch (e: Throwable) {
            println("MyDownloadHelper DataSourcefactory Error: ${e.message}")
            throw IOException(e)
        }
    }
}
