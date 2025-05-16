# 游戏内容窗口

extends Window
class_name SCREENWOLFxWINDOW

# 窗体基本设置
func setBase():
	self.size = GArea.BASE_SCREEN_SIZE
	self.initial_position = Window.WINDOW_INITIAL_POSITION_CENTER_PRIMARY_SCREEN
	self.content_scale_size = GArea.BASE_SCREEN_SIZE
	self.content_scale_mode = Window.CONTENT_SCALE_MODE_VIEWPORT
	self.content_scale_aspect = Window.CONTENT_SCALE_ASPECT_KEEP

func _init() -> void:
	setBase()

func _on_close_requested() -> void:
	hide()
