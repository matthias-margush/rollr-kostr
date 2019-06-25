# kotlin-kstreams-experiments

## Notes
See [notes](Notes.md).

## Setup
### Install gradle if needed:
```bash
brew install gradle
```

### Bootstrap build (once after git clone):
```bash
gradle wrapper --gradle-version 5.4.1
```

### Build (& fetch dependencies):
```bash
gradle build
```
### Run tests
```bash
gradle test # --info for more details
```

### If using Intellij:
```bash
idea .
```
