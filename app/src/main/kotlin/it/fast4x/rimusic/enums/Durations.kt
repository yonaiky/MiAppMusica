package it.fast4x.rimusic.enums

import android.util.Range

enum class DurationInSeconds {
    Disabled,
    `3`,
    `4`,
    `5`,
    `6`,
    `7`,
    `8`,
    `9`,
    `10`,
    `11`,
    `12`;

    val seconds: Int
        get() = when (this) {
            Disabled -> 0
            `3` -> 3
            `4` -> 4
            `5` -> 5
            `6` -> 6
            `7` -> 7
            `8` -> 8
            `9` -> 9
            `10` -> 10
            `11` -> 11
            `12` -> 12
        } * 1000

    /*
    val fadeOutRange: ClosedRange<Int>
        get() = when (this) {
            Disabled -> 0..0
            `3` -> 3000..3100
            `4` -> 4000..4100
            `5` -> 5000..5100
            `6` -> 6000..6100
            `7` -> 7000..7100
            `8` -> 8000..8100
            `9` -> 9000..9100
            `10` -> 10000..10100
            `11` -> 11000..11100
            `12` -> 12000..12100
        }
     */

    val fadeOutRange: ClosedFloatingPointRange<Float>
        get() = when (this) {
        Disabled -> 0f..0f
        `11`, `12` -> 96.00f..96.20f
        `9`, `10` -> 97.00f..97.20f
        `7`, `8` -> 98.00f..98.20f
        `5`, `6` -> 98.21f..99.20f
        `3`, `4` -> 99.21f..99.99f
    }

    val fadeInRange: ClosedFloatingPointRange<Float>
        get() = 00.00f..00.00f

}
