JMX Exporter
=====

JMX to Prometheus bridge.

A Collector that can configurably scrape and expose mBeans of a JMX target.
This can be run as an independent HTTP server and scrape remote JMX targets.

## Building and Running

`mvn package` to build.
See `./run_sample_httpserver.sh` for a sample script that runs the httpserver against itself.

## Configuration
The configuration is in JSON. An example with all possible options:
```
{ 
  "hostPort": "127.0.0.1:1234",
  "rules": [
    {"pattern": "^org.apache.cassandra.metrics<type=(\w+), name=(\w+)><>Value:",
     "name": "cassandra_$1_$2",
     "labels": {},
     "help": "Cassandra metric $1 $2",
     "type": "GAUGE",
     "attrNameSnakeCase": false},
  ]
}
```
Name     | Description
---------|------------
hostPort | The host and port to connect to via remote JMX. If not specified, will talk to the local JVM.
rules    | A list of rules to apply in order, processing stops at the first matching rule. Attributes that aren't matched aren't collected. If not specified, defaults to collecting everything in the default format.
pattern  | Regex pattern to match against each bean attribute. The pattern is not anchored. Capture groups can be used in other options. Defaults to matching everything.
attrNameSnakeCase | Converts the attribute name to snake case. This is seen in the names matched by the pattern and the default format. For example, anAttrName to an\_attr\_name.
name     | The metric name to set. Capture groups from the `pattern` can be used. If not specified, the default format will be used.
labels   | A map of label name to label value pairs. Capture groups from `pattern` can be used in each. `name` must be set to use this. If not specified and the default format is not being used, no labels are set.
help     | Help text for the metric. Capture groups from `pattern` can be used. `name` must be set to use this. Defaults to the mBean attribute decription and the full name of the attribute.
type     | The type of the metric, can be `GAUGE` or `COUNTER`. `name` must be set to use this. Defaults to `GAUGE`.

Metric names and label names are sanitized. All characters other than `[a-zA-Z0-9:_]` are replaced with underscores,
and adjacent underscores are collapsed. There's no limitations on label values or the help text.

A minimal config is `{}`, which will connect to the local JVM and collect everything in the default format.
Note that the scraper always processes all mBeans, even if they're not exported.

### Pattern input
The format of the input matches against the pattern is
```
domain<beanpropertyName1=beanPropertyValue1, beanpropertyName2=beanPropertyValue2, ...><key1, key2, ...>attrName: value
```

Part     | Description
---------|------------
domain   | Bean name. This is the part before the colon in the JMX object name.
beanProperyName/Value | Bean properties. These are the key/values after the colon in the JMX object name.
keyN     | If composite or tabular data is encountered, the name of the attribute is added to this list. 
attrName | The name of the attribute. For tabular data, this will be the name of the column. If `attrNameSnakeCase` is set, this will be converted to snake case.
value    | The value of the attribute.

No escaping or other changes are made to these values, with the exception of if `attrNameSnakeCase` is set. 
The default help includes this string, except for the value.

### Default format
The default format will transform beans in a way that should produce sane metrics in most cases. It is
```
domain_beanPropertyValue1_key1_key2_...keyN_attrName{beanpropertyName2="beanPropertyValue2", ...}: value
```
If a given part isn't set, it'll be excluded.

## Testing

`mvn test` to test.

## Installing

A Debian binary package is created as part of the build process and it can 
be used to install an executable into `/usr/local/bin/jmx4prometheus` with configuration
in `/etc/jmx4prometheus/jmx4prometheus_config.json`.

## TODO

* Add configurable logging  

