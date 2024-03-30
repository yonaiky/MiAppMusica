package it.fast4x.rimusic.enums

enum class HomeScreenTabs {
    Default,
    QuickPics,
    Songs,
    Artists,
    Albums,
    Library;
   // Discovery;



    val index: Int
        get() = when (this) {
            Default -> 100
            QuickPics -> 0
            Songs -> 1
            Artists -> 2
            Albums -> 3
            Library -> 4
            //Discovery -> 5

        }

}