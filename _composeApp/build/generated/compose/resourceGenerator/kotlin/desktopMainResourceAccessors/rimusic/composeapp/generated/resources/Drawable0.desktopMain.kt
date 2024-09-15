@file:OptIn(org.jetbrains.compose.resources.InternalResourceApi::class)

package rimusic.composeapp.generated.resources

import kotlin.OptIn
import org.jetbrains.compose.resources.DrawableResource

private object DesktopMainDrawable0 {
  public val icon: DrawableResource by 
      lazy { init_icon() }
}

public val Res.drawable.icon: DrawableResource
  get() = DesktopMainDrawable0.icon

private fun init_icon(): DrawableResource = org.jetbrains.compose.resources.DrawableResource(
  "drawable:icon",
    setOf(
      org.jetbrains.compose.resources.ResourceItem(setOf(),
    "composeResources/rimusic.composeapp.generated.resources/drawable/icon.svg", -1, -1),
    )
)
