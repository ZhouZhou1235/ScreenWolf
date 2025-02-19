# 全局空间

extends Node

const GAME_TITLE = "屏幕有狼"
const GAME_LOGO_URL = "res://assets/image/icon/gamelogo.png"
const BASE_SCREEN_SIZE = Vector2(1280,720) # 程序原始屏幕大小
const USER_SAVE = "../user/" # 数据储存位置
var SCREEN :Window # 运行的屏幕
var POLYGON :Polygon2D # 渲染的多边形
var MOUSE_POS :Vector2 # 全局实际鼠标位置
var SCALE_VEC :Vector2 # 屏幕缩放向量

var data_pets :Dictionary # 原版宠物狼
var data_modpetList :Array # 模组宠物狼资源列表
var activePetSave :Dictionary # 活动的宠物数据

# 加载游戏数据
func loadData():
	data_pets = jsonDecode(getFromFile("res://data/pets.json"))
	data_modpetList = scanDir(USER_SAVE+"mod/")
	loadModWolfToRes(data_modpetList)

# 窗体初始化
func initScreen():
	SCREEN = self.get_viewport().get_window()
	SCREEN.size = DisplayServer.screen_get_size()
	SCREEN.position = Vector2(0,1)
	SCALE_VEC = Vector2(
		GArea.SCREEN.size.x/GArea.BASE_SCREEN_SIZE.x,
		GArea.SCREEN.size.y/GArea.BASE_SCREEN_SIZE.y
	)
	# 不要设置为Vector2(0,0)
	# 零坐标会被Windows认为是全屏游戏程序导致黑屏

# 更新透明窗体的多边形鼠标穿透
func updateMousePassthrough():
	var x: PackedVector2Array = POLYGON.polygon
	for i in range(x.size()):
		x[i] = POLYGON.to_global(x[i])
	DisplayServer.window_set_mouse_passthrough(x)

# 解析json对象
func jsonDecode(jsonStr): return JSON.parse_string(jsonStr)

# 编码json对象
func jsonEncode(obj): return JSON.stringify(obj,"\t")

# 保存内容到文件
func saveToFile(pathname,content):
	var f = FileAccess.open(pathname,FileAccess.WRITE)
	f.store_string(content)
	f.close()

# 从文件读取内容
func getFromFile(pathname):
	var f = FileAccess.open(pathname,FileAccess.READ)
	var content = f.get_as_text()
	return content

# 从对象数组找到元素
func findFromObjArray(arr:Array,key:String,value:String,getIndex=false):
	var result = null
	var index = 0
	for item in arr:
		if item[key]==value:
			result = item
			break
		index += 1
	if getIndex:
		if index==len(arr): return null
		return index
	return result

# 实例化一个场景
func createNode(url): return load(url).instantiate()

# 得到两点之间距离
func getLengthFrom2Pos(pos1:Vector2,pos2:Vector2):
	return sqrt(pow(pos1.x-pos2.x,2)+pow(pos1.y-pos2.y,2))

# 将字符串以字符区分分成数组
func stringToArrayByChar(s:String,c:String):
	var arr :PackedStringArray = s.split(c)
	var theArr = []
	for i in arr:
		if i!="":
			theArr.append(i)
	return theArr

# 屏幕随机取点
func randomPointOnScreen():
	return Vector2(int(randf()*SCREEN.size.x),int(randf()*SCREEN.size.y))

# 遍历文件夹返回内容数组
func scanDir(path:String):
	var result = []
	var dir = DirAccess.open(path)
	if dir:
		dir.list_dir_begin()
		var fileName = "begin"
		while fileName!="":
			fileName = dir.get_next()
			if fileName!="":
				result.append(fileName)
		dir.list_dir_end()
	return result

# 将模组宠物狼载入文件系统
func loadModWolfToRes(modpetList):
	for pck in modpetList:
		ProjectSettings.load_resource_pack(USER_SAVE+"mod/"+pck)
