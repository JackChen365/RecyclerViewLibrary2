## RecyclerView Adapter

### 中文文档
* [Document](readme-cn.md)

> This project support all different kinds of RecyclerView adapter.

* HeaderWrapperAdapter
* DynamicWrapperAdapter
* SelectWrapperAdapter
* DragWrapperAdapter
* StickyWrapperAdapter

All the adapter above was decorated adapter. In that case. You don't have to change anything.

#### HeaderWrapperAdapter

> It's an wrapper adapter. We use this adapter to add extra header or footer view.

```
Header1
Header2
...(List)
Footer1
Footer2
```

[More detail](document/en/HeaderWrapperAdapter.md)

#### DynamicWrapperAdapter

> It's an enhanced adapter for the HeaderWrapperAdapter. The HeaderWrapperAdapter only allow you add view as a header or footer.
> This Adapter support you insert layout in anywhere.


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

By use a decorate design. We use an integer array to manipulate the original adapter.
It won't ruin your adapter. But given you an orderly position without extra layout position. [More detail](document/en/DynamicWrapperAdapter.md)

#### SelectWrapperAdapter
> It's for the user who wants to manually choose some things.

We support three different choice-mode.
* Single-choice
* Multi-choice
* Range-choice

[More detail](document/en/SelectWrapperAdapter.md)

#### DragWrapperAdapter
> It's a drag adapter. It's actually an adapter support user to drag the layout.[More detail](document/en/DragWrapperAdapter.md)


### The list above was functional adapter.

* BaseAdapter
* ExpandAdapter
* TreeAdapter
* FilterAdapter
* CursorAdapter

#### BaseAdapter
> It's a basic adapter. The only thing we did was totally control how the list data changed.
  Like functional programming. We don't expose the list but hide all the operations inside.[More detail](document/en/BaseAdapter.md)


#### ExpandAdapter
> It's a same thing like the old ListView's ExpandListAdapter. But It's more flexible and easy to use.[More detail](document/en/ExpandAdapter.md)

#### TreeAdapter
> It's a tree adapter. Just a tree adapter.[More detail](document/en/TreeAdapter.md)

#### FilterAdapter.
> This adapter extend from BaseAdapter. And we support an default object filter by use the Filter.[More detail](document/en/FilterAdapter.md)

#### StickyAdapter

### The class hierarchy view

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

