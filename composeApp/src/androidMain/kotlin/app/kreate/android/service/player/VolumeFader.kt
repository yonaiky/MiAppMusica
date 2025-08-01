package app.kreate.android.service.player

import android.animation.Animator
import android.animation.ValueAnimator
import androidx.annotation.MainThread
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.media3.exoplayer.ExoPlayer
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class VolumeFader(private val player: ExoPlayer) {

    private var volumeAnimator: ValueAnimator? = null

    /**
     * Starts a volume fade from a start volume to a target volume over a specified duration.
     * The fade uses a logarithmic curve for perceptual smoothness.
     *
     * @param start The volume to start the fade from (0.0f to 1.0f).
     * @param end The volume to fade to (0.0f to 1.0f).
     */
    @MainThread
    fun startFade(
        start: Float,
        end: Float,
        durationInMillis: Long,
        doOnStart: (Animator) -> Unit = {},
        doOnEnd: (Animator) -> Unit = {}
    ) {
        stopFade()

        with( ValueAnimator.ofFloat( 0f, 1f ) ) {
            volumeAnimator = this

            duration = durationInMillis
            addUpdateListener { animator ->
                val floor = min( start, end )
                val ceiling = max( start, end )

                player.volume = getVolumeForProgress(
                    linearProgress = animator.animatedValue as Float,
                    startVolume = start,
                    targetVolume = end
                ).coerceIn( floor, ceiling )
            }
            doOnStart( doOnStart )
            doOnEnd( doOnEnd )

            start()
        }
    }

    fun stopFade() {
        volumeAnimator?.cancel()
        volumeAnimator = null
    }

    /**
     * Calculates the perceptually scaled volume based on a linear progress.
     * This uses a logarithmic curve to make the fade sound more natural.
     *
     * @param linearProgress A linear value from 0.0f to 1.0f representing the fade progress.
     * @param startVolume The starting volume for the current fade.
     * @param targetVolume The target volume for the current fade.
     * @return The volume value (0.0f to 1.0f) to set on ExoPlayer.
     */
    private fun getVolumeForProgress(
        linearProgress: Float,
        startVolume: Float,
        targetVolume: Float
    ): Float {
        // Adjust this factor to change the steepness of the curve.
        // A common range is 2.0 to 4.0. Higher values make the lower end steeper.
        val curveFactor = 3.0f

        // Apply a power curve (logarithmic perception)
        // This formula maps a linear input (linearProgress) to a more perceptually linear output.
        val scaledProgress = if (linearProgress <= 0f) {
            0f
        } else if (linearProgress >= 1f) {
            1f
        } else {
            (10f.pow(curveFactor * linearProgress) - 1f) / (10f.pow(curveFactor) - 1f)
        }

        // Interpolate between start and target volume using the scaled progress
        return startVolume + (targetVolume - startVolume) * scaledProgress
    }

    fun release() = stopFade()
}