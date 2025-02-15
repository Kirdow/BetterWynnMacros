<p align="center">
    <img src="https://raw.githubusercontent.com/Kirdow/BetterWynnMacros/refs/heads/master/src/main/resources/assets/ktnwynnmacros/logo.png">
</p>
<p align="center">
    <h1 style="border-bottom: none" align="center"><u>Better Wynn Macros</u></h1>
</p>
<p align="center" style="margin-top: -20px">
    <a align="center" href="https://github.com/Kirdow/BetterWynnMacros/blob/master/LICENSE">
        <img src="https://img.shields.io/github/license/Kirdow/BetterWynnMacros?style=flat-square">       
    </a>
    <a align="center" href="https://github.com/Kirdow/BetterWynnMacros/actions/workflows/build.yml">
        <img alt="GitHub Actions Workflow Status" src="https://img.shields.io/github/actions/workflow/status/Kirdow/BetterWynnMacros/build.yml?style=flat-square">
    </a>
</p>
<p align="center">
    Adds better and more reliable spell keybinds to Wynncraft
</p>
<hr>

This mod improves Wynncraft's spell casting by adding predictive and intuitive spell macros. It does this by adding two features. Smart Casting and Force Casting.

The mod also has 4 buttons used to spell casting just like any other Wynn Macro mod.
## Smart Casting
Smart casting checks the current casting sequence and determines if it can cast a spell by finish the current queue. If it can, it will. If it can't, the options depend on your Force Casting setting.

## Force Casting
Force Casting (or Force-Cast as referred to in game) has 3 modes. Default is None/Off. Other two modes are Wait for Timeout and Block Manual.

### None
When Force Casting is turned off, it simulates the vanilla behaviour. However, if Smart Casting is enabled, it will still complete existing casts if it can.

### Wait for Timeout
This mode waits for the current cast sequence/queue to expire (as seen when the hotbar text fades away) before it executes the spell cast. If Smart Casting is enabled, it will however finish off a cast directly if it can.

### Block Manual
This is the same as None, except it also blocks the vanilla spell cast button, which for Archer is Left-Click and other classes is Right-Click. Side effect of using this involves not being able to right click interactable objects like doors or NPCs. Currently to fix that the mod only works when holding a weapon.

## Other Features
The mod also has 4 key binds used to cast spells, just like any other Wynn Macro mod. The default delay between each cast input is 100ms. This value is also configurable between 10ms and 1000ms. However values below 100ms are not recommended as they tend to glitch out.

# Download

Available on [Modrinth](https://modrinth.com/mod/better-wynn-macros) and [CurseForge](https://www.curseforge.com/minecraft/mc-mods/betterwynnmacros).

# License

Better Wynn Macros is released under the license [GNU Lesser General Public License v3.0](https://github.com/Kirdow/BetterWynnMacros/blob/master/LICENSE).
