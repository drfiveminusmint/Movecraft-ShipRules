# Movecraft-ShipRules
A Spigot plugin designed to automatically enforce ship rules on Movecraft servers. Written by a4bde but borrows code heavily from Movecraft 7, written by cccm5, TylerS1066, and BaccaYarro.

# Building
Movecraft-ShipRules requires Movecraft 7 to build. Its directory should be placed in the same directory as the directory containing Movecraft-Master, and then ```mvn clean install``` can be run to build the plugin. Jars will be located in ```Movecraft-ShipRules/target```.

# Usage
To use Movecraft-Shiprules, you must create a .rules file. .rules files apply to one particular CraftType and are read as YAML files. A list of properties one may set in a .rules file is included below:

* ```applicableType:``` The CraftType this set of rules applies to. Required.
* ```minLengthToWidthRatio:``` The minimum ratio of length to width for the craft's detected hitbox. This is not direction-dependent, which axis constitutes 'length' and which constitutes 'width' is found at detection time. Defaults to -1, which will have no effect.
* ```maxLenghtToWidthRatio:``` Same as above.
* ```minLengthToHeightRatio:``` Same as above. Unlike Length and Width, which change which axis they are depending on a ship's configuration, Height is always the Y-axis.
* ```maxLengthToHeightRatio```
* ```minWidthToHeightRatio:``` It's width to height rather than the other way around as it is expected that ships will be wider than they are tall.
* ```maxWidthToHeightRatio```
* ```minAbsoluteHeight:``` The maximum number of blocks tall this type can be, irrespective of all other directions. Defaults to -1 (disabled).
* ```maxAbsoluteHeight:``` Same as above. Setting this and the above as the same integer value will require crafts to always be the same number of blocks tall, and will change the message to only display one number rather than the 'max' and 'min.'
* ```minAbsoluteLength:``` Same as above.
* ```maxAbsoluteLength```
* ```minAbsoluteWidth```
* ```maxAbsoluteWidth```
* ```minEngineBlobs:``` Currently unused.
* ```maxEngineBlobs:``` Same as above.
* ```requireCruiseSignAlignment:``` Requires all cruise signs on the craft to be facing the same direction.
