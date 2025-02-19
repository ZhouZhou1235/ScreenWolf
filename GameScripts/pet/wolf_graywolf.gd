# 宠物狼 大灰狼

extends SCREENWOLF_PET

func _ready() -> void:
	super._ready()
	speed = 240
	followLength = 150
	strokeNeedNum = 200

func _on_long_timer_timeout() -> void:
	if isFree():
		autoTargetPos = GArea.randomPointOnScreen()/GArea.SCALE_VEC
		followSelfMove = true

func _on_short_timer_timeout() -> void:
	if isFree():
		if $Appreance.animation!="sitdown": $Appreance.play("sitdown")
