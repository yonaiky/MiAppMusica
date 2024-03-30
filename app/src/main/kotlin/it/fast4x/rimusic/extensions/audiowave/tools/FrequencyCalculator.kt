import java.util.Arrays
import kotlin.experimental.and
import kotlin.experimental.or

class FrequencyCalculator(fftlen: Int) {

    private var spectrumAmpFFT: RealDoubleFFT? = null
    private var spectrumAmpOutCum: DoubleArray? = null
    private var spectrumAmpOutTmp: DoubleArray? = null
    private var spectrumAmpOut: DoubleArray? = null
    private var spectrumAmpOutDB: DoubleArray? = null
    private var spectrumAmpIn: DoubleArray? = null
    private var spectrumAmpInTmp: DoubleArray? = null
    private var wnd: DoubleArray? = null
    private var spectrumAmpOutArray: Array<DoubleArray>? = null

    private var fftLen: Int = 0
    private var spectrumAmpPt: Int = 0
    private var spectrumAmpOutArrayPt = 0
    private var nAnalysed = 0
    private var bytes: ByteArray? = null

    val freq: Double
        get() {
            if (nAnalysed != 0) {
                val outLen = spectrumAmpOut!!.size
                val sAOC = spectrumAmpOutCum
                for (j in 0 until outLen) {
                    sAOC?.let { it[j] /= nAnalysed.toDouble() }
                }
                System.arraycopy(sAOC!!, 0, spectrumAmpOut!!, 0, outLen)
                Arrays.fill(sAOC, 0.0)
                nAnalysed = 0
                for (i in 0 until outLen) {
                    spectrumAmpOutDB?.let { it[i] = 10.0 * Math.log10(spectrumAmpOut!![i]) }
                }
            }

            var maxAmpDB = 20 * Math.log10(0.125 / 32768)
            var maxAmpFreq = 0.0
            for (i in 1 until spectrumAmpOutDB!!.size) {
                if (spectrumAmpOutDB!![i] > maxAmpDB) {
                    maxAmpDB = spectrumAmpOutDB!![i]
                    maxAmpFreq = i.toDouble()
                }
            }
            val sampleRate = 44100
            maxAmpFreq = maxAmpFreq * sampleRate / fftLen
            if (sampleRate / fftLen < maxAmpFreq && maxAmpFreq < sampleRate / 2 - sampleRate / fftLen) {
                val id = Math.round(maxAmpFreq / sampleRate * fftLen).toInt()
                val x1 = spectrumAmpOutDB!![id - 1]
                val x2 = spectrumAmpOutDB!![id]
                val x3 = spectrumAmpOutDB!![id + 1]
                val a = (x3 + x1) / 2 - x2
                val b = (x3 - x1) / 2
                if (a < 0) {
                    val xPeak = -b / (2 * a)
                    if (Math.abs(xPeak) < 1) {
                        maxAmpFreq += xPeak * sampleRate / fftLen
                    }
                }
            }
            return maxAmpFreq
        }

    init {
        init(fftlen)
    }

    private fun init(fftlen: Int) {
        fftLen = fftlen
        spectrumAmpOutCum = DoubleArray(fftlen)
        spectrumAmpOutTmp = DoubleArray(fftlen)
        spectrumAmpOut = DoubleArray(fftlen)
        spectrumAmpOutDB = DoubleArray(fftlen)
        spectrumAmpIn = DoubleArray(fftlen)
        spectrumAmpInTmp = DoubleArray(fftlen)
        spectrumAmpFFT = RealDoubleFFT(fftlen)
        spectrumAmpOutArray = arrayOf(doubleArrayOf(Math.ceil(1.toDouble() / fftlen)))

        for (i in spectrumAmpOutArray!!.indices) {
            spectrumAmpOutArray?.let { it[i] = DoubleArray(fftlen) }
        }
        wnd = DoubleArray(fftlen)
        for (i in wnd!!.indices) {
            wnd?.let { it[i] = Math.asin(Math.sin(Math.PI * i / wnd!!.size)) / Math.PI * 2 }
        }
    }

    private fun getShortFromBytes(index: Int): Short {
        var index = index
        index *= 2
        var buff = bytes!![index + 1].toShort()
        var buff2 = bytes!![index].toShort()

        buff = (buff.toInt() and 0xFF shl 8).toShort()
        buff2 = (buff2 and 0xFF)

        return (buff or buff2)
    }

    fun feedData(ds: ByteArray, dsLen: Int) {
        bytes = ds
        var dsPt = 0
        while (dsPt < dsLen) {
            while (spectrumAmpPt < fftLen && dsPt < dsLen) {
                val s = getShortFromBytes(dsPt++) / 32768.0
                spectrumAmpIn?.let { it[spectrumAmpPt++] = s }
            }
            if (spectrumAmpPt == fftLen) {
                for (i in 0 until fftLen) {
                    spectrumAmpInTmp?.let { it[i] = spectrumAmpIn!![i] * wnd!![i] }
                }
                spectrumAmpFFT!!.ft(spectrumAmpInTmp!!)
                fftToAmp(spectrumAmpOutTmp!!, spectrumAmpInTmp!!)
                System.arraycopy(spectrumAmpOutTmp!!, 0, spectrumAmpOutArray!![spectrumAmpOutArrayPt], 0, spectrumAmpOutTmp!!.size)
                spectrumAmpOutArrayPt = (spectrumAmpOutArrayPt + 1) % spectrumAmpOutArray!!.size
                for (i in 0 until fftLen) {
                    spectrumAmpOutCum?.let { it[i] += spectrumAmpOutTmp!![i] }
                }
                nAnalysed++
                val n2 = spectrumAmpIn!!.size / 2
                System.arraycopy(spectrumAmpIn!!, n2, spectrumAmpIn!!, 0, n2)
                spectrumAmpPt = n2
            }
        }
    }

    private fun fftToAmp(dataOut: DoubleArray, data: DoubleArray) {
        val scalar = 2.0 * 2.0 / (data.size * data.size)
        dataOut[0] = data[0] * data[0] * scalar / 4.0
        var j = 1
        var i = 1
        while (i < data.size - 1) {
            dataOut[j] = (data[i] * data[i] + data[i + 1] * data[i + 1]) * scalar
            i += 2
            j++
        }
        dataOut[j] = data[data.size - 1] * data[data.size - 1] * scalar / 4.0
    }
}