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
                        applicationContext.defaultDataSourceFactory
                        /*
                        DefaultDataSource.Factory(
                            this,
                            OkHttpDataSource.Factory(okHttpClient())
                                .setUserAgent("Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Mobile Safari/537.36")
                        )
                        */
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
                    appContext().defaultDataSourceFactory
                    //OkHttpDataSource.Factory(okHttpClient())
                    //    .setUserAgent("Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Mobile Safari/537.36")
                    /*
                    DefaultHttpDataSource.Factory()
                        .setConnectTimeoutMs(16000)
                        .setReadTimeoutMs(8000)
                        .setUserAgent("Mozilla/5.0 (Windows NT 10.0; rv:91.0) Gecko/20100101 Firefox/91.0")

                     */
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
