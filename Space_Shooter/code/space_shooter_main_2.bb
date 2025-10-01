AppTitle "Spaceshooter Port from Pygame"
Const SCREEN_WIDTH = 1280 : Const SCREEN_HEIGHT = 720
Graphics SCREEN_WIDTH,SCREEN_HEIGHT,0,2
SetBuffer BackBuffer()
;Const FPS_CAP = 60
;fps_timer = CreateTimer(FPS_CAP)
SeedRnd MilliSecs()
TFormFilter False

Const IMAGE_PATH$ = "../images/"
Const SOUND_PATH$ = "../audio/"
Const A_KEY = 30 : Const D_KEY = 32 
Const S_KEY = 31 : Const W_KEY = 17
Const SPACE_KEY = 57 : Const RETURN_KEY = 28

;bgm_channel = PlayMusic(SOUND_PATH$+"Echoes_Of_The_Past.mp3")

explosion_sound = LoadSound(SOUND_PATH$ + "explosion.wav")
player_damage_sound = LoadSound(SOUND_PATH$ + "damage.wav")
SoundVolume player_damage_sound,0.5
player_blaster_sound = LoadSound(SOUND_PATH$ + "laser.wav")
SoundVolume player_blaster_sound,0.1
LoopSound(player_blaster_sound)

ready_girl_vs = LoadSound(SOUND_PATH$ + "ready_girl_voice.wav")

Dim girly_hurt_sounds(2)
For s_num = 0 To 2
	girly_hurt_sounds(s_num) = LoadSound(SOUND_PATH$ + "girl_hurt_"+Str(s_num+1)+".wav")
Next


font_large_100 = LoadFont("Oxanium.ttf",100) ;has to be in root dir to work and renamed to meta file name
font_normal_50 = LoadFont("Oxanium.ttf",50)
font_small_25 = LoadFont("Oxanium.ttf",25)


player_image = LoadImage(IMAGE_PATH$+"player.png")
bullet_image = LoadImage(IMAGE_PATH$+"laser.png")

asteroid_image = LoadImage(IMAGE_PATH$+"meteor.png")
MidHandle asteroid_image
Global max_rotation_frames = 24
Dim asteroid_rotation_frames(max_rotation_frames-1)
angle_deg# = 0
For frame_num = 0 To max_rotation_frames-1
	temp_img = CopyImage(asteroid_image)
	RotateImage temp_img,angle_deg#
	asteroid_rotation_frames(frame_num) = temp_img
	angle_deg# = angle_deg# + 15
Next

