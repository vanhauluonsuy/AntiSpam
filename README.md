# AntiSpamChat

![Minecraft](https://img.shields.io/badge/Minecraft-1.20+-brightgreen)
![Paper](https://img.shields.io/badge/Platform-Paper%20%7C%20Spigot-blue)
![License](https://img.shields.io/badge/License-Custom-lightgrey)
![Author](https://img.shields.io/badge/Author-vanhauluonsuy-purple)

---

## 🇻🇳 Giới thiệu

**AntiSpamChat** là plugin giúp **ngăn chặn spam chat** trong server Minecraft.

Plugin tự động phát hiện khi người chơi:

- Chat quá nhanh
- Gửi tin nhắn lặp lại
- Spam ký tự
- Spam chữ in hoa

Plugin được thiết kế **nhẹ – tối ưu – dễ cấu hình**, phù hợp với:

- Survival
- Skyblock
- PvP
- Minigame
- Network server

---

## 🇺🇸 Introduction

**AntiSpamChat** is a plugin that **prevents chat spam** on Minecraft servers.

The plugin automatically detects when players:

- Send messages too quickly
- Repeat the same message
- Spam characters
- Use excessive capital letters

Lightweight, optimized, and easy to configure.

---

## ✨ Features / Tính năng

- Anti chat spam detection
- Anti repeated messages
- Anti excessive characters
- Anti CAPS spam
- Configurable chat cooldown
- Customizable messages
- Lightweight & optimized

---

## ⚙️ Configuration Example

```yml
chat:
  cooldown: 2
  block-repeated: true
  max-caps: 70
  max-characters: 200

messages:
  spam: "&cBạn đang chat quá nhanh!"
  repeat: "&cKhông được gửi tin nhắn giống nhau!"
  caps: "&cKhông spam chữ IN HOA!"
