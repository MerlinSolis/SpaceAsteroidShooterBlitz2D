AppTitle "20 Random Stars"
Const SCREEN_WIDTH = 1280 : Const SCREEN_HEIGHT = 720
Graphics SCREEN_WIDTH,SCREEN_HEIGHT,0,2
Const FPS_CAP = 60
fps_timer = CreateTimer(FPS_CAP)
SetBuffer BackBuffer()
SeedRnd MilliSecs()

Const ASSET_PATH$ = "../images/"

star_img = LoadImage(ASSET_PATH$+"star2.png")
MaskImage star_img,255,0,110

bg_image = Create_Star_BG(star_img)


ClsColor 0,0,0
Const ESC_KEY=1
run = True
While run
	If KeyHit(ESC_KEY) Then
		run = False
	EndIf
	Cls
	WaitTimer(fps_timer)
	
	
	DrawImage bg_image,0,0
	

	Flip
Wend
FlushKeys
WaitKey
End


Function Create_Star_BG(star_img)
	ClsColor 0,42,64
	width = ImageWidth(star_img)
	height = ImageHeight(star_img)
	img_surf = CreateImage(SCREEN_WIDTH,SCREEN_HEIGHT)
	SetBuffer ImageBuffer(img_surf)
	Cls
	For star_num = 0 To 19
		rand_x = Rand(width,SCREEN_WIDTH-width)
		rand_y = Rand(height,SCREEN_HEIGHT-height)
		DrawImage star_img,rand_x,rand_y
	Next
	SetBuffer BackBuffer()
	Return img_surf
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D