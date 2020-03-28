## RecyclerView Adapter

> 这是一个扩展 RecyclerView Adapter 的库

* HeaderWrapperAdapter 包装实现 Header footer
* DynamicWrapperAdapter 包装实现添加任一位置控件.
* SelectWrapperAdapter 包装实现不同选择状态
* DragWrapperAdapter 包装实现拖动

以上数据适配器都是基于包装设计模式,所以不需要更改原来的数据适配器逻辑.

#### HeaderWrapperAdapter

> 包装实现添加头与尾的数据适配器.

```
Header1
Header2
...(List)
Footer1
Footer2
```

[More detail](document/en/HeaderWrapperAdapter.md)

#### DynamicWrapperAdapter

> 增强型的动态包装数据适配器,用来在任意位置添加自定义控件.


```
Header1
Data1
Data2
(Layout1)
Data3
(Layout2)
Data4
Footer1
```

内部使用一个映射所有关联位置. [More detail](document/en/DynamicWrapperAdapter.md)

#### SelectWrapperAdapter
> 用于增强扩展不同模式的选择.

支持以下三种选择模式
* 单选
* 多选
* 块选

[More detail](document/en/SelectWrapperAdapter.md)

#### DragWrapperAdapter
> 拖动的数据适配器. 支持控件拖动

[More detail](document/en/DragWrapperAdapter.md)


### The list above was functional adapter.

* BaseAdapter
* ExpandAdapter
* TreeAdapter
* FilterAdapter
* CursorAdapter

#### BaseAdapter
> 一个基础的数据适配器,封装了列表数据操作.避免外围直接操作.引发的各种问题.对所有操作进行局部更新.更可靠. [More detail](document/en/BaseAdapter.md)


#### ExpandAdapter
> 类似于ListView 的双层级展开的数据适配器.

[More detail](document/en/ExpandAdapter.md)

#### TreeAdapter
> 树形数据适配器

[More detail](document/en/TreeAdapter.md)

#### FilterAdapter.
> 过滤数据的数据适配器

[More detail](document/en/FilterAdapter.md)


### 数据适配器类视图树.

```
RecyclerView.Adapter
    |- HeaderWrapperAdapter
        |- DynamicWrapperAdapter
            |- DragWrapperAdapter
    |- SelectWrapperAdapter

RecyclerView.Adapter
    |- BaseAdapter
        |- FilterAdapter
    |- CursorAdapter
    |- ExpandAdapter
    |- TreeAdapter
```

