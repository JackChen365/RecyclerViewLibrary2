package com.cz.widget.recyclerview.sample.adapter

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater.from
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.cz.android.sample.api.RefRegister
import com.cz.android.sample.library.appcompat.SampleAppCompatActivity
import com.cz.android.sample.library.component.code.SampleSourceCode
import com.cz.widget.recyclerview.adapter.WrapperAdapter
import com.cz.widget.recyclerview.adapter.support.tree.TreeAdapter
import com.cz.widget.recyclerview.adapter.support.tree.TreeNode
import com.cz.widget.recyclerview.adapter.wrapper.header.HeaderWrapperAdapter
import com.cz.widget.recyclerview.sample.R
import com.cz.widget.recyclerview.sample.adapter.impl.FileTreeAdapter
import kotlinx.android.synthetic.main.activity_tree_sample.*
import java.io.File
import kotlin.random.Random

/**
 * @author Created by cz
 * @date 2020-03-18 11:06
 * @email bingo110@126.com
 */
@SampleSourceCode
@RefRegister(title=R.string.tree_adapter,desc = R.string.tree_adapter_desc,category = R.string.adapter)
class TreeSampleActivity : SampleAppCompatActivity() {
    companion object{
        private const val REQUEST_PERMISSION_CODE=1
    }
    private var colorList = mutableListOf(-0x1000000, -0xbbbbbc ,-0x777778, -0x333334, -0x1,-0x10000,-0xff0100,-0xffff01,-0x100,-0xff0001,-0xff01)

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tree_sample)
        recyclerView.layoutManager= LinearLayoutManager(this)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                loadFileSystem()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSION_CODE)
            }
        } else {
            loadFileSystem()
        }

        buttonExpandAll.setOnClickListener {
            val adapter = recyclerView.adapter
            if(null!=adapter&&adapter is WrapperAdapter){
                val wrappedAdapter = adapter.adapter
                if(wrappedAdapter is TreeAdapter<*>){
                    wrappedAdapter.expandAll()
                }
            }
        }

        buttonCollapseAll.setOnClickListener {
            val adapter = recyclerView.adapter
            if(null!=adapter&&adapter is WrapperAdapter){
                val wrappedAdapter = adapter.adapter
                if(wrappedAdapter is TreeAdapter<*>){
                    wrappedAdapter.collapseAll()
                }
            }
        }
    }

    private fun loadFileSystem() {
        val fileNode = getAllFileNode(2)
        val fileTreeAdapter = FileTreeAdapter(this, fileNode)
        //We could lazily load the sub-node by use TreeLoadCallback
        fileTreeAdapter.setLoadCallback { parentNode ->
            val file = parentNode.item
            val children = mutableListOf<TreeNode<File>>()
            if (file.isDirectory) {
                for (f in file.listFiles()) {
                    children.add(TreeNode<File>(parentNode, f))
                }
            }
            children
        }
        buttonExpandAll.isEnabled = true
        buttonCollapseAll.isEnabled = true
        val wrapperAdapter = HeaderWrapperAdapter(fileTreeAdapter)
        wrapperAdapter.addHeaderView(getHeaderView(wrapperAdapter))
        recyclerView.adapter = wrapperAdapter
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (i in 0 until grantResults.size) {
                if(Manifest.permission.READ_EXTERNAL_STORAGE==permissions[i]&&PackageManager.PERMISSION_GRANTED==grantResults[i]){
                    loadFileSystem()
                } else {
                    AlertDialog.Builder(this).setMessage(R.string.permission_denied).show()
                }
            }
        }
    }

    private fun getAllFileNode(maxDepth:Int): TreeNode<File> {
        val rootFile = Environment.getExternalStorageDirectory()
        val rootNode = TreeNode(rootFile)
        traversal(rootFile,rootFile,rootNode, maxDepth,0)
        return rootNode
    }

    private fun traversal(rootFile: File, file:File, parentNode:TreeNode<File>, maxDepth: Int, depth:Int){
        var childNode = parentNode
        if(rootFile!=file){
            childNode = TreeNode(parentNode, file)
            parentNode.children.add(childNode)
            parentNode.isLoad=true
        }
        if(maxDepth > depth && file.isDirectory){
            val listFiles = file.listFiles()
            if(null!=listFiles){
                for(f in listFiles){
                    traversal(rootFile,f,childNode,maxDepth,depth+1)
                }
            }
        }
    }

    /**
     * Return a header view
     */
    private fun getHeaderView(wrapperAdapter:HeaderWrapperAdapter): View {
        val textColor = colorList[Random.nextInt(colorList.size)]
        val header = from(this).inflate(R.layout.adapter_header_layout, recyclerView, false)
        val headerView = header as TextView
        headerView.setTextColor(textColor)
        headerView.text = "HeaderView:" + wrapperAdapter.headerViewCount
        headerView.setOnClickListener { wrapperAdapter.addHeaderView(getHeaderView(wrapperAdapter)) }
        return headerView
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val adapter=recyclerView.adapter
        if (id == R.id.action_add) {
            if(null!=adapter&&adapter is WrapperAdapter){
                val wrappedAdapter = adapter.adapter as TreeAdapter<File>
                val file = File("New file.")
                wrappedAdapter.addFirst(file)
            }
            return true
        } else if (id == R.id.action_remove) {
            if(null!=adapter&&adapter is WrapperAdapter){
                val wrappedAdapter = adapter.adapter as TreeAdapter<File>
                wrappedAdapter.remove(0)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