Global max_explosion_frames = 21
Dim explosion_frames(max_explosion_frames -1)
For e_frame_num = 0 To max_explosion_frames-1
	explosion_frames(e_frame_num) = LoadImage(IMAGE_PATH$+"explosion\"+Str(e_frame_num)+".png")
	MidHandle explosion_frames(e_frame_num)
Next
Type Explosion
	Field image
	Field x#,y#
	Field frame_index#
	Field animation_speed#
End Type
Function Init_Explosion.Explosion(x,y,animation_speed)
	e.Explosion = New Explosion
	e\frame_index# = 0
	e\image = explosion_frames(e\frame_index)
	e\x# = x
	e\y# = y
	e\animation_speed# = animation_speed
	
	Return e.Explosion
End Function
Function Update_Explosion(e.Explosion,delta_time#)
	e\frame_index# = e\frame_index# + e\animation_speed# * delta_time#
	If e\frame_index# < max_explosion_frames-1 Then
		e\image = explosion_frames(Int(e\frame_index))
	Else
		Delete e
	EndIf
End Function
Function Draw_Explosion(e.Explosion)
	If e <> Null Then
		DrawImage e\image,e\x#,e\y#
	EndIf
End Function



star_image = LoadImage(IMAGE_PATH$+"star2.png")
MaskImage star_image,255,0,110
Global star_count = 0
Type Star
	Field image
	Field x#,y#
	Field speed#
End Type
Function Init_Star.Star(image,x,y,speed)
	star.Star = New Star
	star\image = image
	star\x# = x
	star\y# = y
	star\speed# = speed
	star_count = star_count + 1
	Return star.Star
End Function
Function Update_Star(star.Star,delta_time#)
	star\y# = star\y# + star\speed# * delta_time#
	
	If star\y# > SCREEN_HEIGHT Then
		Delete star
		star_count = star_count - 1
	EndIf
	
End Function
Function Draw_Star(star.Star)
	If star <> Null Then
		DrawImage star\image,star\x#,star\y
	EndIf
End Function


;Global asteroid_count = 0
Type Asteroid
	Field image
;	Field original_image
;	Field rotation_angle#
;	Field rotation_speed#
	Field anim_dir
	Field anim_speed#
	Field frame_index#
	Field x#,y#
	Field vector2#[1]
	Field speed#
	Field start_time
	Field life_time
End Type
Function Init_Asteroid.Asteroid(x,y,speed)
	a_roid.Asteroid = New Asteroid
;	MidHandle image
;	a_roid\original_image = image
	
	a_roid\anim_speed# = Rand(5,24)
	a_roid\anim_dir = Rand(-1,1)
	If a_roid\anim_dir = -1 Then
		a_roid\frame_index# = 9
	Else
		a_roid\frame_index# = 0
	EndIf
	a_roid\image = asteroid_rotation_frames(a_roid\frame_index)
	a_roid\x# = x
	a_roid\y# = y
	a_roid\vector2#[0] = Rnd(-0.5,0.5)
	a_roid\vector2#[1] = 1.0
	a_roid\speed# = speed
	a_roid\start_time = MilliSecs()
	a_roid\life_time = 3000
;	a_roid\rotation_angle# = 0
;	a_roid\rotation_speed# = Rand(-75,75)
;	asteroid_count = asteroid_count + 1
	Return a_roid.Asteroid
End Function
Function Update_Asteroid(a_roid.Asteroid,delta_time#)
	a_roid\x# = a_roid\x# + (a_roid\vector2#[0] * a_roid\speed#  * delta_time#)
	a_roid\y# = a_roid\y# + (a_roid\vector2#[1] * a_roid\speed# * delta_time#)
	
;	a_roid\rotation_angle# = a_roid\rotation_angle# + (a_roid\rotation_speed# * delta_time#)
;	If Abs(a_roid\rotation_angle#) > 360 Then
;		a_roid\rotation_angle# = 0
;	EndIf
;	a_roid\image = CopyImage(a_roid\original_image)
;	
;	RotateImage a_roid\image,a_roid\rotation_angle#
	
	Select a_roid\anim_dir
		Case 1
			a_roid\frame_index# = a_roid\frame_index# +(a_roid\anim_speed# * delta_time#)
			If a_roid\frame_index# > max_rotation_frames-1 Then
				a_roid\frame_index# = 0
			EndIf
		Case -1
			a_roid\frame_index# = a_roid\frame_index# -(a_roid\anim_speed# * delta_time#)
			If a_roid\frame_index# < 0 Then
				a_roid\frame_index# = max_rotation_frames-1
			EndIf
		Default
			a_roid\frame_index# = 0
	End Select
	
	a_roid\image = asteroid_rotation_frames(Int(a_roid\frame_index))
	
	If MilliSecs() > a_roid\start_time + a_roid\life_time Then
		Delete a_roid
;		asteroid_count = asteroid_count-1
	EndIf
	
End Function
Function Draw_Asteroid(a_roid.Asteroid)
	If a_roid <> Null Then
		DrawImage a_roid\image,a_roid\x#,a_roid\y#
	EndIf
End Function


Type Blaster_Bolt
	Field image
	Field x#,y#
	Field speed#
End Type
Function Init_Bolt.Blaster_Bolt(image,x,y,speed)
	bolt.Blaster_Bolt = New Blaster_Bolt
	MidHandle image
	bolt\image = image
	bolt\x# = x
	bolt\y# = y
	bolt\speed# = speed
End Function
Function Update_Bolt(bolt.Blaster_Bolt,delta_time#)
	bolt\y# = bolt\y# - bolt\speed# * delta_time#
	
	If (bolt\y + ImageHeight(bolt\image)/2) < 0 Then
		Delete bolt
	EndIf
End Function
Function Draw_Bolt(bolt.Blaster_Bolt)
	If bolt <> Null Then
		DrawImage bolt\image,bolt\x#,bolt\y#
	EndIf
End Function

Type Player
	Field image
	Field x#,y#
	Field vector2#[1]
	Field speed#
	Field can_shoot
	Field shoot_timer#
	Field cooldown_duration#
	Field armor_points
	Field blast_sound_channel
	Field blaster_max_charge#
	Field blast_energy_per_shot#
	Field blaster_current_charge#
	Field blaster_recharge_rate#
End Type
Function Init_Player.Player(image,x,y,speed)
	player.Player = New Player
	MidHandle image
	player\image = image
	player\x# = x
	player\y# = y
	player\speed# = speed
	player\can_shoot = True
	player\shoot_timer = 0
	player\cooldown_duration = 500
	player\armor_points = 3
	player\blaster_max_charge# = 100
	player\blast_energy_per_shot# = 10
	player\blaster_current_charge# = player\blaster_max_charge#
	player\blaster_recharge_rate# = 5
	Return player.Player
End Function
Function Get_Player_Input(player.Player,bullet_image,player_blaster_sound)
	player\vector2#[0] = KeyDown(D_KEY) - KeyDown(A_KEY)
	player\vector2#[1] = KeyDown(S_KEY) - KeyDown(W_KEY)
	
	If KeyHit(SPACE_KEY) And player\can_shoot And player\blaster_current_charge >= player\blast_energy_per_shot Then
		player\blast_sound_channel = PlaySound(player_blaster_sound)
		bolt.Blaster_Bolt = Init_Bolt.Blaster_Bolt(bullet_image, player\x#-4-ImageWidth(bullet_image)/2,player\y#-ImageHeight(player\image)/2,600)
		bolt_2.Blaster_Bolt = Init_Bolt.Blaster_Bolt(bullet_image,player\x#+4+ImageWidth(bullet_image)/2,player\y#-ImageHeight(player\image)/2,600)
		
		player\blaster_current_charge = player\blaster_current_charge - player\blast_energy_per_shot
		player\shoot_timer = MilliSecs()
		player\can_shoot = False
		
	EndIf
	
	
	If (player\vector2#[0] <> 0) And (player\vector2#[1] <> 0) Then
		magnitude# = Sqr(player\vector2#[0]^2 + player\vector2#[1]^2)
		player\vector2#[0] = player\vector2#[0] / magnitude#
		player\vector2#[1] = player\vector2#[1] / magnitude#
	EndIf
End Function
Function Blaster_Recharge(player.Player,delta_time#)
	If player\blaster_current_charge# < player\blaster_max_charge# Then
		player\blaster_current_charge# = player\blaster_current_charge# + player\blaster_recharge_rate# * delta_time#
		If player\blaster_current_charge# > player\blaster_max_charge# Then
			player\blaster_current_charge# = player\blaster_max_charge
		EndIf
		
	EndIf
	
End Function
Function Blaster_Timer(player.Player)
	
	If (Not player\can_shoot) Then
		If MilliSecs() > player\shoot_timer + player\cooldown_duration Then
			StopChannel player\blast_sound_channel
			player\can_shoot = True
		EndIf
	EndIf
End Function
Function Update_Player(player.Player,delta_time#,bullet_image,player_blaster_sound)
	Get_Player_Input(player,bullet_image,player_blaster_sound)
	Blaster_Timer(player)
	Blaster_Recharge(player,delta_time#)
	player\x# = player\x# + (player\speed# * player\vector2#[0] * delta_time#)
	player\y# = player\y# + (player\speed# * player\vector2#[1] * delta_time#)
End Function
Function Draw_Player(player.Player)
	DrawImage player\image,player\x#,player\y#
End Function

Dim colors(0,2)
colors(0,0) = 58
colors(0,1) = 42
colors(0,2) = 63
ClsColor colors(0,0),colors(0,1),colors(0,2)
.start_point
PlaySound(ready_girl_vs)
Const MAX_STARS = 20
For star_num = 0 To MAX_STARS-1
	star.Star = Init_Star(star_image,Rand(0,SCREEN_WIDTH-50),Rand(0,SCREEN_HEIGHT-50),40)
Next


player.Player = Init_Player.Player(player_image,SCREEN_WIDTH / 2,SCREEN_HEIGHT / 2, 400)

bgm_channel = PlayMusic(SOUND_PATH$+"Echoes_Of_The_Past.mp3")
ChannelVolume bgm_channel,0.15
asteroid_timer = MilliSecs()
asteroid_spawn_interval = 750
start_time = MilliSecs()
player_main_score = MilliSecs()
player_shot_score = 0
asteroid_shot_value = 100

frame_count = 1
frame_timer# = MilliSecs()
duration# = 1000
delta_time# = 0.0
current_frames = 0
Global hit_flag = False
Global last_hit_time = MilliSecs() - MilliSecs()
Global game_state = 1
Const ESC_KEY = 1
run = True
While run
	If KeyHit(ESC_KEY) Then
		End
	EndIf
	
	If Not ChannelPlaying(bgm_channel) Then
		bgm_channel = PlayMusic(SOUND_PATH$+"Echoes_Of_The_Past.mp3")
		ChannelVolume bgm_channel,0.15
	EndIf
;	WaitTimer(fps_timer)
	Select game_state
		Case 1
			Cls
			If MilliSecs() >= frame_timer# + duration Then
				current_frames = frame_count
				delta_time# = 1.0 / current_frames
				current_frames = 0
				frame_count = 0
				frame_timer# = MilliSecs()
			EndIf
			
			Create_More_Stars(star_image)
			asteroid_timer = Asteroid_Spawner(asteroid_timer,asteroid_spawn_interval)
			
			For star.Star = Each Star
				Update_Star(star,delta_time#)
				
				Draw_Star(star)
			Next
			
			player_shot_score = Bolt_Asteroid_Collision(explosion_sound,player_shot_score,asteroid_shot_value)
			
			For asteroid.Asteroid = Each Asteroid
				Update_Asteroid(asteroid,delta_time#)
				Player_Asteroid_Collision(player,asteroid,player_damage_sound)
				Draw_Asteroid(asteroid)
			Next
			
			
			For bolt.Blaster_Bolt = Each Blaster_Bolt
				Update_Bolt(bolt,delta_time#)
				
				
				Draw_Bolt(bolt)
			Next
			
			For e.Explosion = Each Explosion
				Update_Explosion(e,delta_time#)
				Draw_Explosion(e)
			Next
			
			
			Update_Player(player,delta_time#,bullet_image,player_blaster_sound)
			Draw_Player(player)
			
			
			Show_Player_Armor_Points(player,font_normal_50,font_small_25)
			player_main_score = Show_Player_Score(player_main_score,font_normal_50,start_time,font_small_25,player_shot_score,asteroid_shot_value)
			
			Show_Player_Weapon_Energy(player,font_small_25)
			
			Check_Player_Health(player)
			frame_count = frame_count + 1
			Flip
			
			
			
		Case -1
			Game_Over_Screen(font_large_100,font_normal_50)
		Case 0
			run = False
	End Select
Wend
FlushKeys
Goto start_point
WaitKey
End

Function Create_More_Stars(star_image)
	If star_count < MAX_STARS Then
		For star_num = 0 To MAX_STARS-1
			
			star.Star = Init_Star.Star(star_image,Rand(0,SCREEN_WIDTH-50),Rand(-SCREEN_HEIGHT,-50),40)
		Next
	EndIf
End Function

Function Asteroid_Spawner(asteroid_timer,asteroid_spawn_interval)
	If MilliSecs() > asteroid_timer + asteroid_spawn_interval Then
		asteroid.Asteroid = Init_Asteroid.Asteroid(Rand(0,SCREEN_WIDTH),Rand(-200,-100),Rand(400,500))
		asteroid_timer = MilliSecs()
	EndIf
	
	Return asteroid_timer
End Function

Function Bolt_Asteroid_Collision(explosion_sound,player_shot_score,asteroid_shot_value)
	For bolt.Blaster_Bolt = Each Blaster_Bolt
		For a_roid.Asteroid = Each Asteroid
			If ImagesCollide(a_roid\image,a_roid\x#,a_roid\y#,0,bolt\image,bolt\x#,bolt\y#,0) Then		
				PlaySound(explosion_sound)
				player_shot_score = player_shot_score + asteroid_shot_value
				hit_flag = True
				last_hit_time = MilliSecs()
				e.Explosion = Init_Explosion.Explosion(a_roid\x#,a_roid\y#,40)
				Delete a_roid
				Delete bolt
;				asteroid_count = asteroid_count - 1
				Exit
			EndIf
		Next
	Next
	Return player_shot_score
End Function

Function Player_Asteroid_Collision(player.Player,asteroid.Asteroid,player_damage_sound)
	If asteroid <> Null Then
		If ImagesCollide(asteroid\image,asteroid\x#,asteroid\y#,0,player\image,player\x#,player\y#,0) Then
			Delete asteroid
			player\armor_points = player\armor_points - 1
			PlaySound(player_damage_sound)
			PlaySound(girly_hurt_sounds(Rand(0,2)))
		EndIf
	EndIf
End Function

Function Game_Over_Screen(font_large_100,font_normal_50)
	Cls
	Delete_All_Sprites()
	SetFont font_large_100
	Color 255,255,255
	g_o_text$ = "Game Over"
	Text SCREEN_WIDTH/2,SCREEN_HEIGHT/2,g_o_text$,True,True
	
	SetFont font_normal_50
	Color 255,255,255
	prompt_text$ = "Press <RETURN> to continue or <ESCAPE> to exit the game"
	Text SCREEN_WIDTH/2,SCREEN_HEIGHT/2+ 100,prompt_text$,True,True
	
	If KeyHit(RETURN_KEY) Then
		game_state = 0
	ElseIf KeyHit(ESC_KEY) Then
		End
	EndIf
	Flip
End Function

Function Check_Player_Health(player.Player)
	If player\armor_points <= 0 Then
		game_state = -1
	EndIf
End Function

Function Delete_All_Sprites()
	For p.Player = Each Player
		Delete p
	Next
	For a.Asteroid = Each Asteroid
		Delete a
	Next
	For b.Blaster_Bolt = Each Blaster_Bolt
		Delete b
	Next
	For s.Star = Each Star
		Delete s
	Next
End Function

Function Show_Player_Score(player_main_score,font_normal_50,start_time,font_small_25,player_shot_score,asteroid_shot_value)
	player_main_score = (MilliSecs() - start_time) / 100
	player_combined_score = player_main_score + player_shot_score
	
	SetFont font_normal_50
	Color 255,255,255
	score_text$ = Str(player_combined_score)
	Text SCREEN_WIDTH/2,SCREEN_HEIGHT-50,score_text$,True,True
	
	Color 255,255,255
	Rect SCREEN_WIDTH/2-StringWidth(score_text$)/2 -5,SCREEN_HEIGHT-50-StringHeight(score_text$)/2,StringWidth(score_text$) + 13,StringHeight(score_text$),False
	Rect SCREEN_WIDTH/2-StringWidth(score_text$)/2 -4,SCREEN_HEIGHT-50-StringHeight(score_text$)/2+1,StringWidth(score_text$) + 13,StringHeight(score_text$),False
	
	SetFont font_small_25
	Color 255,255,255
	label_text$ = "Score"
	Text SCREEN_WIDTH/2+50,SCREEN_HEIGHT-72/2,label_text$,False,False
	
	If hit_flag Then
		SetFont font_small_25
		Color 255,255,255
		Text SCREEN_WIDTH/2 +StringWidth(score_text$)+10,SCREEN_HEIGHT-75,"+"+Str(asteroid_shot_value)
	EndIf
	If MilliSecs() > last_hit_time + 750 Then
		hit_flag = False
	EndIf
	
	Return player_main_score
End Function

Function Show_Player_Armor_Points(player.Player,font_normal_50,font_small_25)
	SetFont font_normal_50
	Color 255,255,255
	armor_text$ = Str(player\armor_points)
	Text 50,SCREEN_HEIGHT-50,armor_text$,True,True
	
	
	Color 255,255,255
	Rect 49-StringWidth(armor_text$)/2 -5,SCREEN_HEIGHT-52-StringHeight(armor_text$)/2,StringWidth(armor_text$) + 13,StringHeight(armor_text$),False
	Rect 48-StringWidth(armor_text$)/2 -5,SCREEN_HEIGHT-51-StringHeight(armor_text$)/2,StringWidth(armor_text$) + 15,StringHeight(armor_text$),False
	
	SetFont font_small_25
	Color 255,255,255
	label_text$ = "Armor Points"
	Text 75,SCREEN_HEIGHT-75/2,label_text$,False,False
	
End Function

Function Show_Player_Weapon_Energy(player.Player,font_small_25)
	Color 250,0,50
	Rect SCREEN_WIDTH-400,SCREEN_HEIGHT-75,200*(player\blaster_current_charge/player\blaster_max_charge),50,True
	SetFont font_small_25
	Color 255,255,255
	w_e_text$ = "Blast Energy"
	Text SCREEN_WIDTH-400/2-200,SCREEN_HEIGHT-75/2,w_e_text$,False,False
End Function


;~IDEal Editor Parameters:
;~F#3A#40#4A#52#5D#62#6B#74#7C#8A#A3#C6#CD#D2#DA#E1#E7#F6#107#11D
;~F#127#130#137#1AB#1B4#1BD#1D0#1DB#1F0#1F6#205#223#235
;~C#Blitz3D