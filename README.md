![Banner](https://cdn.modrinth.com/data/cached_images/56dd6d544d8d34eb29b0dc2d60a575af887807bd.png)

# USAGE
**If you want to use this mod as a library, here is a quick tutorial on how to implement SecureLib into your project**
(This part is still work in progress, for now, use your own knowledge.)
[![](https://jitpack.io/v/RubiiW/SecureLib-NeoForge-1.21.1.svg)](https://jitpack.io/#RubiiW/SecureLib-NeoForge-1.21.1)

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
