# 宠物狼身体区域

extends Area2D

var pet :SCREENWOLF_PET

func _ready() -> void:
	pet = self.get_parent()

func _mouse_enter() -> void:
	pet.canDrag = true

func _mouse_exit() -> void:
	pet.canDrag = false
