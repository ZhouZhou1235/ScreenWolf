# 宠物狼 蓝狼

extends SCREENWOLF_PET

func _ready() -> void:
	super._ready()
	speed = 400
	followLength = 75

func _on_auto_long_timeout() -> void:
	if isFree():
		autoTargetPos = GArea.randomPointOnScreen()/GArea.SCALE_VEC
		followSelfMove = true

func _on_auto_short_timeout() -> void:
	if isFree():
		var num = int(randf()*9)+1
		if num<2:
			doSleep()
		if num<6:
			$Appreance.play("wolf_bite")
		else:
			$Appreance.play("wolf_jump")

func _on_appreance_animation_finished() -> void:
	var appreance :AnimatedSprite2D = $Appreance
	if appreance.animation=="wolf_bite" or appreance.animation=="wolf_jump":
		petDefault()
