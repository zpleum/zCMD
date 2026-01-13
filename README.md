# ⚡ zAutoFeed

**zAutoFeed** is a lightweight Fabric mod for Minecraft 1.21+ that automatically executes commands at configurable intervals.  
Perfect for repetitive tasks like feeding, buffing, or running server-side commands.

---

## Features

- ✅ Execute **multiple commands** with separate intervals  
- ✅ Toggle mod **ON/OFF** with a keybind  
- ✅ **HUD** showing command timers and mod status  
- ✅ Adjustable **HUD opacity** (0–100) via GUI  
- ✅ Full **ClothConfig GUI** for editing commands, intervals, and mod settings  
- ✅ Automatically saves configuration to JSON  
- ✅ Works in **singleplayer** and **multiplayer**

---

## Installation

1. Install **Fabric Loader 0.14+**  
2. Install **Fabric API 0.129+**  
3. Drop the built JAR into your `mods` folder  
4. Run **Minecraft 1.21+**

---

## Usage

| Action                   | Default Key |
|---------------------------|------------|
| Toggle AutoFeed           | `.` (period) |
| Open Config GUI           | `O`          |

### GUI Features
- Enable/disable mod  
- Add/Edit/Delete commands  
- Set per-command interval in seconds  
- Adjust HUD opacity (0 = fully transparent, 100 = fully opaque)  

### HUD
- Displays enabled/disabled status  
- Shows remaining time for each command  
- Footer with author/license info  
- Fully respects opacity setting  

---

## Configuration

- Configuration is stored in `zautofeed.json` inside the Minecraft `config` folder  
- Auto-saved when changing settings via GUI  
- Example:

```json
{
  "intervalSeconds": 200,
  "hudOpacity": 100,
  "showHud": true,
  "hudScale": 1.0,
  "alignment": "TOP_LEFT",
  "enabled": true,
  "commands": [
    {
      "command": "feed",
      "intervalSeconds": 200
    },
    {
      "command": "heal",
      "intervalSeconds": 300
    }
  ]
}
````

---

## License

MIT License – See [LICENSE](LICENSE)

---

## Author

**zPleum** – [Website](https://www.zpleum.site/) | [GitHub](https://github.zpleum.site/) | [Discord](https://discord.zpleum.site/)
