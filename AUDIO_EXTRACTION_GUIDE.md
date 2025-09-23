# 🎵 Guía de Extracción de Audio - MiAppMusica

Esta guía explica cómo usar el sistema de extracción de audio de **MiAppMusica** en tu propia aplicación Android.

## 📋 Tabla de Contenidos

- [Introducción](#introducción)
- [Instalación](#instalación)
- [Configuración](#configuración)
- [Uso Básico](#uso-básico)
- [APIs Disponibles](#apis-disponibles)
- [Ejemplos Prácticos](#ejemplos-prácticos)
- [Manejo de Errores](#manejo-de-errores)
- [Mejores Prácticas](#mejores-prácticas)

## 🚀 Introducción

**MiAppMusica** utiliza un sistema modular de extracción de audio que permite reproducir música de YouTube Music sin restricciones de suscripción premium. El sistema incluye:

- **Innertube API** - Para metadatos de YouTube Music
- **Piped/Invidious** - Para extracción de streams de audio
- **ExoPlayer** - Para reproducción de audio
- **Múltiples fuentes** - Para mayor confiabilidad

## 📦 Instalación

### 1. Agregar Dependencias

```kotlin
// build.gradle.kts (Module: app)
dependencies {
    // Innertube API
    implementation(project(":innertube"))
    
    // Piped API
    implementation(project(":piped"))
    
    // Invidious API
    implementation(project(":invidious"))
    
    // ExoPlayer
    implementation("androidx.media3:media3-exoplayer:1.8.0")
    implementation("androidx.media3:media3-session:1.8.0")
    
    // Ktor para HTTP
    implementation("io.ktor:ktor-client-okhttp:3.2.3")
    implementation("io.ktor:ktor-client-content-negotiation:3.2.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.2.3")
}
```

### 2. Configurar Permisos

```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />
```

## ⚙️ Configuración

### 1. Inicializar APIs

```kotlin
class AudioExtractor {
    init {
        // Inicializar instancias de Piped e Invidious
        lifecycleScope.launch {
            Piped.fetchInstances()
            Invidious.fetchInstances()
        }
    }
}
```

### 2. Configurar ExoPlayer

```kotlin
class AudioPlayer {
    private val exoPlayer = ExoPlayer.Builder(context)
        .setMediaSourceFactory(DefaultMediaSourceFactory(context))
        .build()
    
    fun playAudio(streamUrl: String) {
        val mediaItem = MediaItem.fromUri(streamUrl)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }
}
```

## 🎯 Uso Básico

### 1. Búsqueda de Música

```kotlin
suspend fun searchMusic(query: String): List<Song> {
    return try {
        val searchResponse = Innertube.search(query)
        searchResponse.songsPage?.items?.map { song ->
            Song(
                id = song.id,
                title = song.name,
                artist = song.artistsText,
                duration = song.durationText,
                thumbnail = song.thumbnails.firstOrNull()?.url
            )
        } ?: emptyList()
    } catch (e: Exception) {
        Log.e("AudioExtractor", "Error searching music", e)
        emptyList()
    }
}
```

### 2. Extracción de Stream

```kotlin
suspend fun getAudioStream(videoId: String): String? {
    return try {
        // Intentar con Piped primero
        Piped.getStream(videoId)?.audioStreams?.firstOrNull()?.url
            ?: // Fallback a Invidious
            Invidious.getStream(videoId)?.audioStreams?.firstOrNull()?.url
    } catch (e: Exception) {
        Log.e("AudioExtractor", "Error getting stream", e)
        null
    }
}
```

### 3. Reproducción Completa

```kotlin
class MusicPlayer {
    private val exoPlayer = ExoPlayer.Builder(context).build()
    
    suspend fun playSong(query: String) {
        // 1. Buscar canción
        val songs = searchMusic(query)
        if (songs.isEmpty()) return
        
        val song = songs.first()
        
        // 2. Obtener stream
        val streamUrl = getAudioStream(song.id)
        if (streamUrl == null) return
        
        // 3. Reproducir
        val mediaItem = MediaItem.Builder()
            .setUri(streamUrl)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(song.title)
                    .setArtist(song.artist)
                    .setArtworkUri(Uri.parse(song.thumbnail))
                    .build()
            )
            .build()
            
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }
}
```

## 🔌 APIs Disponibles

### Innertube API

```kotlin
// Búsqueda
val searchResults = Innertube.search("query")

// Información de canción
val song = Innertube.song(videoId)

// Información de playlist
val playlist = Innertube.playlist(playlistId)

// Información de artista
val artist = Innertube.artist(artistId)
```

### Piped API

```kotlin
// Obtener stream de audio
val stream = Piped.getStream(videoId)

// Obtener instancias disponibles
val instances = Piped.instances

// Buscar en Piped
val results = Piped.search(query)
```

### Invidious API

```kotlin
// Obtener stream de audio
val stream = Invidious.getStream(videoId)

// Obtener instancias disponibles
val instances = Invidious.instances
```

## 💡 Ejemplos Prácticos

### 1. Reproductor Simple

```kotlin
class SimpleMusicPlayer {
    private val exoPlayer = ExoPlayer.Builder(context).build()
    
    suspend fun playFromQuery(query: String) {
        try {
            // Buscar
            val songs = Innertube.search(query)
            if (songs.isEmpty()) return
            
            // Obtener stream
            val streamUrl = Piped.getStream(songs.first().id)?.audioStreams?.first()?.url
            if (streamUrl == null) return
            
            // Reproducir
            exoPlayer.setMediaItem(MediaItem.fromUri(streamUrl))
            exoPlayer.prepare()
            exoPlayer.play()
            
        } catch (e: Exception) {
            Log.e("MusicPlayer", "Error playing music", e)
        }
    }
}
```

### 2. Lista de Reproducción

```kotlin
class PlaylistManager {
    private val exoPlayer = ExoPlayer.Builder(context).build()
    private val playlist = mutableListOf<Song>()
    
    suspend fun addToPlaylist(query: String) {
        val songs = Innertube.search(query)
        playlist.addAll(songs)
    }
    
    suspend fun playNext() {
        if (playlist.isEmpty()) return
        
        val song = playlist.removeFirst()
        val streamUrl = getAudioStream(song.id)
        
        if (streamUrl != null) {
            exoPlayer.setMediaItem(MediaItem.fromUri(streamUrl))
            exoPlayer.prepare()
            exoPlayer.play()
        }
    }
}
```

### 3. Búsqueda Avanzada

```kotlin
class AdvancedSearch {
    suspend fun searchByArtist(artist: String): List<Song> {
        return Innertube.search("artist:$artist")
    }
    
    suspend fun searchByAlbum(album: String): List<Song> {
        return Innertube.search("album:$album")
    }
    
    suspend fun searchByYear(year: Int): List<Song> {
        return Innertube.search("year:$year")
    }
}
```

## ⚠️ Manejo de Errores

### 1. Errores de Red

```kotlin
suspend fun safeSearch(query: String): Result<List<Song>> {
    return try {
        val results = Innertube.search(query)
        Result.success(results)
    } catch (e: HttpRequestTimeoutException) {
        Result.failure(Exception("Timeout: ${e.message}"))
    } catch (e: Exception) {
        Result.failure(Exception("Search failed: ${e.message}"))
    }
}
```

### 2. Errores de Stream

```kotlin
suspend fun safeGetStream(videoId: String): String? {
    return try {
        Piped.getStream(videoId)?.audioStreams?.firstOrNull()?.url
    } catch (e: Exception) {
        Log.w("AudioExtractor", "Piped failed, trying Invidious")
        try {
            Invidious.getStream(videoId)?.audioStreams?.firstOrNull()?.url
        } catch (e2: Exception) {
            Log.e("AudioExtractor", "All streams failed", e2)
            null
        }
    }
}
```

## 🏆 Mejores Prácticas

### 1. Caché de Instancias

```kotlin
class InstanceManager {
    private var lastFetch = 0L
    private val CACHE_DURATION = 24 * 60 * 60 * 1000L // 24 horas
    
    suspend fun getInstances(): List<String> {
        if (System.currentTimeMillis() - lastFetch > CACHE_DURATION) {
            Piped.fetchInstances()
            Invidious.fetchInstances()
            lastFetch = System.currentTimeMillis()
        }
        return Piped.instances
    }
}
```

### 2. Manejo de Calidad

```kotlin
fun getBestAudioStream(streams: List<AudioStream>): AudioStream? {
    return streams
        .filter { it.quality != "unknown" }
        .maxByOrNull { 
            when (it.quality) {
                "high" -> 3
                "medium" -> 2
                "low" -> 1
                else -> 0
            }
        }
}
```

### 3. Limpieza de Recursos

```kotlin
class AudioExtractor : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cleanup() {
        exoPlayer.release()
        // Limpiar otros recursos
    }
}
```

## 🔧 Configuración Avanzada

### 1. Proxy y VPN

```kotlin
// Configurar proxy si es necesario
val httpClient = HttpClient(OkHttp) {
    engine {
        config {
            proxy(Proxy(Proxy.Type.HTTP, InetSocketAddress("proxy.example.com", 8080)))
        }
    }
}
```

### 2. Headers Personalizados

```kotlin
val httpClient = HttpClient(OkHttp) {
    defaultRequest {
        header("User-Agent", "MiAppMusica/1.0")
        header("Accept", "application/json")
    }
}
```

## 📚 Recursos Adicionales

- [Documentación de ExoPlayer](https://exoplayer.dev/)
- [Documentación de Ktor](https://ktor.io/)
- [Piped Instances](https://github.com/TeamPiped/Piped-Frontend/wiki/Instances)
- [Invidious Instances](https://api.invidious.io/)

## 🤝 Contribuir

Si encuentras bugs o quieres mejorar el sistema:

1. Fork el repositorio
2. Crea una rama para tu feature
3. Haz commit de tus cambios
4. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la licencia MIT. Ver [LICENSE](LICENSE) para más detalles.

---

**¡Disfruta creando tu reproductor de música! 🎵**
