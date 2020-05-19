**This mod requires Vazkii's [Patchouli](https://www.curseforge.com/minecraft/mc-mods/patchouli) Mod to launch.
Don't Forget to install it!**  

Yes, this mod bring Patchouli Guidebooks a new function:
It can act as a Quest Book now.
![cf2224225650.png](https://i.loli.net/2020/05/14/LerZBDQ4PlYdVN7.png)  

Trust me, this is the most interesting and convenient way to make quests,
before other quest mods is fully developed!

**The mod is WIP. There are many bug in it. Please wait Release version.**

## How to use?

First, check out this article [How to use Patchouli](https://github.com/Vazkii/Patchouli/wiki).  
Then, simply use Patchouli Quests Mod's new Page Type to write down your quests.(For v1.3.3)
Just like this:


```  
{
    "type": "collect_task",
    "collect": {"tag:minecraft:planks": 3,"minecraft:apple": 10},
    "finish_cmd": ["kill @a"],
    "consume": false,
    "reward": ["minecraft:apple#4"],
    "hide": true,
    "lock": true,
    "id": 1
}
```

```  
{
    "type": "near_position_task",
    "x": 0,
    "y": 0,
    "z": 60,
    "range": 100,
    "finish_cmd": [],
    "reward": ["minecraft:apple#4"],
    "hide": true,
    "lock": true,
    "id": 1
}
```

You write down the required item of the task as the value of key `collect`,  
and the reward items given to the player as the value of key  `reward`.

We also have command   

patchouli_quests task @a &lt;book&gt; &lt;entry&gt; &lt;id&gt; &lt;hide|un_hide|lock|un_lock&gt;

to hide/un_hide/lock_unlock the task.
Easy, right?
Go ahead, use this mod to show off your quest-making creativity!

## Features in the Future
more tasks.

Thanks for your downloading!
Feel free to write issues if you need our help!



