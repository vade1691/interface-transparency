# Interface Transparency

A RuneLite plugin that lets you adjust the transparency of the side panel menu tabs, the chat tabs and chat box, and the minimap orbs and surround.

## Features

Each area has its own settings section, with a choice between one slider for everything or separate sliders for each element.

- **Resizable modern:** tab stones (the stone background and the highlighted active tab), tab icons, the panel background and the panel border.
- **Resizable classic:** tab stones, tab icons, the side pillars and the panel background.
- **Chat tabs:** the chat tab buttons, the background behind them, and the chat box background behind the messages.
- **Minimap:** the orbs (health, prayer, run energy, special attack, store, world map, wiki, recommendations and XP drops) and the minimap surround.

Turn on **Use one slider for all** in a section to control everything in it with a single slider, or turn it off to set each element separately.

Sliders run from 0% (fully transparent) to 100% (the game's normal appearance).

## Panel and chat backgrounds

The side panel backgrounds (both layouts), the background behind the chat tab buttons and the chat box background follow their slider directly, so you can control them whether or not the game's own transparency settings are turned on:

- 0% is fully transparent.
- Between 1% and 99% they fade smoothly.
- At 100% they are left as the game draws them.

Each of these has a **Keep ... background opaque** option, off by default. Turn it on to keep that background fully solid at 100% even when the game's **Transparent side panel** or **Transparent chatbox** setting is on, for example to have a transparent chat box with a solid strip behind the tab buttons.

## Tips

- The plugin works alongside the game's own transparency settings. At 100% (with the **Keep ... opaque** options off) it changes nothing, and the game's original look is restored when the plugin is turned off.
- The other elements (tab stones, icons, borders, pillars, orbs and so on) fade relative to the game's own setting, so turning on **Transparent side panel** or **Transparent chatbox** in the game's Interfaces settings still works with them.

## Notes

- This plugin only changes the opacity of existing interface widgets. It does not hide or add any information, and it does not affect gameplay.
- The chat filter text (the On/Off/Filter text under the chat tabs) is ignored by the game's opacity setting, so it does not fade with the chat tab buttons.
