# 桌宠窗口

extends Node

# 隐藏所有窗口
func hideWindows():
	for item:SCREENWOLFxWINDOW in self.get_children():
		item.hide()

func _ready() -> void:
	hideWindows()
