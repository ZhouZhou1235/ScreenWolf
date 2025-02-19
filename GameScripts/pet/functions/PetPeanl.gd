# 宠物面板

extends Node2D

var pet :SCREENWOLF_PET

# 设置面板信息
func setPenalInfo(info:Dictionary):
	$PetName.text = info["name"]

func _ready() -> void:
	pet = self.get_parent()

func _on_dialog_pressed() -> void:
	pet.showDialog()
	pet.get_node("PetDialog").playDialog()

func _on_reading_pressed() -> void:
	pet.showDialog()
	pet.get_node("PetDialog").playReading()

func _on_back_pressed() -> void:
	self.get_node("/root/Main").backToHall()

func _on_show_input_pressed() -> void:
	pet.showDialog()
	pet.get_node("PetDialog").playShowInput()

func _on_sleep_pressed() -> void:
	pet.doSleep()
