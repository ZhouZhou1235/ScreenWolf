# 欢迎组件

extends Node2D

var isClick = false
var offsetLength = Vector2()
var canMove = false

# 展示选择的宠物狼
func showSelectedPets(info:Dictionary):
	var icon :Sprite2D = $Icon
	var title :Label = $Title
	icon.texture = load(info["icon"])
	title.text = info["name"]

# 显示默认外观
func showDefault():
	var icon :Sprite2D = $Icon
	var title :Label = $Title
	icon.texture = load(GArea.GAME_LOGO_URL)
	title.text = GArea.GAME_TITLE

func _physics_process(_delta: float) -> void:
	if isClick and canMove:
		self.position = GArea.MOUSE_POS/GArea.SCALE_VEC+offsetLength

func _input(event):
	if event is InputEventMouseButton:
		if event.pressed:
			isClick = true
			var clickPos = event.position/GArea.SCALE_VEC
			offsetLength = self.position-clickPos
		else:
			isClick = false

func _ready() -> void:
	GArea.POLYGON = $Polygon

func _on_icon_area_mouse_entered() -> void:
	canMove = true

func _on_icon_area_mouse_exited() -> void:
	canMove = false
