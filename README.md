# FairyChat

The next generation chat plugin for managing your server's chat.

*FairyChat is the future of chat management for your server, providing a range of features from customizable chat formats to multi-server message broadcasting. The perfect solution to bring a seamless chatting experience to your server users.*

## Features

- **Customizable Chat Format**: Personalize the chat format as per your needs.
- **Per-Group Chat Format**: Differentiate chats based on the user group for better management.
- **MiniMessage & MiniPlaceholders Support**: All formats are compatible with MiniMessage & MiniPlaceholders, making the customization process easier than ever.
- **Optional Multi-Server Message Sending**: Have multiple servers? No problem. FairyChat supports sending messages across servers with Redis.

## Commands & Permissions

| Command | Permission | Description |
| ------- | ---------- | ----------- |
| `/broadcast <message>` | `fairychat.command.broadcast` | Broadcast a message to all servers |

## Placeholders

For a list of all available placeholders, please refer to the [MiniPlaceholders wiki](https://github.com/MiniPlaceholders/MiniPlaceholders/wiki).

## Permission Providers

FairyChat supports the following permission providers:

- [LuckPerms](https://luckperms.net/)

Please note that FairyChat will automatically detect the permission provider and use it.

*If you would like to see support for another permission provider, please open an issue.*

## Installation

You need to have [Paper](https://papermc.io/) (or fork of paper) installed on your server. FairyChat is not compatible with other server software.

## Configuration

```yaml
# Redis credentials, used to broadcast messages to other servers.
redisCredentials:
  enabled: true
  url: redis://localhost:6379
# The format of chat messages as minimessage.
# You can use any placeholder from
# https://github.com/MiniPlaceholders/MiniPlaceholders/wiki/Placeholders
chatFormat: <player_name> <dark_gray>» <gray><message>
# Define formats based on the player's group.
# Following permission systems are supported:
# LuckPerms
groupFormats:
  admin: <dark_red><player_name> <dark_gray>» <white><message>
```

## License

**FairyChat** is licensed under custom terms, which can be found [here](LICENSE.md).