# PatchouliQuests

Patchouli Quests Book.

## json

```json5
{
    "type": "collect_task",
    "collect": {"tag:minecraft:planks": 3,"minecraft:apple": 10},
    "finish_cmd": ["kill @a"],
    "consume": false,
    "reward": ["minecraft:apple#4"],
    "hide": true,
    "lock": true
}
```

```json5
{
    "type": "near_position_task",
    "x": 0,
    "y": 0,
    "z": 60,
    "range": 100,
    "finish_cmd": [],
    "reward": ["minecraft:apple#4"],
    "hide": true,
    "lock": true
}
```

## Commands

```text
patchouli_quests task @a <book> <entry> <id> <hide|un_hide|lock|un_lock>
```