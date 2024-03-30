class RealDoubleFFT(private val ndim: Int) {

    var normFactor: Double = 0.toDouble()
    private var wavetable: DoubleArray? = null
    private val ch: DoubleArray    // reusable work array

    companion object {
        internal val ntryh = intArrayOf(4, 2, 3, 5)
    }
    
    init {
        normFactor = ndim.toDouble()
        if (wavetable == null || wavetable!!.size != 2 * ndim + 15) {
            wavetable = DoubleArray(2 * ndim + 15)
        }
        wavetable?.let {
            rffti(ndim, it)
        }
        ch = DoubleArray(ndim)
    }


    ///////////////////////////////////// Mixed ////////////////////////////////////

    fun ft(x: DoubleArray) {
        if (x.size != ndim)
            throw IllegalArgumentException("The length of data can not match that of the wavetable")
        rfftf(ndim, x, wavetable, ch)
    }

    private fun radf2(
        ido: Int, l1: Int, cc: DoubleArray, ch: DoubleArray,
        wtable: DoubleArray, offset: Int
    ) {
        var i: Int
        var k: Int
        var ic: Int
        var ti2: Double
        var tr2: Double
        val iw1: Int = offset

        k = 0
        while (k < l1) {
            ch[2 * k * ido] = cc[k * ido] + cc[(k + l1) * ido]
            ch[(2 * k + 1) * ido + ido - 1] = cc[k * ido] - cc[(k + l1) * ido]
            k++
        }
        if (ido < 2) return
        if (ido != 2) {
            k = 0
            while (k < l1) {
                i = 2
                while (i < ido) {
                    ic = ido - i
                    tr2 =
                        wtable[i - 2 + iw1] * cc[i - 1 + (k + l1) * ido] + wtable[i - 1 + iw1] * cc[i + (k + l1) * ido]
                    ti2 =
                        wtable[i - 2 + iw1] * cc[i + (k + l1) * ido] - wtable[i - 1 + iw1] * cc[i - 1 + (k + l1) * ido]
                    ch[i + 2 * k * ido] = cc[i + k * ido] + ti2
                    ch[ic + (2 * k + 1) * ido] = ti2 - cc[i + k * ido]
                    ch[i - 1 + 2 * k * ido] = cc[i - 1 + k * ido] + tr2
                    ch[ic - 1 + (2 * k + 1) * ido] = cc[i - 1 + k * ido] - tr2
                    i += 2
                }
                k++
            }
            if (ido % 2 == 1) return
        }
        k = 0
        while (k < l1) {
            ch[(2 * k + 1) * ido] = -cc[ido - 1 + (k + l1) * ido]
            ch[ido - 1 + 2 * k * ido] = cc[ido - 1 + k * ido]
            k++
        }
    }

    private fun radf3(
        ido: Int, l1: Int, cc: DoubleArray, ch: DoubleArray,
        wtable: DoubleArray, offset: Int
    ) {
        val taur = -0.5
        val taui = 0.866025403784439
        var i: Int
        var k: Int = 0
        var ic: Int
        var ci2: Double
        var di2: Double
        var di3: Double
        var cr2: Double
        var dr2: Double
        var dr3: Double
        var ti2: Double
        var ti3: Double
        var tr2: Double
        var tr3: Double
        val iw1: Int = offset
        val iw2: Int
        iw2 = iw1 + ido

        while (k < l1) {
            cr2 = cc[(k + l1) * ido] + cc[(k + 2 * l1) * ido]
            ch[3 * k * ido] = cc[k * ido] + cr2
            ch[(3 * k + 2) * ido] = taui * (cc[(k + l1 * 2) * ido] - cc[(k + l1) * ido])
            ch[ido - 1 + (3 * k + 1) * ido] = cc[k * ido] + taur * cr2
            k++
        }
        if (ido == 1) return
        k = 0
        while (k < l1) {
            i = 2
            while (i < ido) {
                ic = ido - i
                dr2 = wtable[i - 2 + iw1] * cc[i - 1 + (k + l1) * ido] + wtable[i - 1 + iw1] * cc[i + (k + l1) * ido]
                di2 = wtable[i - 2 + iw1] * cc[i + (k + l1) * ido] - wtable[i - 1 + iw1] * cc[i - 1 + (k + l1) * ido]
                dr3 =
                    wtable[i - 2 + iw2] * cc[i - 1 + (k + l1 * 2) * ido] + wtable[i - 1 + iw2] * cc[i + (k + l1 * 2) * ido]
                di3 =
                    wtable[i - 2 + iw2] * cc[i + (k + l1 * 2) * ido] - wtable[i - 1 + iw2] * cc[i - 1 + (k + l1 * 2) * ido]
                cr2 = dr2 + dr3
                ci2 = di2 + di3
                ch[i - 1 + 3 * k * ido] = cc[i - 1 + k * ido] + cr2
                ch[i + 3 * k * ido] = cc[i + k * ido] + ci2
                tr2 = cc[i - 1 + k * ido] + taur * cr2
                ti2 = cc[i + k * ido] + taur * ci2
                tr3 = taui * (di2 - di3)
                ti3 = taui * (dr3 - dr2)
                ch[i - 1 + (3 * k + 2) * ido] = tr2 + tr3
                ch[ic - 1 + (3 * k + 1) * ido] = tr2 - tr3
                ch[i + (3 * k + 2) * ido] = ti2 + ti3
                ch[ic + (3 * k + 1) * ido] = ti3 - ti2
                i += 2
            }
            k++
        }
    }

    private fun radf4(ido: Int, l1: Int, cc: DoubleArray, ch: DoubleArray, wtable: DoubleArray, offset: Int) {
        val hsqt2 = 0.7071067811865475
        var i: Int
        var k: Int
        var ic: Int
        var ci2: Double
        var ci3: Double
        var ci4: Double
        var cr2: Double
        var cr3: Double
        var cr4: Double
        var ti1: Double
        var ti2: Double
        var ti3: Double
        var ti4: Double
        var tr1: Double
        var tr2: Double
        var tr3: Double
        var tr4: Double
        val iw1: Int = offset
        val iw2: Int = offset + ido
        val iw3: Int
        iw3 = iw2 + ido
        k = 0
        while (k < l1) {
            tr1 = cc[(k + l1) * ido] + cc[(k + 3 * l1) * ido]
            tr2 = cc[k * ido] + cc[(k + 2 * l1) * ido]
            ch[4 * k * ido] = tr1 + tr2
            ch[ido - 1 + (4 * k + 3) * ido] = tr2 - tr1
            ch[ido - 1 + (4 * k + 1) * ido] = cc[k * ido] - cc[(k + 2 * l1) * ido]
            ch[(4 * k + 2) * ido] = cc[(k + 3 * l1) * ido] - cc[(k + l1) * ido]
            k++
        }
        if (ido < 2) return
        if (ido != 2) {
            k = 0
            while (k < l1) {
                i = 2
                while (i < ido) {
                    ic = ido - i
                    cr2 =
                        wtable[i - 2 + iw1] * cc[i - 1 + (k + l1) * ido] + wtable[i - 1 + iw1] * cc[i + (k + l1) * ido]
                    ci2 =
                        wtable[i - 2 + iw1] * cc[i + (k + l1) * ido] - wtable[i - 1 + iw1] * cc[i - 1 + (k + l1) * ido]
                    cr3 =
                        wtable[i - 2 + iw2] * cc[i - 1 + (k + 2 * l1) * ido] + wtable[i - 1 + iw2] * cc[i + (k + 2 * l1) * ido]
                    ci3 =
                        wtable[i - 2 + iw2] * cc[i + (k + 2 * l1) * ido] - wtable[i - 1 + iw2] * cc[i - 1 + (k + 2 * l1) * ido]
                    cr4 =
                        wtable[i - 2 + iw3] * cc[i - 1 + (k + 3 * l1) * ido] + wtable[i - 1 + iw3] * cc[i + (k + 3 * l1) * ido]
                    ci4 =
                        wtable[i - 2 + iw3] * cc[i + (k + 3 * l1) * ido] - wtable[i - 1 + iw3] * cc[i - 1 + (k + 3 * l1) * ido]
                    tr1 = cr2 + cr4
                    tr4 = cr4 - cr2
                    ti1 = ci2 + ci4
                    ti4 = ci2 - ci4
                    ti2 = cc[i + k * ido] + ci3
                    ti3 = cc[i + k * ido] - ci3
                    tr2 = cc[i - 1 + k * ido] + cr3
                    tr3 = cc[i - 1 + k * ido] - cr3
                    ch[i - 1 + 4 * k * ido] = tr1 + tr2
                    ch[ic - 1 + (4 * k + 3) * ido] = tr2 - tr1
                    ch[i + 4 * k * ido] = ti1 + ti2
                    ch[ic + (4 * k + 3) * ido] = ti1 - ti2
                    ch[i - 1 + (4 * k + 2) * ido] = ti4 + tr3
                    ch[ic - 1 + (4 * k + 1) * ido] = tr3 - ti4
                    ch[i + (4 * k + 2) * ido] = tr4 + ti3
                    ch[ic + (4 * k + 1) * ido] = tr4 - ti3
                    i += 2
                }
                k++
            }
            if (ido % 2 == 1) return
        }
        k = 0
        while (k < l1) {
            ti1 = -hsqt2 * (cc[ido - 1 + (k + l1) * ido] + cc[ido - 1 + (k + 3 * l1) * ido])
            tr1 = hsqt2 * (cc[ido - 1 + (k + l1) * ido] - cc[ido - 1 + (k + 3 * l1) * ido])
            ch[ido - 1 + 4 * k * ido] = tr1 + cc[ido - 1 + k * ido]
            ch[ido - 1 + (4 * k + 2) * ido] = cc[ido - 1 + k * ido] - tr1
            ch[(4 * k + 1) * ido] = ti1 - cc[ido - 1 + (k + 2 * l1) * ido]
            ch[(4 * k + 3) * ido] = ti1 + cc[ido - 1 + (k + 2 * l1) * ido]
            k++
        }
    }

    private fun radf5(ido: Int, l1: Int, cc: DoubleArray, ch: DoubleArray, wtable: DoubleArray, offset: Int) {
        val tr11 = 0.309016994374947
        val ti11 = 0.951056516295154
        val tr12 = -0.809016994374947
        val ti12 = 0.587785252292473
        var i: Int
        var k: Int = 0
        var ic: Int
        var ci2: Double
        var di2: Double
        var ci4: Double
        var ci5: Double
        var di3: Double
        var di4: Double
        var di5: Double
        var ci3: Double
        var cr2: Double
        var cr3: Double
        var dr2: Double
        var dr3: Double
        var dr4: Double
        var dr5: Double
        var cr5: Double
        var cr4: Double
        var ti2: Double
        var ti3: Double
        var ti5: Double
        var ti4: Double
        var tr2: Double
        var tr3: Double
        var tr4: Double
        var tr5: Double
        val iw1: Int = offset
        val iw2: Int
        val iw3: Int
        val iw4: Int
        iw2 = iw1 + ido
        iw3 = iw2 + ido
        iw4 = iw3 + ido

        while (k < l1) {
            cr2 = cc[(k + 4 * l1) * ido] + cc[(k + l1) * ido]
            ci5 = cc[(k + 4 * l1) * ido] - cc[(k + l1) * ido]
            cr3 = cc[(k + 3 * l1) * ido] + cc[(k + 2 * l1) * ido]
            ci4 = cc[(k + 3 * l1) * ido] - cc[(k + 2 * l1) * ido]
            ch[5 * k * ido] = cc[k * ido] + cr2 + cr3
            ch[ido - 1 + (5 * k + 1) * ido] = cc[k * ido] + tr11 * cr2 + tr12 * cr3
            ch[(5 * k + 2) * ido] = ti11 * ci5 + ti12 * ci4
            ch[ido - 1 + (5 * k + 3) * ido] = cc[k * ido] + tr12 * cr2 + tr11 * cr3
            ch[(5 * k + 4) * ido] = ti12 * ci5 - ti11 * ci4
            k++
        }
        if (ido == 1) return
        k = 0
        while (k < l1) {
            i = 2
            while (i < ido) {
                ic = ido - i
                dr2 = wtable[i - 2 + iw1] * cc[i - 1 + (k + l1) * ido] + wtable[i - 1 + iw1] * cc[i + (k + l1) * ido]
                di2 = wtable[i - 2 + iw1] * cc[i + (k + l1) * ido] - wtable[i - 1 + iw1] * cc[i - 1 + (k + l1) * ido]
                dr3 =
                    wtable[i - 2 + iw2] * cc[i - 1 + (k + 2 * l1) * ido] + wtable[i - 1 + iw2] * cc[i + (k + 2 * l1) * ido]
                di3 =
                    wtable[i - 2 + iw2] * cc[i + (k + 2 * l1) * ido] - wtable[i - 1 + iw2] * cc[i - 1 + (k + 2 * l1) * ido]
                dr4 =
                    wtable[i - 2 + iw3] * cc[i - 1 + (k + 3 * l1) * ido] + wtable[i - 1 + iw3] * cc[i + (k + 3 * l1) * ido]
                di4 =
                    wtable[i - 2 + iw3] * cc[i + (k + 3 * l1) * ido] - wtable[i - 1 + iw3] * cc[i - 1 + (k + 3 * l1) * ido]
                dr5 =
                    wtable[i - 2 + iw4] * cc[i - 1 + (k + 4 * l1) * ido] + wtable[i - 1 + iw4] * cc[i + (k + 4 * l1) * ido]
                di5 =
                    wtable[i - 2 + iw4] * cc[i + (k + 4 * l1) * ido] - wtable[i - 1 + iw4] * cc[i - 1 + (k + 4 * l1) * ido]
                cr2 = dr2 + dr5
                ci5 = dr5 - dr2
                cr5 = di2 - di5
                ci2 = di2 + di5
                cr3 = dr3 + dr4
                ci4 = dr4 - dr3
                cr4 = di3 - di4
                ci3 = di3 + di4
                ch[i - 1 + 5 * k * ido] = cc[i - 1 + k * ido] + cr2 + cr3
                ch[i + 5 * k * ido] = cc[i + k * ido] + ci2 + ci3
                tr2 = cc[i - 1 + k * ido] + tr11 * cr2 + tr12 * cr3
                ti2 = cc[i + k * ido] + tr11 * ci2 + tr12 * ci3
                tr3 = cc[i - 1 + k * ido] + tr12 * cr2 + tr11 * cr3
                ti3 = cc[i + k * ido] + tr12 * ci2 + tr11 * ci3
                tr5 = ti11 * cr5 + ti12 * cr4
                ti5 = ti11 * ci5 + ti12 * ci4
                tr4 = ti12 * cr5 - ti11 * cr4
                ti4 = ti12 * ci5 - ti11 * ci4
                ch[i - 1 + (5 * k + 2) * ido] = tr2 + tr5
                ch[ic - 1 + (5 * k + 1) * ido] = tr2 - tr5
                ch[i + (5 * k + 2) * ido] = ti2 + ti5
                ch[ic + (5 * k + 1) * ido] = ti5 - ti2
                ch[i - 1 + (5 * k + 4) * ido] = tr3 + tr4
                ch[ic - 1 + (5 * k + 3) * ido] = tr3 - tr4
                ch[i + (5 * k + 4) * ido] = ti3 + ti4
                ch[ic + (5 * k + 3) * ido] = ti4 - ti3
                i += 2
            }
            ++k
        }
    }

    private fun radfg(
        ido: Int,
        ip: Int,
        l1: Int,
        idl1: Int,
        cc: DoubleArray,
        c1: DoubleArray,
        c2: DoubleArray,
        ch: DoubleArray,
        ch2: DoubleArray,
        wtable: DoubleArray,
        offset: Int
    ) {
        val twopi = 2.0 * Math.PI //6.28318530717959;
        var idij: Int
        val ipph: Int = (ip + 1) / 2
        var i: Int
        var j: Int
        var k: Int
        var l = 1
        var j2: Int
        var ic: Int
        var jc: Int
        var lc: Int
        var ik: Int
        var `is`: Int
        val nbd: Int = (ido - 1) / 2
        var dc2: Double
        var ai1: Double
        var ai2: Double
        var ar1: Double
        var ar2: Double
        var ds2: Double
        val dcp: Double
        val arg: Double
        val dsp: Double
        var ar1h: Double
        var ar2h: Double

        arg = twopi / ip
        dcp = Math.cos(arg)
        dsp = Math.sin(arg)
        if (ido != 1) {
            ik = 0
            while (ik < idl1) {
                ch2[ik] = c2[ik]
                ik++
            }
            j = 1
            while (j < ip) {
                run {
                    k = 0
                    while (k < l1) {
                        ch[(k + j * l1) * ido] = c1[(k + j * l1) * ido]
                        k++
                    }
                }
                j++
            }
            if (nbd <= l1) {
                `is` = -ido
                j = 1
                while (j < ip) {
                    `is` += ido
                    idij = `is` - 1
                    i = 2
                    while (i < ido) {
                        idij += 2
                        k = 0
                        while (k < l1) {
                            ch[i - 1 + (k + j * l1) * ido] =
                                wtable[idij - 1 + offset] * c1[i - 1 + (k + j * l1) * ido] + wtable[idij + offset] * c1[i + (k + j * l1) * ido]
                            ch[i + (k + j * l1) * ido] =
                                wtable[idij - 1 + offset] * c1[i + (k + j * l1) * ido] - wtable[idij + offset] * c1[i - 1 + (k + j * l1) * ido]
                            k++
                        }
                        i += 2
                    }
                    j++
                }
            } else {
                `is` = -ido
                j = 1
                while (j < ip) {
                    `is` += ido
                    k = 0
                    while (k < l1) {
                        idij = `is` - 1
                        i = 2
                        while (i < ido) {
                            idij += 2
                            ch[i - 1 + (k + j * l1) * ido] =
                                wtable[idij - 1 + offset] * c1[i - 1 + (k + j * l1) * ido] + wtable[idij + offset] * c1[i + (k + j * l1) * ido]
                            ch[i + (k + j * l1) * ido] =
                                wtable[idij - 1 + offset] * c1[i + (k + j * l1) * ido] - wtable[idij + offset] * c1[i - 1 + (k + j * l1) * ido]
                            i += 2
                        }
                        k++
                    }
                    j++
                }
            }
            if (nbd >= l1) {
                j = 1
                while (j < ipph) {
                    jc = ip - j
                    k = 0
                    while (k < l1) {
                        i = 2
                        while (i < ido) {
                            c1[i - 1 + (k + j * l1) * ido] =
                                ch[i - 1 + (k + j * l1) * ido] + ch[i - 1 + (k + jc * l1) * ido]
                            c1[i - 1 + (k + jc * l1) * ido] = ch[i + (k + j * l1) * ido] - ch[i + (k + jc * l1) * ido]
                            c1[i + (k + j * l1) * ido] = ch[i + (k + j * l1) * ido] + ch[i + (k + jc * l1) * ido]
                            c1[i + (k + jc * l1) * ido] =
                                ch[i - 1 + (k + jc * l1) * ido] - ch[i - 1 + (k + j * l1) * ido]
                            i += 2
                        }
                        k++
                    }
                    j++
                }
            } else {
                j = 1
                while (j < ipph) {
                    jc = ip - j
                    i = 2
                    while (i < ido) {
                        k = 0
                        while (k < l1) {
                            c1[i - 1 + (k + j * l1) * ido] =
                                ch[i - 1 + (k + j * l1) * ido] + ch[i - 1 + (k + jc * l1) * ido]
                            c1[i - 1 + (k + jc * l1) * ido] = ch[i + (k + j * l1) * ido] - ch[i + (k + jc * l1) * ido]
                            c1[i + (k + j * l1) * ido] = ch[i + (k + j * l1) * ido] + ch[i + (k + jc * l1) * ido]
                            c1[i + (k + jc * l1) * ido] =
                                ch[i - 1 + (k + jc * l1) * ido] - ch[i - 1 + (k + j * l1) * ido]
                            k++
                        }
                        i += 2
                    }
                    j++
                }
            }
        } else {
            ik = 0
            while (ik < idl1) {
                c2[ik] = ch2[ik]
                ik++
            }
        }
        j = 1
        while (j < ipph) {
            jc = ip - j
            k = 0
            while (k < l1) {
                c1[(k + j * l1) * ido] = ch[(k + j * l1) * ido] + ch[(k + jc * l1) * ido]
                c1[(k + jc * l1) * ido] = ch[(k + jc * l1) * ido] - ch[(k + j * l1) * ido]
                k++
            }
            j++
        }

        ar1 = 1.0
        ai1 = 0.0
        while (l < ipph) {
            lc = ip - l
            ar1h = dcp * ar1 - dsp * ai1
            ai1 = dcp * ai1 + dsp * ar1
            ar1 = ar1h
            ik = 0
            while (ik < idl1) {
                ch2[ik + l * idl1] = c2[ik] + ar1 * c2[ik + idl1]
                ch2[ik + lc * idl1] = ai1 * c2[ik + (ip - 1) * idl1]
                ik++
            }
            dc2 = ar1
            ds2 = ai1
            ar2 = ar1
            ai2 = ai1
            j = 2
            while (j < ipph) {
                jc = ip - j
                ar2h = dc2 * ar2 - ds2 * ai2
                ai2 = dc2 * ai2 + ds2 * ar2
                ar2 = ar2h
                ik = 0
                while (ik < idl1) {
                    ch2[ik + l * idl1] += ar2 * c2[ik + j * idl1]
                    ch2[ik + lc * idl1] += ai2 * c2[ik + jc * idl1]
                    ik++
                }
                j++
            }
            l++
        }
        j = 1
        while (j < ipph) {
            run {
                ik = 0
                while (ik < idl1) {
                    ch2[ik] += c2[ik + j * idl1]
                    ik++
                }
            }
            j++
        }

        if (ido >= l1) {
            k = 0
            while (k < l1) {
                i = 0
                while (i < ido) {
                    cc[i + k * ip * ido] = ch[i + k * ido]
                    i++
                }
                k++
            }
        } else {
            i = 0
            while (i < ido) {
                k = 0
                while (k < l1) {
                    cc[i + k * ip * ido] = ch[i + k * ido]
                    k++
                }
                i++
            }
        }
        j = 1
        while (j < ipph) {
            jc = ip - j
            j2 = 2 * j
            k = 0
            while (k < l1) {
                cc[ido - 1 + (j2 - 1 + k * ip) * ido] = ch[(k + j * l1) * ido]
                cc[(j2 + k * ip) * ido] = ch[(k + jc * l1) * ido]
                k++
            }
            j++
        }
        if (ido == 1) return
        if (nbd >= l1) {
            j = 1
            while (j < ipph) {
                jc = ip - j
                j2 = 2 * j
                k = 0
                while (k < l1) {
                    i = 2
                    while (i < ido) {
                        ic = ido - i
                        cc[i - 1 + (j2 + k * ip) * ido] =
                            ch[i - 1 + (k + j * l1) * ido] + ch[i - 1 + (k + jc * l1) * ido]
                        cc[ic - 1 + (j2 - 1 + k * ip) * ido] =
                            ch[i - 1 + (k + j * l1) * ido] - ch[i - 1 + (k + jc * l1) * ido]
                        cc[i + (j2 + k * ip) * ido] = ch[i + (k + j * l1) * ido] + ch[i + (k + jc * l1) * ido]
                        cc[ic + (j2 - 1 + k * ip) * ido] = ch[i + (k + jc * l1) * ido] - ch[i + (k + j * l1) * ido]
                        i += 2
                    }
                    k++
                }
                j++
            }
        } else {
            j = 1
            while (j < ipph) {
                jc = ip - j
                j2 = 2 * j
                i = 2
                while (i < ido) {
                    ic = ido - i
                    k = 0
                    while (k < l1) {
                        cc[i - 1 + (j2 + k * ip) * ido] =
                            ch[i - 1 + (k + j * l1) * ido] + ch[i - 1 + (k + jc * l1) * ido]
                        cc[ic - 1 + (j2 - 1 + k * ip) * ido] =
                            ch[i - 1 + (k + j * l1) * ido] - ch[i - 1 + (k + jc * l1) * ido]
                        cc[i + (j2 + k * ip) * ido] = ch[i + (k + j * l1) * ido] + ch[i + (k + jc * l1) * ido]
                        cc[ic + (j2 - 1 + k * ip) * ido] = ch[i + (k + jc * l1) * ido] - ch[i + (k + j * l1) * ido]
                        k++
                    }
                    i += 2
                }
                j++
            }
        }
    }

    internal fun rfftf1(n: Int, c: DoubleArray, wtable: DoubleArray?, offset: Int, ch: DoubleArray) {
        var i = 0
        var k1 = 1
        var l1: Int
        var l2: Int
        var na: Int
        var kh: Int
        val nf: Int
        var ip: Int
        var iw: Int = n - 1 + n + offset
        var ido: Int
        var idl1: Int

        System.arraycopy(wtable!!, offset, ch, 0, n)

        nf = wtable[1 + 2 * n + offset].toInt()
        na = 1
        l2 = n
        while (k1 <= nf) {
            kh = nf - k1
            ip = wtable[kh + 2 + 2 * n + offset].toInt()
            l1 = l2 / ip
            ido = n / l2
            idl1 = ido * l1
            iw -= (ip - 1) * ido
            na = 1 - na
            if (ip == 4) {
                if (na == 0) {
                    radf4(ido, l1, c, ch, wtable, iw)
                } else {
                    radf4(ido, l1, ch, c, wtable, iw)
                }
            } else if (ip == 2) {
                if (na == 0) {
                    radf2(ido, l1, c, ch, wtable, iw)
                } else {
                    radf2(ido, l1, ch, c, wtable, iw)
                }
            } else if (ip == 3) {
                if (na == 0) {
                    radf3(ido, l1, c, ch, wtable, iw)
                } else {
                    radf3(ido, l1, ch, c, wtable, iw)
                }
            } else if (ip == 5) {
                if (na == 0) {
                    radf5(ido, l1, c, ch, wtable, iw)
                } else {
                    radf5(ido, l1, ch, c, wtable, iw)
                }
            } else {
                if (ido == 1) na = 1 - na
                na = if (na == 0) {
                    radfg(ido, ip, l1, idl1, c, c, c, ch, ch, wtable, iw)
                    1
                } else {
                    radfg(ido, ip, l1, idl1, ch, ch, ch, c, c, wtable, iw)
                    0
                }
            }
            l2 = l1
            ++k1
        }
        if (na == 1) return
        while (i < n) {
            c[i] = ch[i]
            i++
        }
    }

    private fun rfftf(n: Int, r: DoubleArray, wtable: DoubleArray?, ch: DoubleArray) {
        if (n == 1) return
        rfftf1(n, r, wtable, 0, ch)
    }

    private fun rffti1(n: Int, wtable: DoubleArray, offset: Int) {

        val twopi = 2.0 * Math.PI
        val argh: Double
        var ntry = 0
        var i: Int
        var j: Int
        var argld: Double
        var k1 = 1
        var l1: Int
        var l2: Int
        var ib: Int
        var fi: Double
        var ld: Int
        var ii: Int
        var nf = 0
        var ip: Int
        var nl: Int
        var `is` = 0
        var nq: Int
        var nr: Int
        var arg: Double
        var ido: Int
        var ipm: Int
        val nfm1: Int

        nl = n
        j = 0

        factorize_loop@ while (true) {
            ++j
            if (j <= 4)
                ntry = ntryh[j - 1]
            else
                ntry += 2
            do {
                nq = nl / ntry
                nr = nl - ntry * nq
                if (nr != 0) continue@factorize_loop
                ++nf
                wtable[nf + 1 + 2 * n + offset] = ntry.toDouble()

                nl = nq
                if (ntry == 2 && nf != 1) {
                    i = 2
                    while (i <= nf) {
                        ib = nf - i + 2
                        wtable[ib + 1 + 2 * n + offset] = wtable[ib + 2 * n + offset]
                        i++
                    }
                    wtable[2 + 2 * n + offset] = 2.0
                }
            } while (nl != 1)
            break@factorize_loop
        }
        wtable[0 + 2 * n + offset] = n.toDouble()
        wtable[1 + 2 * n + offset] = nf.toDouble()
        argh = twopi / n
        nfm1 = nf - 1
        l1 = 1
        if (nfm1 == 0) return
        while (k1 <= nfm1) {
            ip = wtable[k1 + 1 + 2 * n + offset].toInt()
            ld = 0
            l2 = l1 * ip
            ido = n / l2
            ipm = ip - 1
            j = 1
            while (j <= ipm) {
                ld += l1
                i = `is`
                argld = ld * argh

                fi = 0.0
                ii = 3
                while (ii <= ido) {
                    i += 2
                    fi += 1.0
                    arg = fi * argld
                    wtable[i - 2 + n + offset] = Math.cos(arg)
                    wtable[i - 1 + n + offset] = Math.sin(arg)
                    ii += 2
                }
                `is` += ido
                ++j
            }
            l1 = l2
            k1++
        }
    }

    private fun rffti(n: Int, wtable: DoubleArray) {
        if (n == 1) return
        rffti1(n, wtable, 0)
    }

}