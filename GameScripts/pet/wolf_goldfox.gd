# 宠物狼 金光狐狸

extends SCREENWOLF_PET

func _ready() -> void:
	super._ready()
	speed = 300
	followLength = 50

func _on_long_timer_timeout() -> void:
	if isFree():
		autoTargetPos = GArea.randomPointOnScreen()/GArea.SCALE_VEC
		followSelfMove = true

func _on_short_timer_timeout() -> void:
	if isFree():
		$Appreance.play("jump")
		$Timers/RangeTimer.start()

func _on_range_timer_timeout() -> void:
	if isFree():
		if $Appreance.animation=="jump": $Appreance.play("default")
