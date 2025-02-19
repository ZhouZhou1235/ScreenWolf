# 选择宠物粮

extends Control

var gamePets = []

# 载入原版宠物狼列表
func loadGamePets():
	var petList :Array = GArea.data_pets["data"]
	var pets :ItemList = $Game/GamePets
	pets.clear()
	for item in petList:
		gamePets.append(item)
		pets.add_item(item["name"]+"-"+item["info"],load(item["icon"]))

# 载入模组宠物狼列表
func loadModPets():
	var petList :Array = GArea.data_modpetList
	var pets :ItemList = $Mod/ModPets
	pets.clear()
	for pck in petList:
		pets.add_item(pck)

func _ready() -> void:
	loadGamePets()
	loadModPets()

func _on_game_pets_item_activated(index: int) -> void:
	var main :Node = self.get_node("/root/Main")
	main.selectPet(gamePets[index])

func _on_mod_pets_item_activated(index: int) -> void:
	var main :Node = self.get_node("/root/Main")
	main.selectModPet(index)
