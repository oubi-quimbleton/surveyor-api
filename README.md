# Surveyor (Legacy)
A retired Minecraft plugin originally designed to copy chunks between worlds using Bukkit‑level APIs. This version of Surveyor predates the modern NBT‑driven architecture and is preserved here for historical reference, debugging, and migration purposes.

# Overview
Surveyor (Legacy) provided basic utilities for:

Copying chunks from one world to another

Handling block‑by‑block transfers

Performing simple world manipulation tasks

Supporting early versions of Manifest Destiny’s world‑import workflow

This implementation relied heavily on Bukkit APIs and synchronous operations, which made it functional but not suitable for large‑scale or high‑performance chunk operations.

# Status
This project is deprecated and no longer maintained.
It exists solely to preserve the original implementation while development continues on the modern rewrite:

👉 Surveyor Engine — a standalone, NBT‑level chunk manipulation library with no commands, no gameplay logic, and a clean API surface.

# Why Keep This Code?
Reference for migration to Surveyor Engine

Historical context for architectural decisions

Useful for comparing old vs. new chunk‑copying behavior

Helps ensure feature parity during the rewrite

# License
This legacy code is provided as‑is. You may use or reference it freely.