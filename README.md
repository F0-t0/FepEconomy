![logo](https://cdn.modrinth.com/data/cached_images/69e84bdf9db4beb83098a82163f0dbdd7b41d978.png)

<p align="center">
  <img alt="Custom badge" src="https://shieldcn.dev/badge/JAVA-21-22c55e.svg?theme=orange&logo=ri%3AFaJava).svg?theme=gray&amp;logo=ri%3AFaJava&amp;label=Java" />
  <a href="https://github.com/F0-T0/FepEconomy"><img alt="badge" src="https://shieldcn.dev/github/F0-T0/FepEconomy/stars.svg" /></a>
</p>
<p align="center">
<img alt="paper" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/paper_vector.svg">
  <img alt="purpur" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/purpur_vector.svg">
  <br>
  <img alt="bukkit" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/unsupported/bukkit_vector.svg">
  <img alt="spigot" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/unsupported/spigot_vector.svg">
</p>

## Overview

**FepEconomy** is a minecraft Plugin that implements Vault economy

![poster](https://cdn.modrinth.com/data/cached_images/88c1ba6db5c04961ea1aa2d0affa6245e21ef523.png)


<details>
<summary>Text Description</summary>

FEP ECONOMY

OVERVIEW An open-source, Minecraft Vault economy plugin with asynchronous SQL and some useful features that anyone can
contribute to.

EASY NUMBERS Easily write large numbers using K, M, B, T, Q without the need to write 1500000. U can easily write 1.5M

BALTOP GUI A simple baltop GUI with multiple pages and optimized fetching

PAYHISTORY A GUI of pay history with any amount of transaction (default 100 max) and also the ability for admins to view
other players history

ASYNC DATABASE Every database call is asynchronous which means that if some operation takes really long it won't affect
the gameplay

CONFIGURABLE Most of it is configurable, at least I think it is

---

</details>

## Permissions:

| Permission               | Description                                                                                                 |
|--------------------------|-------------------------------------------------------------------------------------------------------------|
| fepeconomy.admin         | gives the player access to /eco set\|take\|reset\|add\|reload and the access to payhistory of other players |
| fepeconomy.baltop.exempt | Exempts the player from baltop so he doesn't show up                                                        |

## Commands

| Command                        | Description                                                              |
|--------------------------------|--------------------------------------------------------------------------|
| /eco take <player> <amount>    | Removes money from the specified player                                  |
| /eco give <player> <amount>    | Gives the specified player money                                         |
| /eco set <player> <amount>     | Sets the specified player money                                          |
| /eco reset <player\|all>       | Resets the money of the specified player or everyone to the start amount |
| /eco reload                    | Reloads the plugin                                                       |
| /pay <player> <amount>         | Transfers the specified amount of money to the specified player          |
| /togglepay                     | Toggles the ability for players to pay you                               |
| /payhistory <player (optional> | Shows your or the specified player's pay history                         |
| /baltop                        | Show's the balance top                                                   |
| /bal <player (optional)        | Show's your or the specified player's balance                            |

## Config.yml

```
#Should the plugin use a symbol for the currency
#or the name plular/singular
use-symbol: false
symbol: $

currency-name-singular: "dollar"
currency-name-plural: "dollars"

#The max amount of fetching per page for baltop players
# Recommended: 20
max-fetch: 20

#The maximum amount of transactions saved per player
# Recommended: 20-150
max-history: 100

#Per how many seconds should the
#Data be saved
autosave: 30

#The amount of money for the player to start with
start-amount: 50
```

## Messages.yml

```
#----------- Error Messages -----------
no-balance: "&cYou cannot withdraw negative amount"
insufficient-funds: "&cInsufficient funds"
deposit-negative: "&cCannot deposit negative amount"
args: "&cNot enough arguments"
player-not-found: "&cCould not find player %player%"
same-player: "&cYou cannot send money to yourself"

#----------- /bal Command -----------
# %bal% - Balance
# %player% - Player
balance-self: "&aYour balance is %bal%"
balance-other: "&a%player%'s Balance is %bal%"


#----------- /eco Command -----------
#%amount% - amount
#%player% - the player
set: "&aSet %player%'s balance to %amount%"
add: "&aAdded %amount% to %player%'s balance"
take: "&aTook %amount% from %player%'s balance"

#----------- /pay Command -----------
#%amount% - amount
#%receiver% - receiver
#%sender% - sender

invalid-number: "&cInvalid number format"
sent: "&aSuccessfully transferred %amount% to %receiver%"
received: "&aReceived a payment of %amount% from %sender%"
player-turned-off-payments: "&c%receiver% has turned off payments"

#----------- /togglePay Command -----------
turned-off: "&aSuccessfully &cturned off &apayments"
turned-on: "&aSuccessfully turned on payments"

#----------- /eco reload -----------
reload: "&aConfiguration reloaded successfully"

#----------- /eco reset -----------
reset: "&aReset %player%'s balance to %amount%"
reset-all: "&aReset %count% players to %amount%"

#---- Menu Globals----
nextPage-name: "&6Next Page"
previousPage-name: "&6Previous Page"

#----------- Transaction History -----------

guiTitle: "&aTransaction History"

status-received: "&a&lRECEIVED"
status-sent: "&4&lSENT"

Transaction-name: "&b&lTRANSACTION "
Transaction-Lore:
  - "&7- &f&lAmount: &b&l%amount%"
  - "&7- &f&lSender: &b&l%sender%"
  - "&7- &f&lReceiver: &b&l%receiver%"
  - "&7- &f&lStatus: &b&l%status%"
  - "&7- &f&lTime: &b&l%time%"

#----------- BalTop -----------
balTop-Title: "&6BalTOP"

head-name: "&f%place%. &6%player%"
head-lore:
  - ""
  - "&7- &f&lBalance: &b&l%bal%"
```
