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
- **Legacy Support**: Legacy Color codes are supported, so you don't have to worry about your old chat formats.
  PlaceholderAPI is also supported.
- **Optional Multi-Server Message Sending**: Have multiple servers? No problem. FairyChat supports sending messages
  across servers with Redis.
- **Fully Paper Compatible**: FairyChat is fully compatible with Paper, and is optimized to provide the best
  performance.

## Documentation

You can find the documentation for FairyChat [here](https://github.com/rexlManu/FairyChat/wiki).

## Commands & Permissions

| Command                    | Permission                      | Description                              |
|----------------------------|---------------------------------|------------------------------------------|
| `/broadcast <message>`     | `fairychat.command.broadcast`   | Broadcast a message to all servers       |
|                            | `fairychat.feature.minimessage` | Allows MiniMessage & Color Codes in chat |
| `/pm <username> <message>` |                                 | Send a private message to a player       |
| `/r <message>`             |                                 | Reply to a private message               |
| `/ignore <username>`       |                                 | Toggle ignoring a player                 |
|                            | `fairychat.bypass.ignore`       | Bypass ignoring a player                 |
|                            | `fairychat.notify-update`       | Notify about updates on join             |

## Installation

You need to have [Paper](https://papermc.io/) (or fork of paper) installed on your server. FairyChat is not
compatible
with other server software.

1. Download the latest FairyChat version from [here](https://github.com/rexlManu/FairyChat/releases).
2. Place the downloaded jar file in your server's `plugins` folder.
3. Restart your server.
4. Configure FairyChat to your liking.
5. Restart your server again.

## Support

If you need any help with FairyChat, feel free to join our [Discord server](https://discord.gg/bM8NtsJVeb).

For bug reports and feature requests, please open an issue on [GitHub](https://github.com/rexlManu/FairyChat/issues).

## Configuration

Please checkout the generated configuration file for more information on how to configure FairyChat.

## License

**FairyChat** is licensed under MIT License, which can be found [here](LICENSE).