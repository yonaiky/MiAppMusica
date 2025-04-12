package it.fast4x.rimusic.service.modern

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.ResolvingDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.appContext
import it.fast4x.rimusic.utils.isConnectionMetered
import it.fast4x.rimusic.utils.okHttpDataSourceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.IOException

@OptIn(UnstableApi::class)
internal fun PlayerServiceModern.createDataSourceFactory(): DataSource.Factory {
    return ResolvingDataSource.Factory(
        CacheDataSource.Factory()
                       .setCache( downloadCache )
                       .setUpstreamDataSourceFactory(
                           CacheDataSource.Factory()
                                          .setCache( cache )
                                          .setUpstreamDataSourceFactory(
                                              appContext().okHttpDataSourceFactory
                                          )
                       )
                       .setCacheWriteDataSinkFactory( null )
                       .setFlags( FLAG_IGNORE_CACHE_ON_ERROR )
    ) { dataSpec: DataSpec ->
        try {
            runBlocking( Dispatchers.Main ) {
                player.currentMediaItem?.let {
                    Database.asyncTransaction {
                        insertIgnore( it )
                    }
                }
            }

            return@Factory runBlocking {
                dataSpecProcess( dataSpec, applicationContext, applicationContext.isConnectionMetered() )
            }
        }
        catch (e: Throwable) {
            println("PlayerService DataSourcefactory Error: ${e.message}")
            throw IOException(e)
        }
    }
}


