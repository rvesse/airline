---
layout: page
title: Path, File and Directory Annotations
---

## `@Path`

The `@Path` annotation may be applied to fields annotated with [`@Option`](option.html) and [`@Arguments`](arguments.html) that take filesystem path related arguments to impose various restrictions on those e.g.

```java
@Option(name = "--path", arity = 1)
@Path(mustExist = true)
public String pathMustExist;
```

Requires that the `--path` option refer to an existing path.

We can also restrict the kind of the path e.g.
    
```java
@Option(name = "--file", arity = 1)
@Path(mustExist = true, kind = PathKind.FILE)
public String fileMustExit;
```
Requires that the `--file` option be an existing path that references a file.

Similarly we can also require a directory e.g.
    
```java
@Option(name = "--directory", arity = 1)
@Path(mustExist = true, kind = PathKind.DIRECTORY)
public String dirMustExist;
```
Note that there is also a `PathKind.ANY` if we allow either files or directories.

Additionally we can specify restrictions on the access mode of the path:
    
```java
@Option(name = "--readable", arity = 1)
@Path(mustExist = false, readable = true, writable = false, executable = false)
public String readable;
    
@Option(name = "--writable", arity = 1)
@Path(mustExist = false, writable = true, readable = false, executable = false)
public String writable;
    
@Option(name = "--executable", arity = 1)
@Path(mustExist = false, executable = true, readable = false, writable = false)
public String executable;
```
In the above examples we use the `readable`, `writable` and `executable` fields of the annotation to specify the access modes that the path must support.

## `@File` and `@Directory`

These annotations function the same as the above described `@Path` annotation except that the `kind` field is not available since it is implied by the name of the annotation.

So `@File` functions the same as `@Path(kind = PathKind.FILE)` and `@Directory` the same as `@Path(kind = PathKind.DIRECTORY)` 

### Related Annotations

For enforcing certain file extensions the [`@EndsWith`](ends-with.html) annotation may be useful.