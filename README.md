# FairyChat

The next generation chat plugin for managing your server's chat.

*FairyChat is the future of chat management for your server, providing a range of features from customizable
chat
formats to multi-server message broadcasting. The perfect solution to bring a seamless chatting experience to
your
server users.*

## Features

- **Customizable Chat Format**: Personalize the chat format as per your needs.
- **Per-Group Chat Format**: Differentiate chats based on the user group for better management.
- **Private Messaging**: Send private messages to other players with ease. Supports tab completion across multiple
  servers.
- **MiniMessage & MiniPlaceholders Support**: All formats are compatible with MiniMessage & MiniPlaceholders, making the
  customization process easier than ever.
- **Optional Multi-Server Message Sending**: Have multiple servers? No problem. FairyChat supports sending messages
  across servers with Redis.

## Commands & Permissions

| Command                                 | Permission                      | Description                        |
|-----------------------------------------|---------------------------------|------------------------------------|
| `/broadcast --type=broadcast <message>` | `fairychat.command.broadcast`   | Broadcast a message to all servers |
|                                         | `fairychat.feature.minimessage` | Use mini message in chat           |
| `/pm`                                   |                                 | Send a private message to a player |
| `/r`                                    |                                 | Reply to a private message         |

## Placeholders

For a list of all available placeholders, please refer to
the [MiniPlaceholders wiki](https://github.com/MiniPlaceholders/MiniPlaceholders/wiki).

## Permission Providers

FairyChat supports the following permission providers:

- [LuckPerms](https://luckperms.net/)

Please note that FairyChat will automatically detect the permission provider and use it.

*If you would like to see support for another permission provider, please open an issue.*

## Installation

You need to have [Paper](https://papermc.io/) (or fork of paper) installed on your server. FairyChat is not
compatible
with other server software.

It's also important to have MiniPlaceholders installed on your server. You can download it
from [here](https://modrinth.com/plugin/miniplaceholders).

For the best experience, you also should install the player expansion for MiniPlaceholders. You can download it
from [here](https://github.com/MiniPlaceholders/Player-Expansion).

## Support

If you need any help with FairyChat, feel free to join our [Discord server](https://discord.gg/bM8NtsJVeb).

For bug reports and feature requests, please open an issue on [GitHub](https://github.com/rexlManu/FairyChat/issues).

## Configuration

Please checkout the generated configuration file for more information on how to configure FairyChat.

## License

**FairyChat** is licensed under MIT License, which can be found [here](LICENSE).