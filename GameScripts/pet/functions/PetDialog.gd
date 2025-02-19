# 宠物狼的对话框

extends Node2D

var pet :SCREENWOLF_PET
var dialogIndex = 0
var readIndex = 0
var dialogList :Array
var readList :Array

# 播放输入事件动画
func playInputEventAnimation(keycodeText:String):
	if keycodeText!="Unknown":
		$ShowInput/Event.text = keycodeText
		$ShowInput/Animation.play("ShowtEvent")

# 播放输入换行动画
func playInputTextLineAnimation(text:String):
	$ShowInput/TextLine.text = text
	$ShowInput/Animation.play("ShowTextLine")

# 打开对话
func playDialog():
	GArea.POLYGON = pet.get_node("Polygons/PolygonDialog")
	$DialogBox.show()
	$ReadingBox.hide()
	$ShowInput.hide()
	dialogList = GArea.activePetSave["dialog"]
	var appreance :AnimatedSprite2D = pet.get_node("Appreance")
	appreance.play("dialog")
	var length = len(dialogList)
	if length>0:
		$DialogBox/TextLabel.text = dialogList[dialogIndex]["text"]
		$DialogBox/Down.text = dialogList[dialogIndex]["reply"]
		dialogIndex += 1
		if dialogIndex>length-1: dialogIndex=0

# 打开阅读
func playReading():
	GArea.POLYGON = pet.get_node("Polygons/PolygonReading")
	$DialogBox.hide()
	$ReadingBox.show()
	$ShowInput.hide()
	$ReadingBox/Next.disabled = false
	$ReadingBox/Down.disabled = true
	readList = GArea.activePetSave["reading"]
	var appreance :AnimatedSprite2D = pet.get_node("Appreance")
	appreance.play("reading")
	var length = len(readList)
	if length>0:
		if readIndex>=length-1: readIndex=0
		$ReadingBox/TextLabel.text = readList[readIndex]

# 打开输入
func playShowInput():
	GArea.POLYGON = pet.get_node("Polygons/PolygonShowInput")
	$DialogBox.hide()
	$ReadingBox.hide()
	$ShowInput.show()
	var appreance :AnimatedSprite2D = pet.get_node("Appreance")
	appreance.play("showinput")

func _ready() -> void:
	pet = self.get_parent()

func _input(event: InputEvent) -> void:
	if (event is InputEventKey) and event.is_pressed():
		var x = event.as_text()
		playInputEventAnimation(x)

func _on_down_pressed() -> void:
	pet.petDefault()

func _on_next_pressed() -> void:
	if readIndex<len(readList)-1:
		readIndex += 1
		$ReadingBox/TextLabel.text = readList[readIndex]
	else:
		$ReadingBox/Next.disabled = true
		$ReadingBox/Down.disabled = false

func _on_text_area_lines_edited_from(from_line: int, to_line: int) -> void:
	if from_line!=to_line:
		var s :String = $ShowInput/TextArea.text
		var arr :Array = GArea.stringToArrayByChar(s,"\n")
		if len(arr)>0:
			playInputTextLineAnimation(arr[-1])
		else:
			$ShowInput/TextLine.text = "......"
			$ShowInput/Event.text = "......"

func _on_input_paste_pressed() -> void:
	if DisplayServer.clipboard_has():
		var x = DisplayServer.clipboard_get()
		$ShowInput/TextArea.text = x+"\n"

func _on_input_copy_pressed() -> void:
	var x = $ShowInput/TextArea.text
	DisplayServer.clipboard_set(x)

func _on_save_to_dialog_pressed() -> void:
	pet.saveDialog($ShowInput/TextArea.text)

func _on_save_to_read_pressed() -> void:
	pet.saveReading($ShowInput/TextArea.text)
