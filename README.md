# Welcome to Augur, the free Tarot tool!
The cards foretold your arrival...

Augur is a command line interface that allows the user to divine their future using a spread of Tarot cards. It calls the REST [Tarot Card API](https://tarotapi.dev/) and implements the [Picocli](https://picocli.info/) framework to make this possible.

### Features:
* Users can choose between three traditional Tarot spreads (the one-card draw, the three-card draw, and the Celtic cross) for a reading suited to their individual sortilege needs.
* For users unfamiliar with Tarot, Augur will ask you some useful questions and choose a suitable Tarot spread to divine your future.
* A spread includes the image of each Tarot card, the significance of its position in the spread, and its divinatory meaning.
* As in a real Tarot reading, some cards will be drawn in reverse position with their meaning altered accordingly.
* Users can request another reading as many times as necessary to divine their future.

### Example three-card draw:
![alt text](app/src/main/resources/images/AugurScreenshot.png)

### Quick start:
* Download Augur files. (`git clone https://github.com/fionadark/augur`)
* `cd` into Augur source folder.
* Execute `./gradlew run`.

Code available under the Apache 2.0 license.