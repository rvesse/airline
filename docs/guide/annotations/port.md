---
layout: page
title: Port Annotation
---

## `@Port`

The `@Port` annotation may be applied to fields annotated with [`@Option`](option.html) and [`@Arguments`](arguments.html) to restrict values which are intended to reference a port number e.g.

```java
@Option(name = "-p", title = "Port")
@Port(acceptablePorts = { PortType.OS_ALLOCATED, PortType.DYNAMIC })
public int port;
```
This restricts the `-p` option to taking a value which is either an `OS_ALLOCATED` or `DYNAMIC` port which corresponds to the ranges `0` and `49512` through `65535`.

The following table shows the available standard port types:

| `PortType` constant | Port Ranges | Notes |
| -------------------------- | ----------------- | -------- |
| `ANY` | `0` through `65535` | Any valid port number |
| `OS_ALLOCATED` | `0` | The system will allocate a free port since `0` is not a real port number but a special constant |
| `SYSTEM` | `1` through `1023` | System ports, usually require administrative privileges to listen on |
| `USER` | `1024` through `49151` | User ports that may be registered with the IANA |
| `DYNAMIC` | `49152` through `65535` | Dynamic aka `ephermeral` ports |

If you need custom port ranges you can use the more general `@PortRange` and `@PortRanges` annotations as detailed below.

## `@PortRange`

The `@PortRange` annotation allows for custom port ranges e.g.

```java
@Option(name = "-p", title = "Port")
@PortRange(minimum = 2000, maximum = 3000)
public int port;
```

Restricts the `-p` option to ports in the range 2000 to 3000.

## `@PortRanges`

The `@PortRanges` annotation allows for multiple custom port ranges e.g.

```java
@Option(name = "-p", title = "Port")
@PortRanges({ 
  @PortRange(minimum = 2000, maximum = 3000),
  @PortRange(minimum = 6000, maximum = 7000)
}
public int port;
```

Restricts the `-p` option to ports in the range 2000 to 3000 or 6000 to 7000.