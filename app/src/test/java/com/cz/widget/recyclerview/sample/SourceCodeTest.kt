package com.cz.widget.recyclerview.sample

import org.junit.Test
import java.io.File

/**
 * @author Created by cz
 * @date 2020-03-27 19:18
 * @email bingo110@126.com
 *
 */
class SourceCodeTest{

    @Test
    fun sourceFileTest(){
        val file= File("../")
        println(file.absolutePath)
        file.listFiles().forEach {
            println(it.absolutePath)
        }
//        Files.walkFileTree()
    }
}