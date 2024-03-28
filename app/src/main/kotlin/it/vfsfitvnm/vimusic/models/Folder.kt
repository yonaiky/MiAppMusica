package it.vfsfitvnm.vimusic.models

class Folder(
    val name: String,
    val parent: Folder? = null,
    val subFolders: MutableList<Folder> = mutableListOf(),
    val songs: MutableList<OnDeviceSong> = mutableListOf(),
    val fullPath: String = ""
) {
    fun addSubFolder(folder: Folder) {
        subFolders.add(folder)
    }

    fun addSong(song: OnDeviceSong) {
        songs.add(song)
    }
}
