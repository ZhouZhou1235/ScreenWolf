# 程序根树

extends Node

var activePetInfo :Dictionary
var activePet :SCREENWOLF_PET = null

# 选择模组宠物狼
func selectModPet(index):
	var pck :String = GArea.data_modpetList[index]
	var tscnUrl = "res://"+pck.replace(".pck",".tscn")
	var obj :SCREENWOLF_PET = GArea.createNode(tscnUrl)
	activePet = obj
	activePetInfo = obj.petInfo
	$Game/Welcome.showDefault()
	$Game/Welcome/Title.text = pck

# 选择宠物狼
func selectPet(info:Dictionary):
	var obj = GArea.createNode(info["res"])
	if obj is SCREENWOLF_PET:
		activePet = obj
		activePetInfo = info
		$Game/Welcome.showSelectedPets(info)

# 返回大厅
func backToHall():
	GArea.POLYGON = $Game/Welcome/Polygon
	$Game/Welcome.show()
	for node in $Game/Pet/MyWolf.get_children(): node.queue_free()
	activePet = null
	activePetInfo = {}
	$Game/Welcome.showDefault()

# 开始游戏
func playPet():
	if activePet:
		$Game/Welcome.hide()
		$Windows.hideWindows()
		activePet.updateData(activePetInfo)
		activePet.updateSave()
		activePet.position = $Game/Pet/BeginPos.position
		$Game/Pet/MyWolf.add_child(activePet)
		GArea.POLYGON = activePet.myPolygon

# 缩放以适配屏幕
func scaleToScreen():
	$Game.scale = GArea.SCALE_VEC

func _init() -> void:
	GArea.loadData()

func _ready() -> void:
	GArea.initScreen()
	scaleToScreen()

func _physics_process(_delta: float) -> void:
	GArea.updateMousePassthrough()
	GArea.MOUSE_POS = DisplayServer.mouse_get_position()

func _on_select_pressed() -> void:
	$Windows/SelectPet.show()

func _on_exit_pressed() -> void:
	self.get_tree().quit()

func _on_start_pressed() -> void:
	playPet()

func _on_about_pressed() -> void:
	$Windows/About.show()
