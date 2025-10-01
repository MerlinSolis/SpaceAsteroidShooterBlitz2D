AppTitle "Placing Objects"
Const SCREEN_WIDTH = 1280 : Const SCREEN_HEIGHT = 720
Graphics SCREEN_WIDTH,SCREEN_HEIGHT,0,2
SetBuffer BackBuffer()
Const FPS_CAP = 60
fps_timer = CreateTimer(FPS_CAP)
TFormFilter False
SeedRnd MilliSecs()

Const FILE_PATH$ = "../images/"

laser_blast_img = LoadImage(FILE_PATH$+"laser.png")
laser_blast_h = ImageHeight(laser_blast_img)
laser_blast_pos_x = 20
laser_blast_pos_y = SCREEN_HEIGHT - laser_blast_h - 20

meteor_img = LoadImage(FILE_PATH$+"meteor.png")
MidHandle meteor_img
meteor_pos_x = SCREEN_WIDTH / 2
meteor_pos_y = SCREEN_HEIGHT / 2



player_img = LoadImage(FILE_PATH$+"player.png")
player_img_w = ImageWidth(player_img)
player_img_h = ImageHeight(player_img)
player_pos_x# = 100 - player_img_w/2
player_pos_y# = 100 - player_img_h/2

player_dir = 1
player_speed = 100

star_img = LoadImage(FILE_PATH$+"star2.png")
star_img_w = ImageWidth(star_img)
star_img_h = ImageHeight(star_img)
MaskImage star_img,255,0,110
Dim star_pos_list(19,1)
For star_num = 0 To 19
	star_pos_list(star_num,0) = Rand(star_img_w,SCREEN_WIDTH-star_img_w)
	star_pos_list(star_num,1) = Rand(star_img_h,SCREEN_HEIGHT-star_img_h)
Next

frame_count = 0
frame_timer = MilliSecs()
duration = 1000 ; ms = 1s
current_frames = 0
dt# = 0
ClsColor 0,42,64
Const ESC_KEY = 1
run = True
While run
	; calculate fps and dt
	frame_count = frame_count + 1
	If MilliSecs() >= frame_timer + duration Then
		current_frames = frame_count
		dt# = 1.0/current_frames
		frame_count = 0
		frame_timer = MilliSecs()
	EndIf
	
	If KeyHit(ESC_KEY) Then
		run = False
	EndIf
	Cls
	WaitTimer(fps_timer)
	
	For star_num = 0 To 19
		DrawImage star_img,star_pos_list(star_num,0),star_pos_list(star_num,1)
	Next
	
	player_pos_x# = player_pos_x# + (player_speed * player_dir)*dt#
	
;	If (player_pos_x + player_img_w) > SCREEN_WIDTH Then
;		player_dir = -1
;	ElseIf player_pos_x < 0 Then
;		player_dir = 1
;	EndIf
	
	If (player_pos_x + player_img_w) > SCREEN_WIDTH Or player_pos_x < 0 Then
		player_dir = player_dir * -1
	EndIf
	
	DrawImage laser_blast_img,laser_blast_pos_x,laser_blast_pos_y
	DrawImage meteor_img,meteor_pos_x,meteor_pos_y
	
	DrawImage player_img,player_pos_x,player_pos_y
	
	
	
	
	Text 10,10,dt#
	Text 10,30,current_frames
	
	
	
	Flip
	
Wend
FlushKeys
WaitKey
End

;~IDEal Editor Parameters:
;~C#Blitz3D