package me.knighthat.component.tab.toolbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import it.fast4x.rimusic.R
import me.knighthat.component.header.TabToolBar
import kotlin.random.Random

interface Randomizer<T>: Button {

    companion object {
        /**
         * To ensure true randomness, Random must not
         * be a constant variable
         */
        fun nextInt( until: Int ) =
            Random( System.currentTimeMillis() ).nextInt( until )
    }

    val itemsState: MutableState<List<T>>

    fun onClick( item: T )

    @Composable
    override fun ToolBarButton() {
        val items by itemsState

        TabToolBar.Icon(
            iconId = R.drawable.dice,
            enabled = items.isNotEmpty()
        ) {
            onClick( items[nextInt( items.size )] )
        }
    }
}