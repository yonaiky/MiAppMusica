package it.vfsfitvnm.vimusic.extensions.audiowave.model

data class AudioWaveformUiState(
    val audioDisplayName: String = "",
    val amplitudes: List<Int> = emptyList(),
    val isPlaying: Boolean = false,
    val progress: Float = 0F
)