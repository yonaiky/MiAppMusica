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

    val fadeOutRange: ClosedFloatingPointRange<Float>
        get() = when (this) {
        Disabled -> 0f..0f
        `11`, `12` -> 95.00f..95.50f
        `9`, `10` -> 96.00f..96.50f
        `7`, `8` -> 97.00f..97.50f
        `5`, `6` -> 98.00f..98.50f
        `3`, `4` -> 99.00f..99.50f
    }
}
