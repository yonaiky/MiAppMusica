package it.fast4x.rimusic.service

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.ResolvingDataSource
import androidx.media3.datasource.cache.CacheDataSource
import it.fast4x.rimusic.appContext
import it.fast4x.rimusic.utils.isConnectionMetered
import it.fast4x.rimusic.utils.okHttpDataSourceFactory
import kotlinx.coroutines.runBlocking
import java.io.IOException

@OptIn(UnstableApi::class)
internal fun MyDownloadHelper.createDataSourceFactory(): DataSource.Factory {
    return ResolvingDataSource.Factory(
        CacheDataSource.Factory()
            .setCache(getDownloadCache(appContext())).apply {
                setUpstreamDataSourceFactory(
                    appContext().okHttpDataSourceFactory
                )
                setCacheWriteDataSinkFactory(null)
            }
    ) { dataSpec: DataSpec ->
        try {

            return@Factory runBlocking {
                dataSpecProcess(dataSpec, appContext(), appContext().isConnectionMetered())
            }
        }
        catch (e: Throwable) {
            println("MyDownloadHelper DataSourcefactory Error: ${e.stackTraceToString()}")
            throw IOException(e)
        }
    }
}
