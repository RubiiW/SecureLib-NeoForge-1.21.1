![Banner](https://cdn.modrinth.com/data/cached_images/56dd6d544d8d34eb29b0dc2d60a575af887807bd.png)

# USAGE
**If you want to use this mod as a library, here is a quick tutorial on how to implement SecureLib into your project**

Inside your ```build.gradle```, edit the ```repositories``` category to include ```maven { url 'https://jitpack.io' }```. It should look like this:

```
repositories {
    maven { url 'https://jitpack.io' }
}
```

Then, still in your ```build.gradle```, edit the ```dependencies``` category to include ```implementation 'com.github.RubiiW:SecureLib-NeoForge-1.21.1:VERSION'```.

It should look like this. Make sure to replace ```VERSION``` with the version of the mod you want to use (for instance, the currently latest ```1.4.4```).
```
dependencies {
    implementation 'com.github.RubiiW:SecureLib-NeoForge-1.21.1:VERSION'
}
```
<details>
  <summary>Usage Example</summary>
  
  As an example, you can create your own Card Reader by registering a block of type CardReaderBlock

```
import net.rubii.securelib.block.custom.CardReaderBlock;
```
You will also need to register this block in your main mod class as a card reader

```
import net.rubii.securelib.api.CardReaderRegistry;

@Mod(YourModClass.MODID)
public class YourModClass {
  public YourModClass(IEventBus modEventBus, ModContainer modContainer) {
        CardReaderRegistry.register(YourModBlocksClass.CUSTOM_CARD_READER);
        //Add the remaining calls and functions below
  }
}
```
</details>

## FEATURES:
- **Keycard (Customizable)**
- **Operator Keycard (Admin Only Skeleton Keycard)**
- **Card Reader**
- **Card Writer & Printer**
- **Reader Editor**

## CLEARANCE & FREQUENCY SYSTEM
**You can finally make your own security infrastructure for your ~~Nuclear Power Plant~~ spider farm!**

- The Clearance is a value between 1 and 9, any Keycard can open Card Readers with an equal or greater Clearance, for instance, a Keycard with a clearance of 4 can interact with a Card Reader wich Clearance is 1, 2, 3 or 4.
- The Frequency is a text that defines which elements can interact in your infrastructure, for instance, a Keycard with a Frequency of "TestFreq1" won't be able to interact with a Card Reader of Frequency "TestFreq2".
- ⚠️Frequencies are **hashed**, that means that anyone can see them, but no one can reproduce them easily. For instance, "TestFreq1" will output "800698151" and "TestFreq2" will output "800698152", which means that you can decrypt your way to a frequency, hardly.⚠️

# RELEASES
**Go on the [Modrinth Page](https://modrinth.com/mod/securelib) to access the mod's releases**

# LISENCING
**Project is lisenced under [CreativeCommons Atribution NonCommercial ShareAlike 4.0](https://creativecommons.org/licenses/by-nc-sa/4.0/)**
