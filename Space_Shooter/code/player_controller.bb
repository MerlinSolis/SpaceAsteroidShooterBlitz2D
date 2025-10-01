apptitle "Player Controller Input"
const SCREEN_WIDTH = 1280 : const SCREEN_HEIGHT = 720
graphics SCREEN_WIDTH,SCREEN_HEIGHT,0,2
setbuffer backbuffer()
const FPS_CAP = 60
fps_timer = createtimer(FPS_CAP)


const W_KEY = 17 : const S_KEY = 31 : const A_KEY = 30 : const D_KEY = 32

const IMAGE_PATH$ = "../images/"
player_image = loadimage(IMAGE_PATH$+"player.png")
midhandle player_image
global player_vector2#[1]
player_vector2[0] = 0.0
player_vector2[1] = 0.0
player_speed# = 300
player_x# = SCREEN_WIDTH / 2
player_y# = SCREEN_HEIGHT / 2

frame_count = 1
frame_timer = millisecs()
duration = 1000 ;ms
delta_time# = 0.0
current_frames = 0
clscolor 0,42,64
const ESC_KEY = 1
run = True 
while run 
    if millisecs() >= frame_timer + duration
        current_frames = frame_count
        delta_time# = 1.0 / current_frames
        frame_count = 0
        frame_timer = millisecs()
    endif
    waittimer(fps_timer)
    if keyhit(ESC_KEY) then 
        run = False 
    endif
    cls

    player_vector2[0] = keydown(D_KEY) - keydown(A_KEY)
    player_vector2[1] = keydown(S_KEY) - keydown(W_KEY)

    text 10,10,delta_time#
    text 10,30,current_frames
    ; magnitude# = sqr((player_vector2[0]*player_speed)^2 + (player_vector2[1]*player_speed)^2)
    ; if player_vector2[0] <> 0 and player_vector2[1] <> 0 then 
    ;     player_x = player_x + (player_vector2[0]*player_speed#)/magnitude# * delta_time# *player_speed#
    ;     player_y = player_y + (player_vector2[1]*player_speed#)/magnitude# * delta_time# *player_speed#
    ; else 
    ;     player_x = player_x + (player_vector2[0] * player_speed# * delta_time#)
    ;     player_y = player_y + (player_vector2[1] * player_speed# * delta_time#)
    ; endif

    if player_vector2[0] <> 0 and player_vector2[0] <> 0 then   
        magnitude# = sqr((player_vector2[0]^2 + (player_vector2[1]^2)))
        player_x = player_x + (player_vector2[0] / magnitude#) * delta_time# * player_speed#
        player_y = player_y + (player_vector2[1] / magnitude#) * delta_time# * player_speed#
        ; new_magnitude# = sqr((player_vector2[0]/magnitude#)^2 + (player_vector2[1]/magnitude#)^2)
        text 10,50,player_vector2[0]/magnitude#
        text 10,70,player_vector2[1]/magnitude#
    else 
        player_x = player_x + (player_vector2[0]) * delta_time# * player_speed#
        player_y = player_y + (player_vector2[1]) * delta_time# * player_speed#
        text 10,50,player_vector2[0]
        text 10,70,player_vector2[1]
        ; new_magnitude# = 0
    endif
    

    drawimage player_image,player_x,player_y

    ; text 10,50,magnitude 
    ; text 10,70,new_magnitude
   
    
    frame_count = frame_count + 1
    flip 
wend 
flushkeys 
waitkey 
end 
