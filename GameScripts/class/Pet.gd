# 宠物狼类

extends CharacterBody2D
class_name SCREENWOLF_PET

var strokeNeedNum = 100
var strokeNum = 0
var speed = 250
var followLength = 100
var stroke :bool = false
var isClick :bool = false
var canDrag :bool = false
var inProgress :bool = false
var indicate :bool = false
var isSleep :bool = false
var followSelfMove = false
var offsetLength = Vector2()
var targetPos = Vector2()
var autoTargetPos = Vector2()
var myPolygon :Polygon2D
var petInfo :Dictionary = {
	"id":"WOLF_PET",
	"name":"wolfname",
	"icon":"...",
	"info":"...",
	"res":"...",
	"save":"..."
}
var initSave :Dictionary = {
	"info":"...",
	"dialog":[
		{
			"text":"...",
			"reply":"..."
		}
	],
	"reading":[]
}

func isFree(): return !inProgress and !indicate and !canDrag and !isClick and !isSleep

# 自主移动
func autoMove(delta):
	if followSelfMove and !inProgress:
		var appreance :AnimatedSprite2D = $Appreance
		gotoPoint(autoTargetPos,delta)
		if appreance.animation!="move": appreance.play("move")
		if self.position==autoTargetPos:
			followSelfMove = false
			appreance.play("default")

# 翻转
func checkFlip():
	var appreance :AnimatedSprite2D = $Appreance
	if !isSleep:
		if !followSelfMove:
			var x = GArea.MOUSE_POS.x/GArea.SCALE_VEC.x-self.position.x
			if x>0 and appreance.flip_h: appreance.flip_h=false
			elif x<0 and !appreance.flip_h: appreance.flip_h=true
		else:
			var x = autoTargetPos.x-self.position.x
			if x>0 and appreance.flip_h: appreance.flip_h=false
			elif x<0 and !appreance.flip_h: appreance.flip_h=true

# 睡觉
func doSleep():
	petDefault()
	$Appreance.play("sleep")
	inProgress = true
	isSleep = true

# 被摸摸
func playStroke():
	if isClick or inProgress:
		strokeNum = 0
	if strokeNum>strokeNeedNum and !stroke:
		stroke = true
		$Appreance.play("stroke")

# 一个物理帧向指定方向移动
func gotoPoint(pos:Vector2,delta):
	self.position = self.position.move_toward(pos,delta*speed)

# 移动
func petMove(delta):
	if isClick and canDrag:
		self.position = GArea.MOUSE_POS/GArea.SCALE_VEC+offsetLength
	if indicate and !isClick:
		targetPos = GArea.MOUSE_POS/GArea.SCALE_VEC
		var length = GArea.getLengthFrom2Pos(self.position,targetPos)
		if length>followLength:
			if $Appreance.animation!="move": $Appreance.play("move")
			gotoPoint(targetPos,delta)
		else:
			if $Appreance.animation!="focus": $Appreance.play("focus")
	move_and_slide()

# 播放动画
func playAnimation(animationName:String):
	var sprite :AnimatedSprite2D = $Appreance
	sprite.play(animationName)

# 更新磁盘活动数据
func updateSave():
	var url = GArea.USER_SAVE+petInfo["save"]+".json"
	var dialogurl = GArea.USER_SAVE+"dialog/"+petInfo["save"]+".txt"
	var readingurl = GArea.USER_SAVE+"reading/"+petInfo["save"]+".txt"
	if !FileAccess.file_exists(url): GArea.saveToFile(url,GArea.jsonEncode(initSave))
	if !FileAccess.file_exists(dialogurl): GArea.saveToFile(dialogurl,"")
	else:
		# 将文本变成数组
		var theSave = GArea.jsonDecode(GArea.getFromFile(url))
		var theArr = GArea.stringToArrayByChar(GArea.getFromFile(dialogurl),"\n")
		var saveArr = []
		# 更新对话
		if len(theArr)%2==0:
			var index = 0
			while index<len(theArr):
				var obj = {
					"text":theArr[index],
					"reply":theArr[index+1]
				}
				saveArr.append(obj)
				index += 2
			if theSave["dialog"]!=saveArr:
				theSave["dialog"] = saveArr
				GArea.saveToFile(url,GArea.jsonEncode(theSave))
	if !FileAccess.file_exists(readingurl): GArea.saveToFile(readingurl,"")
	else:
		# 将文本变成数组
		var theSave = GArea.jsonDecode(GArea.getFromFile(url))
		var theArr = GArea.stringToArrayByChar(GArea.getFromFile(readingurl),"\n")
		var saveArr = []
		# 更新对话
		if len(theArr)>0:
			for i in theArr:
				saveArr.append(i)
			if theSave["reading"]!=saveArr:
				theSave["reading"] = saveArr
				GArea.saveToFile(url,GArea.jsonEncode(theSave))
	GArea.activePetSave = GArea.jsonDecode(GArea.getFromFile(GArea.USER_SAVE+petInfo["save"]+".json"))

# 保存对话
func saveDialog(content:String):
	var dialogurl = GArea.USER_SAVE+"dialog/"+petInfo["save"]+".txt"
	GArea.saveToFile(dialogurl,content)
	updateSave()

# 保存阅读
func saveReading(content:String):
	var readingurl = GArea.USER_SAVE+"reading/"+petInfo["save"]+".txt"
	GArea.saveToFile(readingurl,content)
	updateSave()

# 基本设置
func setBase():
	self.input_pickable = true

# 更新数据
func updateData(info=null):
	myPolygon = $Polygons/Polygon
	if info:
		petInfo = info
		$PetPenal.setPenalInfo(petInfo)

# 回到默认状态
func petDefault():
	isClick = false
	canDrag = false
	inProgress = false
	indicate = false
	stroke = false
	followSelfMove = false
	isSleep = false
	strokeNum = 0
	offsetLength = Vector2()
	GArea.POLYGON = $Polygons/Polygon
	$Appreance.play("default")
	$Appreance.flip_h = false

# 显示面板
func showPenal():
	if !indicate:
		GArea.POLYGON = $Polygons/PolygonMenu
	else:
		GArea.POLYGON = $Polygons/Polygon

# 显示对话框
func showDialog():
	GArea.POLYGON = $Polygons/PolygonDialog
	inProgress = true

func _mouse_enter() -> void:
	if !inProgress:
		$Appreance.play("focus")

func _mouse_exit() -> void:
	strokeNum = 0
	stroke = false
	if !inProgress:
		$Appreance.play("default")

func _ready() -> void:
	setBase()
	updateData()
	petDefault()

func _physics_process(delta: float) -> void:
	petMove(delta)
	playStroke()
	checkFlip()
	autoMove(delta)

func _input(event):
	if event is InputEventMouseButton:
		if event.pressed:
			if event.get_indexed("button_index")==MOUSE_BUTTON_LEFT:
				if canDrag and !inProgress:
					isClick = true
					$Appreance.play("drag")
					var clickPos = event.position/GArea.SCALE_VEC
					offsetLength = (self.position-clickPos)
			if event.get_indexed("button_index")==MOUSE_BUTTON_RIGHT:
				if GArea.POLYGON == $Polygons/Polygon:
					showPenal()
				else:
					petDefault()
			if event.get_indexed("button_index")==MOUSE_BUTTON_MIDDLE:
				if !indicate and !inProgress:
					inProgress = true
					indicate = true
				else:
					petDefault()
		else:
			isClick = false
			if !inProgress:
				$Appreance.play("focus")
	if event is InputEventMouseMotion:
		if event.velocity!= Vector2.ZERO:
			strokeNum += 1
