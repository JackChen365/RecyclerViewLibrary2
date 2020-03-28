package com.cz.widget.recyclerview.sample.adapter.impl

/**
 * Created by cz on 16/1/27.
 */
class Channel {
    var name: String?=null
    var use: Boolean = false

    constructor()

    constructor(name: String, use: Boolean) {
        this.name = name
        this.use = use
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val r = o as Channel?
        return name == r!!.name
    }

    override fun toString(): String =name?:""
}
