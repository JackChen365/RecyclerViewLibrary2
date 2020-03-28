## TreeAdapter.

> It's a tree adapter.


#### Example classes:

* [TreeSampleActivity](https://raw.githubusercontent.com/momodae/RecyclerViewLibrary2/master/app/src/main/java/com/cz/widget/recyclerview/sample/adapter/TreeSampleActivity.kt)
* [FileTreeAdapter](https://raw.githubusercontent.com/momodae/RecyclerViewLibrary2/master/app/src/main/java/com/cz/widget/recyclerview/sample/adapter/impl/FileTreeAdapter.kt )

#### Image
![tree_adapter](https://github.com/momodae/LibraryResources/blob/master/RecyclerViewLibrary/image/adapter/tree_adapter.gif?raw=true)

### Usage

```
//Build a tree.
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
//Here setup the RecyclerView.
val fileNode = getAllFileNode(3)
val fileTreeAdapter = FileTreeAdapter(this, fileNode)
recyclerView.adapter=fileTreeAdapter
```


#### The functions

| Function prototype | Sample | Note |
| ------ | ------ | ------ |
| TreeAdapter.expandAll() | adapter.expandAll() | expand all the nodes. |
| TreeAdapter.collapseAll() | adapter.collapseAll() | close all the opened nodes. |
| TreeAdapter.removeNode(@NonNull TreeNode<E> node) | adapter.removeNode(node) | Remove a node from tree. |
| TreeAdapter.remove(int position) | adapter.remove(0) | Remove a node by the position |
| TreeAdapter.add(E e) | adapter.add(item) | Add a data as a node |
| TreeAdapter.addNode(TreeNode<E> node) | adapter.addNode(node) | Add a new node |
| TreeAdapter.setLoadCallback(TreeLoadCallback<E> callback)| adapter.setLoadCallback(callback) | Lazily load data from this callback |
| TreeAdapter.setOnTreeNodeClickListener(OnTreeNodeClickListener<E> listener)| adapter.setOnTreeNodeClickListener(listener) | Receive the node click listener. |


### Load data lazily

```
//If we click one node. We assume this node is not load the data.
//If you want to load data from somewhere. Here you could return the children.
fileTreeAdapter.setLoadCallback { parentNode->
    val file=parentNode.item
    val children= mutableListOf<TreeNode<File>>()
    if(file.isDirectory){
        for(f in file.listFiles()){
            children.add(TreeNode<File>(parentNode,f))
        }
    }
    children
}
```



### Problems

* There is a problem. If you want to use both HeaderWrapperAdapter and TreeAdapter.

```
@Override
public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
    TreeNode<E> node = getNode(position);
    onBindViewHolder(holder, node, node.item, getItemViewType(position), position);
    holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Here from the ViewHolder you always return a real adapter position.
            //The one includes the extra position. So be careful with it. But use this adapter alone is fine.
            int adapterPosition = holder.getAdapterPosition();
            ...
        }
    }
}
```
