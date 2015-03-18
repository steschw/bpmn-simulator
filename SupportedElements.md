

# Flow objects #

## Event ##

| **Element** | **Supported** |
|:------------|:--------------|
| conditionalStartEvent | _no_ |
| endEvent | yes |
| errorEndEvent | _no_ |
| escalationEndEvent | _no_ |
| messageEndEvent | _no_ |
| messageStartEvent | _no_ |
| signalEndEvent | _no_ |
| signalStartEvent | _no_ |
| startEvent | yes |
| terminateEndEvent | yes |
| timerStartEvent | _no_ |

## Activity ##

| **Element** | **Supported** |
|:------------|:--------------|
| callActivity | _no_ |
| subProcess | yes |

### Task ###

| **Element** | **Supported** |
|:------------|:--------------|
| businessRuleTask | yes |
| manualTask | yes |
| receiveTask | yes |
| scriptTask | yes |
| sendTask | yes |
| serviceTask | yes |
| task | yes |
| userTask | yes |

## Gateway ##

| **Element** | **Supported** |
|:------------|:--------------|
| exclusiveGateway | yes |
| inclusiveGateway | yes |
| parallelGateway | yes |
| eventBasedGateway | _no_ |

# Connecting objects #

| **Element** | **Supported** |
|:------------|:--------------|
| sequenceFlow |yes |
| messageFlow |_no_ |

# Swim lanes #

| **Element** | **Supported** |
|:------------|:--------------|
| participant | yes |
| laneSet | yes |

# Artifacts #

| **Element** | **Supported** |
|:------------|:--------------|
| association | yes |
| dataAssociation | _no_ |
| dataObject | _no_ |
| group | yes |
| message | _no_ |
| textAnnotation | yes |